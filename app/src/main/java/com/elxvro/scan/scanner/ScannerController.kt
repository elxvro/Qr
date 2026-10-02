package com.elxvro.scan.scanner

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.elxvro.scan.ScanDeduplicator
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class ScannerController(
    private val context: Context,
    cooldownMs: Long = 1500L
) : AutoCloseable {
    private val scanner = BarcodeScanning.getClient()
    private val executor = Executors.newSingleThreadExecutor()
    private val mainExecutor = ContextCompat.getMainExecutor(context)
    private val deduplicator = ScanDeduplicator(cooldownMs)

    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var busy = false
    private var closed = false
    private var torchEnabled = false

    fun start(
        owner: LifecycleOwner,
        previewView: PreviewView,
        initialTorch: Boolean = false,
        onResult: (Barcode) -> Unit,
        onZoomChanged: (Float) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        if (closed) return
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            runCatching {
                val cameraProvider = future.get()
                provider = cameraProvider
                cameraProvider.unbindAll()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(executor) { proxy -> analyze(proxy, onResult) }

                camera = cameraProvider.bindToLifecycle(
                    owner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
                attachGestures(previewView, onZoomChanged)
                if (initialTorch && hasFlash()) setTorch(true)
                onZoomChanged(currentZoom())
            }.onFailure(onError)
        }, mainExecutor)
    }

    private fun analyze(proxy: ImageProxy, onResult: (Barcode) -> Unit) {
        if (busy || closed) {
            proxy.close()
            return
        }
        val media = proxy.image ?: run {
            proxy.close()
            return
        }
        busy = true
        val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener(mainExecutor) { codes ->
                val accepted = codes.firstOrNull { code ->
                    val raw = code.rawValue?.trim().orEmpty()
                    raw.isNotBlank() && deduplicator.shouldAccept(raw, SystemClock.elapsedRealtime())
                }
                if (accepted != null) onResult(accepted)
            }
            .addOnCompleteListener {
                busy = false
                proxy.close()
            }
    }

    fun scanUri(
        uri: Uri,
        onResult: (Barcode) -> Unit,
        onEmpty: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        runCatching { InputImage.fromFilePath(context, uri) }
            .onFailure(onError)
            .onSuccess { image ->
                scanner.process(image)
                    .addOnSuccessListener(mainExecutor) { list ->
                        val code = list.firstOrNull { !it.rawValue.isNullOrBlank() }
                        if (code == null) onEmpty() else onResult(code)
                    }
                    .addOnFailureListener(mainExecutor, onError)
            }
    }

    fun hasFlash(): Boolean = camera?.cameraInfo?.hasFlashUnit() == true

    fun setTorch(enabled: Boolean): Boolean {
        val target = enabled && hasFlash()
        camera?.cameraControl?.enableTorch(target)
        torchEnabled = target
        return torchEnabled
    }

    fun toggleTorch(): Boolean = setTorch(!torchEnabled)

    fun currentZoom(): Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f

    fun setZoomRatio(ratio: Float): Float {
        val state = camera?.cameraInfo?.zoomState?.value ?: return 1f
        val target = max(state.minZoomRatio, min(ratio, state.maxZoomRatio))
        camera?.cameraControl?.setZoomRatio(target)
        return target
    }

    fun focusAt(previewView: PreviewView, x: Float, y: Float) {
        val current = camera ?: return
        val point = previewView.meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()
        current.cameraControl.startFocusAndMetering(action)
    }

    private fun attachGestures(previewView: PreviewView, onZoomChanged: (Float) -> Unit) {
        val scaleDetector = ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val value = setZoomRatio(currentZoom() * detector.scaleFactor)
                    onZoomChanged(value)
                    return true
                }
            }
        )
        val tapDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean = true
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    focusAt(previewView, e.x, e.y)
                    return true
                }
            }
        )
        previewView.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            tapDetector.onTouchEvent(event)
            true
        }
    }

    fun stop() {
        provider?.unbindAll()
        camera = null
        torchEnabled = false
        busy = false
    }

    override fun close() {
        if (closed) return
        closed = true
        stop()
        scanner.close()
        executor.shutdown()
    }
}
