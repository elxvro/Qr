package com.elxvro.scan.qrcard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.max
import kotlin.math.min

object ProfessionalCardRenderer {
    fun render(model: QrCardModel, qrBitmap: Bitmap, requestedLongEdge: Int): Bitmap {
        val preset = ProfessionalCardCatalog.find(model.backgroundPresetId)
        val size = QrCardOutputSizePolicy.resolve(requestedLongEdge, model.cardAspectRatio)
        val output = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val layout = ProfessionalCardLayoutEngine.resolve(size.width, size.height, preset.composition)
        val copy = QrCardCopyPolicy.resolve(model)
        val short = min(size.width, size.height).toFloat()

        drawScene(canvas, paint, preset, size.width.toFloat(), size.height.toFloat(), short)
        drawContentPanel(canvas, paint, preset, layout.panelRect.toRectF(), short)
        drawCopy(canvas, paint, preset, copy, layout, short)
        drawQrPlate(canvas, paint, preset, qrBitmap, layout.qrRect.toRectF(), short)
        drawCta(canvas, paint, preset, copy.cta, layout.ctaRect.toRectF(), short)
        drawMicroDetail(canvas, paint, preset, size.width.toFloat(), size.height.toFloat(), short)
        return output
    }

    private fun drawScene(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float) {
        paint.shader = LinearGradient(0f, 0f, width, height, preset.backgroundStartArgb, preset.backgroundEndArgb, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, width, height, paint)
        paint.shader = null

        when (preset.scene) {
            ProfessionalCardScene.FREE_ALPINE -> mountains(canvas, paint, preset, width, height, short, premium = false)
            ProfessionalCardScene.PRO_COAST -> mountains(canvas, paint, preset, width, height, short, premium = true)
            ProfessionalCardScene.FREE_COFFEE -> tabletop(canvas, paint, preset, width, height, short, dining = false)
            ProfessionalCardScene.PRO_DINING -> tabletop(canvas, paint, preset, width, height, short, dining = true)
            ProfessionalCardScene.FREE_NEON_TECH -> tech(canvas, paint, preset, width, height, short, premium = false)
            ProfessionalCardScene.PRO_TECH_LAUNCH -> tech(canvas, paint, preset, width, height, short, premium = true)
            ProfessionalCardScene.FREE_METRO -> skyline(canvas, paint, preset, width, height, short, corporate = false)
            ProfessionalCardScene.PRO_ARCHITECTURE -> skyline(canvas, paint, preset, width, height, short, corporate = true)
            ProfessionalCardScene.FREE_BOTANICAL -> botanical(canvas, paint, preset, width, height, short, wellness = false)
            ProfessionalCardScene.PRO_WELLNESS -> botanical(canvas, paint, preset, width, height, short, wellness = true)
            ProfessionalCardScene.FREE_STUDIO -> workspace(canvas, paint, preset, width, height, short, estate = false)
            ProfessionalCardScene.PRO_PROPERTY -> workspace(canvas, paint, preset, width, height, short, estate = true)
            ProfessionalCardScene.FREE_STAGE -> stage(canvas, paint, preset, width, height, short, fashion = false)
            ProfessionalCardScene.PRO_NIGHT -> stage(canvas, paint, preset, width, height, short, fashion = false, night = true)
            ProfessionalCardScene.PRO_FASHION -> stage(canvas, paint, preset, width, height, short, fashion = true)
            ProfessionalCardScene.FREE_INTERIOR -> interior(canvas, paint, preset, width, height, short, resort = false)
            ProfessionalCardScene.PRO_RESORT -> interior(canvas, paint, preset, width, height, short, resort = true)
            ProfessionalCardScene.FREE_CREATIVE -> creative(canvas, paint, preset, width, height, short, beauty = false)
            ProfessionalCardScene.PRO_BEAUTY -> creative(canvas, paint, preset, width, height, short, beauty = true)
            ProfessionalCardScene.FREE_FLOW -> flow(canvas, paint, preset, width, height, short)
        }

        when (preset.artVariant) {
            0 -> {
                paint.color = withAlpha(preset.accentArgb, 0.18f)
                canvas.drawCircle(width * .90f, height * .12f, short * .18f, paint)
            }
            1 -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = max(2f, short * .006f)
                paint.color = withAlpha(preset.accentArgb, .34f)
                canvas.drawRoundRect(RectF(width*.055f,height*.07f,width*.945f,height*.93f), short*.035f, short*.035f, paint)
                paint.style = Paint.Style.FILL
            }
            2 -> {
                paint.color = withAlpha(preset.accentArgb, .14f)
                val path = Path().apply { moveTo(0f,height*.82f); lineTo(width*.44f,height*.55f); lineTo(width*.68f,height); close() }
                canvas.drawPath(path, paint)
            }
            3 -> {
                paint.color = withAlpha(preset.accentArgb, .20f)
                repeat(3) { i -> canvas.drawCircle(width*(.80f+i*.06f), height*(.12f+i*.06f), short*(.045f+i*.018f), paint) }
            }
            else -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = max(2f, short*.005f)
                repeat(4) { i ->
                    paint.color = withAlpha(preset.accentArgb, .12f + i*.05f)
                    canvas.drawLine(width*(.08f+i*.08f), 0f, width*(.44f+i*.08f), height, paint)
                }
                paint.style = Paint.Style.FILL
            }
        }
    }

    private fun mountains(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, premium: Boolean) {
        val horizon = if (premium) .66f else .70f
        paint.color = withAlpha(0xFFFFFFFF.toInt(), if (premium) .11f else .16f)
        val far = Path().apply {
            moveTo(0f, height*horizon)
            lineTo(width*.20f, height*.43f)
            lineTo(width*.38f, height*.62f)
            lineTo(width*.57f, height*.35f)
            lineTo(width*.76f, height*.59f)
            lineTo(width, height*.42f)
            lineTo(width, height)
            lineTo(0f,height)
            close()
        }
        canvas.drawPath(far, paint)
        paint.color = withAlpha(0xFF08141D.toInt(), if (premium) .40f else .26f)
        val near = Path().apply {
            moveTo(0f,height*.78f); lineTo(width*.25f,height*.55f); lineTo(width*.48f,height*.75f); lineTo(width*.68f,height*.49f); lineTo(width,height*.72f); lineTo(width,height); lineTo(0f,height); close()
        }
        canvas.drawPath(near, paint)
        if (premium) {
            paint.color = withAlpha(preset.accentArgb,.50f)
            canvas.drawRect(0f,height*.82f,width,height*.835f,paint)
        }
    }

    private fun tabletop(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, dining: Boolean) {
        paint.color = withAlpha(0xFF160C08.toInt(), if (dining) .44f else .26f)
        canvas.drawRect(0f,height*.64f,width,height,paint)
        paint.color = withAlpha(0xFFFFFFFF.toInt(), if (dining) .15f else .20f)
        canvas.drawCircle(width*.82f,height*.34f,short*.15f,paint)
        paint.color = withAlpha(preset.accentArgb, if (dining) .72f else .52f)
        canvas.drawCircle(width*.82f,height*.34f,short*.085f,paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = max(3f,short*.009f)
        paint.color = withAlpha(0xFFFFFFFF.toInt(),.42f)
        canvas.drawCircle(width*.82f,height*.34f,short*.19f,paint)
        paint.style = Paint.Style.FILL
        if (dining) {
            paint.color = withAlpha(preset.accentArgb,.36f)
            canvas.drawRect(width*.71f,height*.12f,width*.725f,height*.54f,paint)
            canvas.drawRect(width*.90f,height*.12f,width*.915f,height*.54f,paint)
        }
    }

    private fun tech(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, premium: Boolean) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = max(1.5f,short*.004f)
        val step = short*(if (premium) .09f else .12f)
        var x = -step
        while (x < width+step) { paint.color = withAlpha(preset.accentArgb, if (premium) .15f else .10f); canvas.drawLine(x,0f,x+height*.22f,height,paint); x += step }
        var y = 0f
        while (y < height) { canvas.drawLine(0f,y,width,y,paint); y += step }
        paint.style = Paint.Style.FILL
        paint.shader = RadialGradient(width*.82f,height*.22f,short*.34f,withAlpha(preset.accentArgb,.42f),0x00000000,Shader.TileMode.CLAMP)
        canvas.drawCircle(width*.82f,height*.22f,short*.34f,paint)
        paint.shader = null
    }

    private fun skyline(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, corporate: Boolean) {
        paint.color = withAlpha(0xFF02070C.toInt(), if (corporate) .48f else .34f)
        val base = height*.86f
        repeat(9) { i ->
            val bw = width*(.055f + (i%3)*.012f)
            val left = width*(.48f + i*.06f)
            val top = height*(.20f + ((i*7)%5)*.09f)
            canvas.drawRect(left,top,left+bw,base,paint)
            paint.color = withAlpha(preset.accentArgb, if (corporate) .42f else .28f)
            canvas.drawRect(left+bw*.18f,top+bw*.2f,left+bw*.28f,base-bw*.2f,paint)
            paint.color = withAlpha(0xFF02070C.toInt(), if (corporate) .48f else .34f)
        }
        if (corporate) {
            paint.color = withAlpha(0xFFFFFFFF.toInt(),.10f)
            canvas.drawRect(width*.68f,0f,width*.76f,height,paint)
        }
    }

    private fun botanical(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, wellness: Boolean) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = max(3f,short*.008f)
        paint.color = withAlpha(preset.accentArgb, if (wellness) 0.44f else 0.34f)
        val stems = listOf(.76f to .18f, .84f to .28f, .90f to .12f)
        stems.forEach { (x,y) ->
            val p=Path().apply { moveTo(width*x,height); cubicTo(width*(x-.10f),height*.68f,width*(x+.03f),height*.42f,width*(x-.03f),height*y) }
            canvas.drawPath(p,paint)
            paint.style=Paint.Style.FILL
            repeat(3) { j ->
                paint.color=withAlpha(preset.accentArgb,.18f+j*.07f)
                canvas.drawOval(RectF(width*(x-.13f+j*.03f),height*(.38f+j*.14f),width*(x-.02f+j*.03f),height*(.47f+j*.14f)),paint)
            }
            paint.style=Paint.Style.STROKE
            paint.color=withAlpha(preset.accentArgb,.40f)
        }
        paint.style=Paint.Style.FILL
        if (wellness) {
            paint.color=withAlpha(0xFFFFFFFF.toInt(),.20f)
            canvas.drawCircle(width*.82f,height*.23f,short*.11f,paint)
        }
    }

    private fun workspace(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, estate: Boolean) {
        paint.color=withAlpha(0xFFFFFFFF.toInt(), if (estate) .10f else .22f)
        canvas.drawRect(width*.58f,height*.16f,width*.94f,height*.68f,paint)
        paint.color=withAlpha(preset.accentArgb,.28f)
        canvas.drawRect(width*.61f,height*.20f,width*.76f,height*.64f,paint)
        canvas.drawRect(width*.79f,height*.20f,width*.91f,height*.42f,paint)
        if (estate) {
            paint.style=Paint.Style.STROKE; paint.strokeWidth=max(2f,short*.006f); paint.color=withAlpha(preset.accentArgb,.56f)
            val roof=Path().apply { moveTo(width*.55f,height*.55f); lineTo(width*.75f,height*.32f); lineTo(width*.95f,height*.55f) }
            canvas.drawPath(roof,paint); paint.style=Paint.Style.FILL
        } else {
            paint.color=withAlpha(0xFF101820.toInt(),.22f)
            canvas.drawRect(width*.54f,height*.70f,width*.96f,height*.75f,paint)
        }
    }

    private fun stage(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, fashion: Boolean, night: Boolean = false) {
        paint.color=withAlpha(preset.accentArgb, if (fashion) .20f else .28f)
        val left=Path().apply { moveTo(width*.56f,0f); lineTo(width*.72f,height*.72f); lineTo(width*.84f,height*.72f); lineTo(width*.70f,0f); close() }
        canvas.drawPath(left,paint)
        paint.color=withAlpha(0xFFFFFFFF.toInt(),if (night) .12f else .09f)
        val right=Path().apply { moveTo(width*.92f,0f); lineTo(width*.78f,height*.76f); lineTo(width*.96f,height*.76f); lineTo(width,0f); close() }
        canvas.drawPath(right,paint)
        if (fashion) {
            paint.style=Paint.Style.STROKE; paint.strokeWidth=max(2f,short*.005f); paint.color=withAlpha(preset.accentArgb,.54f)
            canvas.drawRect(width*.62f,height*.12f,width*.91f,height*.82f,paint); paint.style=Paint.Style.FILL
        }
    }

    private fun interior(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, resort: Boolean) {
        paint.color=withAlpha(0xFFFFFFFF.toInt(), if (resort) .16f else .26f)
        canvas.drawRoundRect(RectF(width*.58f,height*.13f,width*.91f,height*.72f),short*.02f,short*.02f,paint)
        paint.color=withAlpha(preset.accentArgb,.20f)
        canvas.drawRect(width*.61f,height*.17f,width*.74f,height*.69f,paint)
        paint.color=withAlpha(0xFF101820.toInt(),if (resort) .22f else .12f)
        canvas.drawRect(width*.56f,height*.76f,width*.96f,height*.82f,paint)
        if (resort) {
            paint.color=withAlpha(preset.accentArgb,.38f)
            canvas.drawCircle(width*.85f,height*.26f,short*.09f,paint)
        }
    }

    private fun creative(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float, beauty: Boolean) {
        if (beauty) {
            paint.color=withAlpha(0xFFFFFFFF.toInt(),.28f)
            canvas.drawOval(RectF(width*.66f,height*.14f,width*.92f,height*.72f),paint)
            paint.color=withAlpha(preset.accentArgb,.34f)
            canvas.drawRoundRect(RectF(width*.73f,height*.35f,width*.84f,height*.72f),short*.02f,short*.02f,paint)
        } else {
            val shapes=listOf(
                RectF(width*.62f,height*.12f,width*.78f,height*.36f),
                RectF(width*.78f,height*.30f,width*.94f,height*.56f),
                RectF(width*.62f,height*.54f,width*.84f,height*.82f)
            )
            shapes.forEachIndexed { i,r -> paint.color=withAlpha(if(i%2==0) preset.accentArgb else 0xFFFFFFFF.toInt(),.20f+i*.06f); canvas.drawRoundRect(r,short*.04f,short*.04f,paint) }
        }
    }

    private fun flow(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float) {
        paint.style=Paint.Style.STROKE; paint.strokeCap=Paint.Cap.ROUND; paint.strokeWidth=max(8f,short*.035f)
        repeat(4) { i ->
            paint.color=withAlpha(preset.accentArgb,.34f-i*.055f)
            val y=height*(.18f+i*.18f)
            val p=Path().apply { moveTo(width*.48f,y); cubicTo(width*.64f,y-short*.18f,width*.80f,y+short*.15f,width*1.05f,y-short*.04f) }
            canvas.drawPath(p,paint)
        }
        paint.style=Paint.Style.FILL
    }

    private fun drawContentPanel(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, rect: RectF, short: Float) {
        val radius=short*.035f
        when(preset.panelStyle) {
            ProfessionalPanelStyle.NONE -> return
            ProfessionalPanelStyle.GLASS -> {
                paint.color=withAlpha(0xFF071018.toInt(), if (isLight(preset.backgroundStartArgb)) .20f else .34f)
                canvas.drawRoundRect(rect,radius,radius,paint)
                paint.style=Paint.Style.STROKE; paint.strokeWidth=max(1.5f,short*.0035f); paint.color=withAlpha(0xFFFFFFFF.toInt(),.18f); canvas.drawRoundRect(rect,radius,radius,paint); paint.style=Paint.Style.FILL
            }
            ProfessionalPanelStyle.SOLID -> {
                paint.color=if(isLight(preset.backgroundStartArgb)) withAlpha(0xFFFFFFFF.toInt(),.78f) else withAlpha(0xFF070A0E.toInt(),.72f)
                canvas.drawRoundRect(rect,radius,radius,paint)
            }
            ProfessionalPanelStyle.SOFT -> {
                paint.color=if(isLight(preset.backgroundStartArgb)) withAlpha(0xFFFFFFFF.toInt(),.46f) else withAlpha(0xFF101820.toInt(),.38f)
                canvas.drawRoundRect(rect,radius,radius,paint)
            }
            ProfessionalPanelStyle.OUTLINE -> {
                paint.style=Paint.Style.STROKE; paint.strokeWidth=max(2f,short*.005f); paint.color=withAlpha(preset.accentArgb,.55f); canvas.drawRoundRect(rect,radius,radius,paint); paint.style=Paint.Style.FILL
            }
        }
    }

    private fun drawCopy(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, copy: QrCardCopy, layout: ProfessionalCardLayout, short: Float) {
        val align=when(layout.textAlignment){
            FixedCardTextAlignment.LEFT->Paint.Align.LEFT
            FixedCardTextAlignment.CENTER->Paint.Align.CENTER
            FixedCardTextAlignment.RIGHT->Paint.Align.RIGHT
        }
        fun x(rect: LayoutRect)=when(layout.textAlignment){
            FixedCardTextAlignment.LEFT->rect.left
            FixedCardTextAlignment.CENTER->(rect.left+rect.right)/2f
            FixedCardTextAlignment.RIGHT->rect.right
        }
        paint.textAlign=align
        if(copy.brand.isNotBlank()){
            paint.typeface=Typeface.create(Typeface.DEFAULT,Typeface.BOLD); paint.color=preset.accentArgb; paint.textSize=max(13f,short*.037f)
            val label=QrCardTextLayout.wrap(copy.brand.uppercase(),layout.brandRect.width,1,paint::measureText).firstOrNull().orEmpty()
            canvas.drawText(label,x(layout.brandRect),layout.brandRect.centerBaseline(paint),paint)
        }
        paint.typeface=Typeface.create(Typeface.DEFAULT,Typeface.BOLD); paint.color=preset.titleArgb; paint.textSize=max(17f,short*.055f)
        var y=layout.titleRect.top-paint.ascent()
        QrCardTextLayout.wrap(copy.title,layout.titleRect.width,2,paint::measureText).forEach { line -> if(y<=layout.titleRect.bottom){ canvas.drawText(line,x(layout.titleRect),y,paint); y+=paint.textSize*1.10f } }
        paint.typeface=Typeface.create(Typeface.DEFAULT,Typeface.NORMAL); paint.color=preset.bodyArgb; paint.textSize=max(12f,short*.030f)
        y=layout.bodyRect.top-paint.ascent()
        QrCardTextLayout.wrap(copy.description,layout.bodyRect.width,3,paint::measureText).forEach { line -> if(y<=layout.bodyRect.bottom){ canvas.drawText(line,x(layout.bodyRect),y,paint); y+=paint.textSize*1.24f } }
        paint.textAlign=Paint.Align.LEFT
    }

    private fun drawQrPlate(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, qrBitmap: Bitmap, rect: RectF, short: Float) {
        val radius=short*.025f
        val shadow=short*.008f
        paint.color=withAlpha(0xFF000000.toInt(),.22f)
        canvas.drawRoundRect(RectF(rect.left+shadow,rect.top+shadow,rect.right+shadow,rect.bottom+shadow),radius,radius,paint)
        when(preset.qrPlateStyle){
            ProfessionalQrPlateStyle.CLEAN -> paint.color=0xFFFFFFFF.toInt()
            ProfessionalQrPlateStyle.BORDERED -> {
                paint.color=preset.accentArgb; canvas.drawRoundRect(expand(rect,short*.007f),radius*1.15f,radius*1.15f,paint); paint.color=0xFFFFFFFF.toInt()
            }
            ProfessionalQrPlateStyle.INK_EDGE -> {
                paint.color=0xFF101820.toInt(); canvas.drawRoundRect(expand(rect,short*.007f),radius*1.15f,radius*1.15f,paint); paint.color=0xFFFFFFFF.toInt()
            }
            ProfessionalQrPlateStyle.GLASS -> paint.color=withAlpha(0xFFFFFFFF.toInt(),.93f)
        }
        canvas.drawRoundRect(rect,radius,radius,paint)
        val inset=short*.018f
        val inner=RectF(rect.left+inset,rect.top+inset,rect.right-inset,rect.bottom-inset)
        canvas.drawBitmap(qrBitmap,null,inner,paint)
    }

    private fun drawCta(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, text: String, rect: RectF, short: Float) {
        if(text.isBlank()) return
        val radius=rect.height()/2f
        paint.color=withAlpha(0xFF000000.toInt(),.13f)
        canvas.drawRoundRect(RectF(rect.left+short*.004f,rect.top+short*.004f,rect.right+short*.004f,rect.bottom+short*.004f),radius,radius,paint)
        paint.color=preset.accentArgb; canvas.drawRoundRect(rect,radius,radius,paint)
        paint.typeface=Typeface.create(Typeface.DEFAULT,Typeface.BOLD); paint.color=preset.ctaTextArgb; paint.textSize=max(12f,min(short*.028f,rect.height()*.40f)); paint.textAlign=Paint.Align.CENTER
        val label=QrCardTextLayout.wrap(text,rect.width()*.80f,1,paint::measureText).firstOrNull().orEmpty()
        canvas.drawText(label,rect.centerX(),rect.centerY()-(paint.ascent()+paint.descent())/2f,paint); paint.textAlign=Paint.Align.LEFT
    }

    private fun drawMicroDetail(canvas: Canvas, paint: Paint, preset: ProfessionalCardPreset, width: Float, height: Float, short: Float) {
        paint.color=withAlpha(preset.accentArgb,.70f)
        val d=max(3f,short*.008f)
        repeat(3){i->canvas.drawCircle(width*(.06f+i*.018f),height*.94f,d*(1f-i*.16f),paint)}
    }

    private fun LayoutRect.toRectF()=RectF(left,top,right,bottom)
    private fun LayoutRect.centerBaseline(paint: Paint)=(top+bottom)/2f-(paint.ascent()+paint.descent())/2f
    private fun expand(rect: RectF, amount: Float)=RectF(rect.left-amount,rect.top-amount,rect.right+amount,rect.bottom+amount)
    private fun withAlpha(color:Int,alpha:Float):Int=((255*alpha.coerceIn(0f,1f)).toInt() shl 24) or (color and 0x00FFFFFF)
    private fun isLight(color:Int):Boolean {
        val r=(color shr 16) and 0xFF; val g=(color shr 8) and 0xFF; val b=color and 0xFF
        return (r*299+g*587+b*114)/1000 >= 145
    }
}
