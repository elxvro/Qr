package com.elxvro.scan

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var content: FrameLayout
    private lateinit var nav: BottomNavigationView
    private lateinit var store: ScanStore
    private val scanner by lazy { BarcodeScanning.getClient() }
    private val executor = Executors.newSingleThreadExecutor()
    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var busy = false
    private var dialogOpen = false
    private var historyFilter = HistoryFilter.ALL

    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { showScan() }
    private val gallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) scanGallery(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = ScanStore(getSharedPreferences("elxvro_scan", MODE_PRIVATE))
        setContentView(shell())
        nav.selectedItemId = SCAN
    }

    override fun onDestroy() {
        provider?.unbindAll(); scanner.close(); executor.shutdown(); super.onDestroy()
    }

    private fun shell(): View {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg()) }
        content = FrameLayout(this)
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        nav = BottomNavigationView(this).apply {
            setBackgroundColor(surface())
            menu.add(0, SCAN, 0, "Tara").setIcon(android.R.drawable.ic_menu_camera)
            menu.add(0, CREATE, 1, "Oluştur").setIcon(android.R.drawable.ic_menu_edit)
            menu.add(0, HISTORY, 2, "Geçmiş").setIcon(android.R.drawable.ic_menu_recent_history)
            menu.add(0, SETTINGS, 3, "Ayarlar").setIcon(android.R.drawable.ic_menu_preferences)
            setOnItemSelectedListener {
                when (it.itemId) { SCAN -> showScan(); CREATE -> showCreate(); HISTORY -> showHistory(); SETTINGS -> showSettings() }
                true
            }
        }
        root.addView(nav, LinearLayout.LayoutParams(-1, dp(70)))
        return root
    }

    private fun showScan() {
        stopCamera()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val box = column(Gravity.CENTER)
            box.addView(title("Kamera izni gerekli"))
            box.addView(note("Canlı QR ve barkod taraması için kamera izni verin."))
            box.addView(button("Kamera İzni Ver") { permission.launch(Manifest.permission.CAMERA) }, params(12))
            box.addView(button("Galeriden Oku", false) { gallery.launch("image/*") }, params(10))
            put(ScrollView(this).apply { addView(box) })
            return
        }
        val root = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        val preview = PreviewView(this).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
        root.addView(preview, FrameLayout.LayoutParams(-1, -1))
        root.addView(ScannerOverlayView(this), FrameLayout.LayoutParams(-1, -1))
        root.addView(TextView(this).apply {
            text = "ELXVRO Scan\nQR veya barkodu çerçeveye getirin"; textSize = 18f; setTextColor(Color.WHITE)
            setPadding(dp(18), dp(18), dp(18), dp(8)); setShadowLayer(8f, 0f, 2f, Color.BLACK)
        }, FrameLayout.LayoutParams(-1, dp(90), Gravity.TOP))
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(14), dp(8), dp(14), dp(8)); background = rounded(Color.argb(190,7,17,31),18) }
        row.addView(button("Galeri", false) { gallery.launch("image/*") }, LinearLayout.LayoutParams(0, dp(50), 1f).apply { marginEnd=dp(8) })
        var torch = false
        row.addView(button("Fener", false) { v ->
            if (camera?.cameraInfo?.hasFlashUnit() == true) { torch = !torch; camera?.cameraControl?.enableTorch(torch); (v as MaterialButton).text = if(torch) "Fener Açık" else "Fener" }
            else toast("Flaş bulunamadı")
        }, LinearLayout.LayoutParams(0, dp(50), 1f))
        root.addView(row, FrameLayout.LayoutParams(-1, dp(68), Gravity.BOTTOM).apply { leftMargin=dp(18); rightMargin=dp(18); bottomMargin=dp(18) })
        put(root); startCamera(preview)
    }

    private fun startCamera(view: PreviewView) {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            runCatching {
                val p = future.get(); provider = p; p.unbindAll()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
                val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                analysis.setAnalyzer(executor) { proxy -> analyze(proxy) }
                camera = p.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            }.onFailure { toast("Kamera başlatılamadı") }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyze(proxy: ImageProxy) {
        if (busy || dialogOpen) { proxy.close(); return }
        val media = proxy.image ?: run { proxy.close(); return }
        busy = true
        scanner.process(InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees))
            .addOnSuccessListener { codes -> codes.firstOrNull()?.let(::found) }
            .addOnCompleteListener { busy=false; proxy.close() }
    }

    private fun scanGallery(uri: Uri) {
        runCatching { InputImage.fromFilePath(this, uri) }.onSuccess { image ->
            scanner.process(image).addOnSuccessListener { list -> list.firstOrNull()?.let(::found) ?: toast("Kod bulunamadı") }
                .addOnFailureListener { toast("Görsel okunamadı") }
        }.onFailure { toast("Görsel açılamadı") }
    }

    private fun found(code: Barcode) {
        val raw = code.rawValue.orEmpty(); if (raw.isBlank() || dialogOpen) return
        runOnUiThread {
            dialogOpen = true; vibrate()
            val kind = if (code.format == Barcode.FORMAT_QR_CODE) "QR" else "Barkod"
            val format = format(code.format); val item = store.add(raw, format, kind)
            val box = column().apply { setPadding(dp(22),dp(18),dp(22),dp(8)) }
            box.addView(title(type(code.valueType))); box.addView(note("$format\n$raw"))
            AlertDialog.Builder(this).setView(box)
                .setPositiveButton(if(kind=="Barkod") "Web'de Ara" else "Aç") { _,_-> open(raw, kind) }
                .setNeutralButton("Kopyala") { _,_-> copy(raw) }
                .setNegativeButton("Favori") { _,_-> store.toggleFavorite(item.id) }
                .create().apply { setOnDismissListener { dialogOpen=false }; show() }
        }
    }

    private fun showCreate() {
        stopCamera()
        val c = column()
        c.addView(title("QR Kod Oluştur"))
        c.addView(note("URL, metin, Wi‑Fi, telefon, e-posta veya kişi bilgisi için QR üretin; görseli kaydedin ya da paylaşın."))

        val types = arrayOf("URL","Metin","Wi-Fi","Telefon","E-posta","Kişi")
        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, types)
        }
        val main = input("Web adresi")
        val extra = input("Ek bilgi").apply { visibility = View.GONE }
        val image = ImageView(this).apply {
            adjustViewBounds = true
            visibility = View.GONE
            setBackgroundColor(Color.WHITE)
            setPadding(dp(14),dp(14),dp(14),dp(14))
        }
        val payloadPreview = note("").apply { visibility = View.GONE }
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; visibility = View.GONE }
        var generated: Bitmap? = null
        var payload = ""

        fun configureFields(position: Int) {
            extra.text.clear()
            when (types[position]) {
                "URL" -> { main.hint = "Web adresi"; extra.visibility = View.GONE }
                "Metin" -> { main.hint = "Metin"; extra.visibility = View.GONE }
                "Wi-Fi" -> { main.hint = "Wi‑Fi adı (SSID)"; extra.hint = "Wi‑Fi şifresi"; extra.visibility = View.VISIBLE }
                "Telefon" -> { main.hint = "Telefon numarası"; extra.visibility = View.GONE }
                "E-posta" -> { main.hint = "E-posta adresi"; extra.visibility = View.GONE }
                "Kişi" -> { main.hint = "Ad soyad"; extra.hint = "Telefon"; extra.visibility = View.VISIBLE }
            }
            generated = null
            payload = ""
            image.visibility = View.GONE
            payloadPreview.visibility = View.GONE
            actions.visibility = View.GONE
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = configureFields(position)
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        actions.addView(button("Paylaş", false) {
            generated?.let { bmp -> runCatching { QrImageActions.share(this, bmp) }.onFailure { toast("QR paylaşılamadı") } }
        }, LinearLayout.LayoutParams(0,dp(48),1f).apply { marginEnd=dp(8) })
        actions.addView(button("Kaydet", false) {
            generated?.let { bmp ->
                runCatching { QrImageActions.save(this, bmp) }
                    .onSuccess(::toast)
                    .onFailure { toast("QR kaydedilemedi") }
            }
        }, LinearLayout.LayoutParams(0,dp(48),1f))

        c.addView(spinner, params(12,58))
        c.addView(main, params(10,58))
        c.addView(extra, params(10,58))
        c.addView(button("QR Kod Oluştur") {
            val value = main.text.toString()
            if (value.isBlank()) main.error = "Bu alan gerekli" else {
                payload = QrPayloadBuilder.build(spinner.selectedItem.toString(), value, extra.text.toString())
                generated = QrCodeUtil.create(payload)
                image.setImageBitmap(generated)
                image.visibility = View.VISIBLE
                payloadPreview.text = "Kod içeriği: $payload"
                payloadPreview.visibility = View.VISIBLE
                actions.visibility = View.VISIBLE
            }
        }, params(12))
        c.addView(image, LinearLayout.LayoutParams(-1, dp(340)).apply { topMargin=dp(14) })
        c.addView(payloadPreview, LinearLayout.LayoutParams(-1,-2).apply { topMargin=dp(8) })
        c.addView(actions, LinearLayout.LayoutParams(-1,dp(48)).apply { topMargin=dp(10) })
        c.addView(button("İçeriği Kopyala", false) {
            if (payload.isBlank()) toast("Önce QR kod oluşturun") else copy(payload)
        }, params(10))
        put(scroll(c))
    }

    private fun showHistory(filter: HistoryFilter = historyFilter) {
        historyFilter = filter
        stopCamera()
        val c = column()
        c.addView(title("Geçmiş ve Favoriler"))
        c.addView(note("Taradığınız kodları yönetin, favorileyin veya tek tek silin."))

        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(
            "Tümü" to HistoryFilter.ALL,
            "Favori" to HistoryFilter.FAVORITES,
            "QR" to HistoryFilter.QR,
            "Barkod" to HistoryFilter.BARCODE
        ).forEachIndexed { index, pair ->
            tabs.addView(
                button(pair.first, pair.second == historyFilter) { showHistory(pair.second) },
                LinearLayout.LayoutParams(0, dp(44), 1f).apply { if (index > 0) marginStart = dp(6) }
            )
        }
        c.addView(tabs, params(12,44))

        val all = store.list()
        val visible = HistoryLogic.filter(all, historyFilter)
        c.addView(note("${visible.size} kayıt • ${all.count { it.favorite }} favori"))

        if (visible.isEmpty()) {
            c.addView(note(if (historyFilter == HistoryFilter.FAVORITES) "Henüz favori kayıt yok." else "Bu bölümde kayıt yok."))
        } else visible.forEach { item ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14),dp(12),dp(14),dp(12))
                background = rounded(surface(),14)
            }
            card.addView(TextView(this).apply {
                text = "${if(item.favorite) "★ " else ""}${item.kind} • ${item.format}\n${item.value}\n${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(item.time))}"
                textSize=15f; setTextColor(fg()); setTextIsSelectable(true)
                setOnClickListener { open(item.value,item.kind) }
            })
            val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0,dp(10),0,0) }
            actions.addView(button(if(item.favorite) "★ Favori" else "☆ Favori", false) {
                store.toggleFavorite(item.id); showHistory(historyFilter)
            }, LinearLayout.LayoutParams(0,dp(42),1f).apply { marginEnd=dp(6) })
            actions.addView(button("Aç", false) { open(item.value,item.kind) }, LinearLayout.LayoutParams(0,dp(42),1f).apply { marginEnd=dp(6) })
            actions.addView(button("Sil", false) { confirmDelete(item) }, LinearLayout.LayoutParams(0,dp(42),1f))
            card.addView(actions)
            c.addView(card, LinearLayout.LayoutParams(-1,-2).apply { topMargin=dp(9) })
        }
        put(scroll(c))
    }

    private fun confirmDelete(item: ScanItem) {
        AlertDialog.Builder(this)
            .setTitle("Kaydı sil")
            .setMessage("Bu tarama geçmişten silinsin mi?")
            .setPositiveButton("Sil") { _, _ -> store.delete(item.id); showHistory(historyFilter) }
            .setNegativeButton("Vazgeç", null)
            .show()
    }

    private fun showSettings() {
        stopCamera(); val prefs=getSharedPreferences("elxvro_scan",MODE_PRIVATE); val c=column(); c.addView(title("Ayarlar"))
        c.addView(switchRow("Titreşim","Tarama sonrası titreşim",prefs.getBoolean("vibrate",true)) { prefs.edit().putBoolean("vibrate",it).apply() }, params(12,68))
        c.addView(switchRow("Koyu tema","Uygulama renklerini değiştir",prefs.getBoolean("dark",true)) { prefs.edit().putBoolean("dark",it).apply(); recreate() }, params(10,68))
        c.addView(button("Geçmişi Temizle", false) { store.clear(); toast("Geçmiş temizlendi") }, params(12))
        c.addView(button("Wi‑Fi Ayarları", false) { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }, params(10))
        put(scroll(c))
    }

    private fun switchRow(a:String,b:String,checked:Boolean,on:(Boolean)->Unit):View {
        val r=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL; setPadding(dp(14),dp(8),dp(12),dp(8)); background=rounded(surface(),14) }
        val t=TextView(this).apply { text="$a\n$b"; textSize=14f; setTextColor(fg()) }; r.addView(t,LinearLayout.LayoutParams(0,-2,1f))
        r.addView(SwitchMaterial(this).apply { isChecked=checked; setOnCheckedChangeListener { _,v->on(v) } }); return r
    }

    private fun open(value:String,kind:String) = runCatching {
        when {
            value.startsWith("http://",true)||value.startsWith("https://",true) -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(value)))
            value.startsWith("tel:",true) -> startActivity(Intent(Intent.ACTION_DIAL,Uri.parse(value)))
            value.startsWith("mailto:",true) -> startActivity(Intent(Intent.ACTION_SENDTO,Uri.parse(value)))
            value.startsWith("WIFI:",true) -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            kind=="Barkod" -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q=${Uri.encode(value)}")))
            else -> share(value)
        }
    }.onFailure { toast("İşlem açılamadı") }.let { Unit }

    private fun copy(v:String){ (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("ELXVRO Scan",v)); toast("Kopyalandı") }
    private fun share(v:String){ startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type="text/plain"; putExtra(Intent.EXTRA_TEXT,v) },"Paylaş")) }
    private fun vibrate(){ if(getSharedPreferences("elxvro_scan",MODE_PRIVATE).getBoolean("vibrate",true)){ val v=getSystemService(Context.VIBRATOR_SERVICE) as Vibrator; if(android.os.Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(60,VibrationEffect.DEFAULT_AMPLITUDE)) else @Suppress("DEPRECATION") v.vibrate(60) } }
    private fun stopCamera(){ provider?.unbindAll(); camera=null }

    private fun format(f:Int)=when(f){ Barcode.FORMAT_QR_CODE->"QR Code"; Barcode.FORMAT_EAN_13->"EAN-13"; Barcode.FORMAT_EAN_8->"EAN-8"; Barcode.FORMAT_UPC_A->"UPC-A"; Barcode.FORMAT_UPC_E->"UPC-E"; Barcode.FORMAT_CODE_128->"Code 128"; Barcode.FORMAT_CODE_39->"Code 39"; Barcode.FORMAT_DATA_MATRIX->"Data Matrix"; Barcode.FORMAT_PDF417->"PDF417"; Barcode.FORMAT_AZTEC->"Aztec"; else->"Barkod" }
    private fun type(t:Int)=when(t){ Barcode.TYPE_URL->"Web Sitesi"; Barcode.TYPE_PHONE->"Telefon"; Barcode.TYPE_EMAIL->"E-posta"; Barcode.TYPE_WIFI->"Wi‑Fi"; Barcode.TYPE_SMS->"SMS"; Barcode.TYPE_GEO->"Konum"; Barcode.TYPE_CONTACT_INFO->"Kişi"; Barcode.TYPE_PRODUCT->"Ürün Barkodu"; else->"Tarama Sonucu" }

    private fun put(v:View){ content.removeAllViews(); content.addView(v,FrameLayout.LayoutParams(-1,-1)) }
    private fun scroll(v:View)=ScrollView(this).apply { setBackgroundColor(bg()); addView(v) }
    private fun column(gravityValue:Int=Gravity.TOP)=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=gravityValue; setPadding(dp(18),dp(18),dp(18),dp(24)); setBackgroundColor(bg()) }
    private fun title(s:String)=TextView(this).apply { text=s; textSize=24f; setTextColor(fg()) }
    private fun note(s:String)=TextView(this).apply { text=s; textSize=14f; setTextColor(muted()); setPadding(0,dp(6),0,dp(6)); setTextIsSelectable(true) }
    private fun input(h:String)=EditText(this).apply { hint=h; setTextColor(fg()); setHintTextColor(muted()); inputType=InputType.TYPE_CLASS_TEXT; isSingleLine=true; setPadding(dp(14),dp(8),dp(14),dp(8)); background=rounded(surface(),14) }
    private fun button(s:String,primary:Boolean=true,click:(View)->Unit)=MaterialButton(this).apply { text=s; isAllCaps=false; cornerRadius=dp(14); setTextColor(if(primary) Color.WHITE else fg()); backgroundTintList=ColorStateList.valueOf(if(primary) Color.rgb(22,140,255) else surface()); setOnClickListener { click(it) } }
    private fun params(top:Int=0,height:Int=52)=LinearLayout.LayoutParams(-1,dp(height)).apply { topMargin=dp(top) }
    private fun rounded(c:Int,r:Int)=GradientDrawable().apply { shape=GradientDrawable.RECTANGLE; setColor(c); cornerRadius=dp(r).toFloat() }
    private fun dark()=getSharedPreferences("elxvro_scan",MODE_PRIVATE).getBoolean("dark",true)
    private fun bg()=if(dark()) Color.rgb(7,17,31) else Color.rgb(245,248,252)
    private fun surface()=if(dark()) Color.rgb(17,31,49) else Color.WHITE
    private fun fg()=if(dark()) Color.WHITE else Color.rgb(20,30,43)
    private fun muted()=if(dark()) Color.rgb(176,190,207) else Color.rgb(90,104,120)
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()

    companion object { const val SCAN=1; const val CREATE=2; const val HISTORY=3; const val SETTINGS=4 }
}
