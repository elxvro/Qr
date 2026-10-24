package com.elxvro.scan.scanner

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.view.OrientationEventListener
import android.view.Surface
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ExperimentalGetImage
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
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var orientationListener: OrientationEventListener? = null
    private var busy = false
    private var closed = false
    private var torchEnabled = false
    private var lowLightState = LowLightState()
    private var lastLumaSampleAtMs = Long.MIN_VALUE
    private var lastDetectionAtMs = 0L
    private var lastFocusAtMs = 0L

    fun start(
        owner: LifecycleOwner,
        previewView: PreviewView,
        initialTorch: Boolean = false,
        onResult: (Barcode) -> Unit,
        onZoomChanged: (Float) -> Unit = {},
        onLowLightChanged: (Boolean) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        if (closed) return
        val startedAt = SystemClock.elapsedRealtime()
        lastDetectionAtMs = startedAt
        lastFocusAtMs = startedAt
        lowLightState = LowLightState()
        lastLumaSampleAtMs = Long.MIN_VALUE

        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            runCatching {
                val cameraProvider = future.get()
                provider = cameraProvider
                cameraProvider.unbindAll()

                val initialRotation = previewView.display?.rotation ?: Surface.ROTATION_0
                val preview = Preview.Builder()
                    .setTargetRotation(initialRotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                        previewUseCase = it
                    }
                val analysis = ImageAnalysis.Builder()
                    .setTargetRotation(initialRotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysisUseCase = it }
                analysis.setAnalyzer(executor) { proxy ->
                    analyze(
                        proxy = proxy,
                        previewView = previewView,
                        onResult = onResult,
                        onLowLightChanged = onLowLightChanged
                    )
                }

                camera = cameraProvider.bindToLifecycle(
                    owner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
                attachGestures(previewView, onZoomChanged)
                attachOrientationTracking()
                if (initialTorch && hasFlash()) setTorch(true)
                onZoomChanged(currentZoom())
                previewView.postDelayed({
                    if (!closed && previewView.width > 0 && previewView.height > 0) {
                        focusAt(previewView, previewView.width / 2f, previewView.height / 2f)
                    }
                }, INITIAL_FOCUS_DELAY_MS)
            }.onFailure(onError)
        }, mainExecutor)
    }

    @androidx.annotation.OptIn(markerClass = [ExperimentalGetImage::class])
    private fun analyze(
        proxy: ImageProxy,
        previewView: PreviewView,
        onResult: (Barcode) -> Unit,
        onLowLightChanged: (Boolean) -> Unit
    ) {
        if (busy || closed) {
            proxy.close()
            return
        }

        val media = proxy.image ?: run {
            proxy.close()
            return
        }

        val now = SystemClock.elapsedRealtime()
        sampleLowLight(proxy, now, onLowLightChanged)

        busy = true
        val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener(mainExecutor) { codes ->
                val accepted = codes.firstOrNull { code ->
                    val raw = code.rawValue?.trim().orEmpty()
                    raw.isNotBlank() && deduplicator.shouldAccept(raw, SystemClock.elapsedRealtime())
                }

                val completedAt = SystemClock.elapsedRealtime()
                if (accepted != null) {
                    lastDetectionAtMs = completedAt
                    onResult(accepted)
                } else if (
                    FocusAssistPolicy.shouldRefocus(
                        nowMs = completedAt,
                        lastDetectionAtMs = lastDetectionAtMs,
                        lastFocusAtMs = lastFocusAtMs
                    )
                ) {
                    focusAt(previewView, previewView.width / 2f, previewView.height / 2f)
                }
            }
            .addOnCompleteListener {
                busy = false
                proxy.close()
            }
    }

    private fun sampleLowLight(
        proxy: ImageProxy,
        nowMs: Long,
        onLowLightChanged: (Boolean) -> Unit
    ) {
        if (!LumaSamplingPolicy.shouldSample(nowMs, lastLumaSampleAtMs)) return
        lastLumaSampleAtMs = nowMs

        val luma = estimateLuma(proxy)
        val previous = lowLightState
        val next = LowLightPolicy.update(previous, luma)
        lowLightState = next
        if (next.isLowLight != previous.isLowLight) {
            mainExecutor.execute { onLowLightChanged(next.isLowLight) }
        }
    }

    private fun estimateLuma(proxy: ImageProxy): Int {
        val plane = proxy.planes.firstOrNull() ?: return 255
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride.coerceAtLeast(1)
        val stepX = max(1, proxy.width / LUMA_SAMPLE_COLUMNS)
        val stepY = max(1, proxy.height / LUMA_SAMPLE_ROWS)

        var total = 0L
        var samples = 0
        var y = 0
        while (y < proxy.height) {
            val rowOffset = y * rowStride
            var x = 0
            while (x < proxy.width) {
                val index = rowOffset + x * pixelStride
                if (index in 0 until buffer.limit()) {
                    total += buffer.get(index).toInt() and 0xFF
                    samples += 1
                }
                x += stepX
            }
            y += stepY
        }
        return if (samples == 0) 255 else (total / samples).toInt()
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
        if (previewView.width <= 0 || previewView.height <= 0) return
        val point = previewView.meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()
        current.cameraControl.startFocusAndMetering(action)
        lastFocusAtMs = SystemClock.elapsedRealtime()
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
        previewView.setOnTouchListener { view, event ->
            scaleDetector.onTouchEvent(event)
            tapDetector.onTouchEvent(event)
            if (event.actionMasked == MotionEvent.ACTION_UP && event.pointerCount == 1) {
                view.performClick()
            }
            true
        }
    }

    private fun attachOrientationTracking() {
        orientationListener?.disable()
        orientationListener = object : OrientationEventListener(context) {
            private var lastRotation = -1

            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return
                val rotation = when {
                    orientation >= 315 || orientation < 45 -> Surface.ROTATION_0
                    orientation < 135 -> Surface.ROTATION_270
                    orientation < 225 -> Surface.ROTATION_180
                    else -> Surface.ROTATION_90
                }
                if (rotation == lastRotation) return
                lastRotation = rotation
                previewUseCase?.targetRotation = rotation
                analysisUseCase?.targetRotation = rotation
            }
        }.also { listener ->
            if (listener.canDetectOrientation()) listener.enable()
        }
    }

    fun stop() {
        orientationListener?.disable()
        orientationListener = null
        previewUseCase = null
        analysisUseCase = null
        provider?.unbindAll()
        camera = null
        torchEnabled = false
        busy = false
        lowLightState = LowLightState()
    }

    override fun close() {
        if (closed) return
        closed = true
        stop()
        scanner.close()
        executor.shutdown()
    }

    private companion object {
        const val LUMA_SAMPLE_COLUMNS = 24
        const val LUMA_SAMPLE_ROWS = 18
        const val INITIAL_FOCUS_DELAY_MS = 350L
    }
}
