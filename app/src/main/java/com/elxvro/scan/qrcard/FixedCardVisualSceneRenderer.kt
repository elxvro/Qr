package com.elxvro.scan.qrcard

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.max
import kotlin.math.min

object FixedCardVisualSceneRenderer {
    fun draw(
        canvas: Canvas,
        paint: Paint,
        preset: FixedCardPreset,
        width: Float,
        height: Float
    ) {
        drawBase(canvas, paint, preset, width, height)

        when (preset.scene) {
            FixedCardVisualScene.FREE_TRAVEL_MOUNTAINS -> mountains(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_CAFE_TABLE -> cafe(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_TECH_NEON -> neonTech(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_CITY_NIGHT -> city(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_NATURE_FOREST -> forest(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_BUSINESS_DESK -> desk(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_EVENT_STAGE -> event(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_MINIMAL_INTERIOR -> minimalInterior(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_CREATIVE_PET -> pet(canvas, paint, preset, width, height)
            FixedCardVisualScene.FREE_ABSTRACT_FLOW -> abstractFlow(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_LUXURY_COAST -> luxuryCoast(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_FINE_DINING -> fineDining(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_CORPORATE_TOWER -> corporateTower(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_FASHION_EDITORIAL -> fashion(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_NIGHTLIFE_STAGE -> nightlife(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_WELLNESS_RETREAT -> wellness(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_HOTEL_RESORT -> resort(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_PREMIUM_TECH -> premiumTech(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_REAL_ESTATE -> realEstate(canvas, paint, preset, width, height)
            FixedCardVisualScene.PRO_BEAUTY_PRODUCT -> beauty(canvas, paint, preset, width, height)
        }

        drawReadability(canvas, paint, preset, width, height)
    }

    private fun drawBase(
        canvas: Canvas,
        paint: Paint,
        preset: FixedCardPreset,
        width: Float,
        height: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            0f,
            0f,
            width,
            height,
            preset.startArgb,
            preset.endArgb,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width, height, paint)
        paint.shader = null
    }

    private fun mountains(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        val v = preset.sceneVariant
        val skyTop = blend(preset.startArgb, 0xFF56B8F6.toInt(), 0.26f + v * 0.04f)
        val skyBottom = blend(preset.endArgb, 0xFFBDE9FF.toInt(), 0.38f)
        paint.shader = LinearGradient(0f, 0f, 0f, h, skyTop, skyBottom, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        paint.color = withAlpha(0xFFFFF4CF.toInt(), 0.34f)
        canvas.drawCircle(w * (0.72f - v * 0.04f), h * 0.16f, min(w,h) * 0.12f, paint)

        drawMountain(canvas, paint, w * -0.06f, h * 0.58f, w * 0.38f, h * 0.17f, 0xFF163F59.toInt())
        drawMountain(canvas, paint, w * 0.19f, h * 0.60f, w * 0.64f, h * 0.11f, 0xFF245B72.toInt())
        drawMountain(canvas, paint, w * 0.55f, h * 0.61f, w * 1.04f, h * 0.22f, 0xFF2E7189.toInt())

        paint.color = withAlpha(0xFFFFFFFF.toInt(), 0.80f)
        val snow = Path().apply {
            moveTo(w * 0.33f, h * 0.17f)
            lineTo(w * 0.26f, h * 0.28f)
            lineTo(w * 0.32f, h * 0.25f)
            lineTo(w * 0.38f, h * 0.31f)
            lineTo(w * 0.42f, h * 0.23f)
            close()
        }
        canvas.drawPath(snow, paint)

        paint.shader = LinearGradient(0f, h * 0.55f, 0f, h, 0xFF1D728F.toInt(), 0xFF0D3F5A.toInt(), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, h * 0.56f, w, h, paint)
        paint.shader = null

        repeat(7) { i ->
            val x = w * (0.03f + i * 0.15f + (v % 2) * 0.025f)
            val baseY = h * (0.88f + (i % 2) * 0.035f)
            drawPine(canvas, paint, x, baseY, min(w,h) * (0.10f + (i % 3) * 0.015f), 0xFF082F2A.toInt())
        }
    }

    private fun cafe(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader = LinearGradient(0f, 0f, 0f, h, 0xFF2A140A.toInt(), 0xFF8A5630.toInt(), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        repeat(8) { i ->
            paint.color = withAlpha(0xFFFFC86C.toInt(), 0.12f + (i % 3) * 0.08f)
            val x = w * ((i * 0.17f + preset.sceneVariant * 0.07f) % 1f)
            val y = h * (0.08f + (i % 4) * 0.13f)
            canvas.drawCircle(x, y, min(w,h) * (0.04f + (i % 2) * 0.018f), paint)
        }

        paint.color = 0xFF3A1F12.toInt()
        canvas.drawRect(0f, h * 0.64f, w, h, paint)
        paint.color = 0xFFE7D0B5.toInt()
        val cup = RectF(w * 0.58f, h * 0.58f, w * 0.86f, h * 0.80f)
        canvas.drawRoundRect(cup, min(w,h)*0.035f, min(w,h)*0.035f, paint)
        paint.color = 0xFF3A2116.toInt()
        canvas.drawOval(RectF(w*0.60f,h*0.59f,w*0.84f,h*0.65f),paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = max(2f,min(w,h)*0.012f)
        paint.color = 0xFFE7D0B5.toInt()
        canvas.drawOval(RectF(w*0.82f,h*0.63f,w*0.92f,h*0.75f),paint)
        paint.style = Paint.Style.FILL

        repeat(3) { i ->
            val steam = Path().apply {
                moveTo(w*(0.65f+i*0.055f), h*0.57f)
                cubicTo(w*(0.62f+i*0.055f),h*0.50f,w*(0.71f+i*0.04f),h*0.47f,w*(0.67f+i*0.055f),h*0.40f)
            }
            paint.style=Paint.Style.STROKE
            paint.strokeWidth=max(2f,min(w,h)*0.008f)
            paint.color=withAlpha(0xFFFFFFFF.toInt(),0.45f)
            canvas.drawPath(steam,paint)
            paint.style=Paint.Style.FILL
        }

        repeat(7) { i ->
            val x = w * (0.08f + (i * 0.12f + preset.sceneVariant * 0.03f) % 0.70f)
            val y = h * (0.78f + (i % 3) * 0.055f)
            paint.color = 0xFF5A301A.toInt()
            canvas.drawOval(RectF(x,y,x+min(w,h)*0.045f,y+min(w,h)*0.027f),paint)
        }
    }

    private fun neonTech(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color = 0xFF06051D.toInt()
        canvas.drawRect(0f,0f,w,h,paint)
        repeat(5) { i ->
            val x = w*(0.08f+i*0.21f)
            val band = Path().apply {
                moveTo(x,h)
                lineTo(x+w*0.22f,0f)
                lineTo(x+w*0.32f,0f)
                lineTo(x+w*0.10f,h)
                close()
            }
            paint.shader = LinearGradient(
                x,0f,x+w*0.30f,h,
                if((i+preset.sceneVariant)%2==0) 0xFF245BFF.toInt() else 0xFFFF2EC8.toInt(),
                0x22000000,
                Shader.TileMode.CLAMP
            )
            canvas.drawPath(band,paint)
            paint.shader=null
        }
        paint.style=Paint.Style.STROKE
        paint.strokeWidth=max(2f,min(w,h)*0.006f)
        repeat(7){i->
            paint.color=withAlpha(if(i%2==0)0xFF4B7BFF.toInt() else 0xFFFF3CC9.toInt(),0.40f)
            canvas.drawLine(0f,h*(0.10f+i*0.11f),w,h*(0.03f+i*0.10f),paint)
        }
        paint.style=Paint.Style.FILL
    }

    private fun city(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFF06101C.toInt(),0xFF172A48.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
        val base=h*0.72f
        repeat(11){i->
            val bw=w*(0.055f+(i%3)*0.018f)
            val x=w*(i*0.095f)
            val top=base-h*(0.12f+((i*7+preset.sceneVariant*3)%8)*0.045f)
            paint.color=if(i%2==0)0xFF0C2036.toInt() else 0xFF102B47.toInt()
            canvas.drawRect(x,top,x+bw,base,paint)
            repeat(5){j->
                paint.color=withAlpha(if((i+j)%3==0)0xFFFFC857.toInt() else 0xFF2FA8FF.toInt(),0.68f)
                canvas.drawRect(x+bw*0.22f,top+(j+1)*(base-top)/6f,x+bw*0.36f,top+(j+1)*(base-top)/6f+bw*0.07f,paint)
            }
        }
        paint.shader=LinearGradient(0f,base,0f,h,0xFF09182A.toInt(),0xFF09243A.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,base,w,h,paint)
        paint.shader=null
        repeat(8){i->
            paint.color=withAlpha(if(i%2==0)0xFFFF4D6D.toInt() else 0xFF1DB4FF.toInt(),0.38f)
            canvas.drawRect(w*(0.04f+i*0.13f),base,w*(0.06f+i*0.13f),h,paint)
        }
    }

    private fun forest(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFF164C2A.toInt(),0xFF072818.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
        paint.shader=RadialGradient(w*0.70f,h*0.20f,min(w,h)*0.45f,0x99E6FFD0.toInt(),0x001A4829,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
        repeat(12){i->
            val side=i%2==0
            val x=if(side) w*(0.02f+(i%4)*0.05f) else w*(0.80f+(i%4)*0.05f)
            val y=h*(0.06f+(i*0.075f)%0.78f)
            drawLeaf(canvas,paint,x,y,min(w,h)*(0.12f+(i%3)*0.025f), if(i%2==0)0xFF4EA965.toInt() else 0xFF2F7E4B.toInt(), side)
        }
        repeat(10){i->
            paint.color=withAlpha(0xFFFFFFFF.toInt(),0.08f+(i%3)*0.04f)
            canvas.drawCircle(w*((i*0.17f+preset.sceneVariant*0.09f)%1f),h*((i*0.11f)%0.80f),min(w,h)*(0.025f+(i%2)*0.012f),paint)
        }
    }

    private fun desk(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFFF2EEE7.toInt()
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=LinearGradient(0f,h*0.70f,w,h,0xFFC4AF93.toInt(),0xFF8E7356.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,h*0.66f,w,h,paint)
        paint.shader=null
        paint.color=0xFFF8F8F6.toInt()
        canvas.drawRoundRect(RectF(w*0.58f,h*0.18f,w*0.92f,h*0.58f),min(w,h)*0.025f,min(w,h)*0.025f,paint)
        paint.color=0xFF183C6B.toInt()
        canvas.drawRect(w*0.62f,h*0.24f,w*0.86f,h*0.255f,paint)
        repeat(4){i->
            paint.color=withAlpha(0xFF43505A.toInt(),0.40f)
            canvas.drawRect(w*0.62f,h*(0.30f+i*0.055f),w*(0.78f+(i%2)*0.06f),h*(0.315f+i*0.055f),paint)
        }
        paint.color=0xFF152338.toInt()
        val pen=Path().apply{
            moveTo(w*0.72f,h*0.72f)
            lineTo(w*0.77f,h*0.93f)
            lineTo(w*0.80f,h*0.92f)
            lineTo(w*0.75f,h*0.71f)
            close()
        }
        canvas.drawPath(pen,paint)
        repeat(5){i->drawLeaf(canvas,paint,w*(0.08f+i*0.04f),h*(0.15f+i*0.07f),min(w,h)*0.10f,0xFF2E7D51.toInt(),true)}
    }

    private fun event(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFF150020.toInt(),0xFF06000E.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
        val beamColors=intArrayOf(0xFFFF2FAE.toInt(),0xFF7E4BFF.toInt(),0xFF23A7FF.toInt())
        repeat(5){i->
            val path=Path().apply{
                moveTo(w*(0.12f+i*0.20f),0f)
                lineTo(w*(0.02f+i*0.20f),h*0.68f)
                lineTo(w*(0.20f+i*0.20f),h*0.68f)
                close()
            }
            paint.color=withAlpha(beamColors[(i+preset.sceneVariant)%beamColors.size],0.20f)
            canvas.drawPath(path,paint)
        }
        repeat(16){i->
            paint.color=if(i%3==0)0xFF3C0C45.toInt() else 0xFF19091F.toInt()
            canvas.drawCircle(w*(i/15f),h*(0.78f+(i%3)*0.035f),min(w,h)*0.055f,paint)
        }
        paint.color=0xFFFF2FAE.toInt()
        canvas.drawRoundRect(RectF(w*0.30f,h*0.51f,w*0.70f,h*0.56f),min(w,h)*0.02f,min(w,h)*0.02f,paint)
    }

    private fun minimalInterior(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFFF3EDE3.toInt()
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=LinearGradient(w*0.18f,0f,w*0.75f,h,0x00FFFFFF,0x446F593E,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
        paint.color=0xFFE1D0B9.toInt()
        canvas.drawRoundRect(RectF(w*0.58f,h*0.12f,w*0.92f,h*0.72f),w*0.17f,w*0.17f,paint)
        paint.color=0xFFCAB69A.toInt()
        canvas.drawOval(RectF(w*0.64f,h*0.60f,w*0.79f,h*0.82f),paint)
        paint.style=Paint.Style.STROKE
        paint.strokeWidth=max(2f,min(w,h)*0.007f)
        paint.color=0xFF6B7556.toInt()
        repeat(5){i->
            val path=Path().apply{
                moveTo(w*0.715f,h*0.61f)
                cubicTo(w*(0.62f+i*0.045f),h*0.48f,w*(0.60f+i*0.05f),h*0.37f,w*(0.64f+i*0.055f),h*(0.24f+(i%2)*0.06f))
            }
            canvas.drawPath(path,paint)
        }
        paint.style=Paint.Style.FILL
    }

    private fun pet(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFFFFE4CA.toInt(),0xFFFFB58A.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
        repeat(9){i->
            drawLeaf(canvas,paint,w*((i*0.13f)%1f),h*(0.72f+(i%3)*0.07f),min(w,h)*0.10f,if(i%2==0)0xFF4B9A5A.toInt() else 0xFF86B84A.toInt(),i%2==0)
        }
        val cx=w*(0.67f+0.03f*(preset.sceneVariant-2))
        val cy=h*0.60f
        paint.color=0xFFD88942.toInt()
        canvas.drawCircle(cx,cy,min(w,h)*0.17f,paint)
        val ear=min(w,h)*0.11f
        val leftEar=Path().apply{moveTo(cx-ear*0.9f,cy-ear*0.7f);lineTo(cx-ear*1.7f,cy-ear*2.0f);lineTo(cx-ear*0.2f,cy-ear*1.2f);close()}
        val rightEar=Path().apply{moveTo(cx+ear*0.9f,cy-ear*0.7f);lineTo(cx+ear*1.7f,cy-ear*2.0f);lineTo(cx+ear*0.2f,cy-ear*1.2f);close()}
        canvas.drawPath(leftEar,paint);canvas.drawPath(rightEar,paint)
        paint.color=0xFF2A1A13.toInt()
        canvas.drawCircle(cx-ear*0.45f,cy-ear*0.15f,ear*0.13f,paint)
        canvas.drawCircle(cx+ear*0.45f,cy-ear*0.15f,ear*0.13f,paint)
        paint.color=0xFF2B160E.toInt()
        canvas.drawOval(RectF(cx-ear*0.25f,cy+ear*0.20f,cx+ear*0.25f,cy+ear*0.48f),paint)
    }

    private fun abstractFlow(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFF07152A.toInt()
        canvas.drawRect(0f,0f,w,h,paint)
        val colors=intArrayOf(0xFFFF7A2E.toInt(),0xFF2468FF.toInt(),0xFF15B9FF.toInt(),0xFFFFC04A.toInt())
        repeat(6){i->
            val path=Path().apply{
                moveTo(-w*0.10f,h*(0.08f+i*0.16f))
                cubicTo(w*0.25f,h*(0.02f+i*0.16f),w*0.54f,h*(0.24f+i*0.10f),w*1.08f,h*(0.04f+i*0.14f))
            }
            paint.style=Paint.Style.STROKE
            paint.strokeWidth=min(w,h)*(0.07f+(i%2)*0.03f)
            paint.strokeCap=Paint.Cap.ROUND
            paint.color=withAlpha(colors[(i+preset.sceneVariant)%colors.size],0.58f)
            canvas.drawPath(path,paint)
        }
        paint.style=Paint.Style.FILL
    }

    private fun luxuryCoast(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFF5BA8D5.toInt(),0xFFE9D6B6.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        paint.color=0xFFF7F1E6.toInt()
        canvas.drawRect(0f,h*0.60f,w,h,paint)
        paint.color=0xFF1483A6.toInt()
        canvas.drawRect(0f,h*0.54f,w,h*0.66f,paint)
        repeat(6){i->
            paint.color=if(i%2==0)0xFFFFFFFF.toInt() else 0xFFF1E5D1.toInt()
            canvas.drawRoundRect(RectF(w*(0.05f+i*0.11f),h*(0.35f+(i%2)*0.05f),w*(0.16f+i*0.11f),h*0.58f),min(w,h)*0.018f,min(w,h)*0.018f,paint)
        }
        paint.color=0xFFD6A95B.toInt()
        canvas.drawCircle(w*0.80f,h*0.18f,min(w,h)*0.09f,paint)
        drawPalm(canvas,paint,w*0.87f,h*0.63f,min(w,h)*0.24f)
    }

    private fun fineDining(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFF120A07.toInt();canvas.drawRect(0f,0f,w,h,paint)
        repeat(10){i->
            paint.color=withAlpha(0xFFE7BA66.toInt(),0.10f+(i%4)*0.06f)
            canvas.drawCircle(w*((i*0.19f)%1f),h*(0.05f+(i%5)*0.11f),min(w,h)*(0.025f+(i%3)*0.015f),paint)
        }
        paint.color=0xFF2A1710.toInt();canvas.drawRect(0f,h*0.64f,w,h,paint)
        paint.color=0xFFF2E7D3.toInt();canvas.drawOval(RectF(w*0.55f,h*0.52f,w*0.92f,h*0.77f),paint)
        paint.color=0xFFB86734.toInt();canvas.drawOval(RectF(w*0.61f,h*0.56f,w*0.86f,h*0.69f),paint)
        repeat(4){i->drawLeaf(canvas,paint,w*(0.68f+i*0.035f),h*(0.50f-i*0.025f),min(w,h)*0.065f,0xFF5A7B3F.toInt(),i%2==0)}
    }

    private fun corporateTower(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,w,h,0xFF05080D.toInt(),0xFF1C2A40.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        repeat(5){i->
            val x=w*(0.48f+i*0.09f)
            val top=h*(0.12f+(i%2)*0.08f)
            paint.color=if(i%2==0)0xFF111B2A.toInt() else 0xFF172438.toInt()
            canvas.drawRect(x,top,x+w*0.12f,h,paint)
            paint.style=Paint.Style.STROKE
            paint.strokeWidth=max(1.5f,min(w,h)*0.004f)
            paint.color=withAlpha(0xFFD6B76C.toInt(),0.35f)
            repeat(6){j->canvas.drawLine(x,top+(j+1)*(h-top)/7f,x+w*0.12f,top+(j+1)*(h-top)/7f,paint)}
            paint.style=Paint.Style.FILL
        }
        val ribbon=Path().apply{moveTo(0f,h*0.78f);cubicTo(w*0.28f,h*0.58f,w*0.54f,h*0.88f,w,h*0.56f);lineTo(w,h*0.68f);cubicTo(w*0.55f,h*0.94f,w*0.26f,h*0.72f,0f,h*0.91f);close()}
        paint.color=withAlpha(0xFFD6B76C.toInt(),0.22f);canvas.drawPath(ribbon,paint)
    }

    private fun fashion(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,w,h,0xFF140C0B.toInt(),0xFF5A342A.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        paint.color=0xFFB9785C.toInt()
        canvas.drawOval(RectF(w*0.58f,h*0.12f,w*0.88f,h*0.48f),paint)
        paint.color=0xFF2B1714.toInt()
        val hair=Path().apply{moveTo(w*0.59f,h*0.22f);cubicTo(w*0.62f,h*0.05f,w*0.88f,h*0.04f,w*0.91f,h*0.32f);cubicTo(w*0.84f,h*0.23f,w*0.79f,h*0.18f,w*0.70f,h*0.16f);close()}
        canvas.drawPath(hair,paint)
        paint.color=0xFF0A0A0A.toInt()
        canvas.drawRoundRect(RectF(w*0.62f,h*0.24f,w*0.76f,h*0.30f),min(w,h)*0.02f,min(w,h)*0.02f,paint)
        canvas.drawRoundRect(RectF(w*0.77f,h*0.24f,w*0.89f,h*0.30f),min(w,h)*0.02f,min(w,h)*0.02f,paint)
        paint.shader=LinearGradient(0f,h*0.50f,w,h,0xFF1B1110.toInt(),0xFF050506.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,h*0.50f,w,h,paint);paint.shader=null
    }

    private fun nightlife(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFF06000E.toInt();canvas.drawRect(0f,0f,w,h,paint)
        val colors=intArrayOf(0xFFFF27CE.toInt(),0xFF5B3CFF.toInt(),0xFF00B4FF.toInt())
        repeat(8){i->
            paint.color=withAlpha(colors[(i+preset.sceneVariant)%3],0.20f)
            val x=w*(i/7f)
            val p=Path().apply{moveTo(x,0f);lineTo(x-w*0.12f,h*0.70f);lineTo(x+w*0.12f,h*0.70f);close()}
            canvas.drawPath(p,paint)
        }
        repeat(18){i->
            paint.color=0xFF130519.toInt()
            canvas.drawCircle(w*(i/17f),h*(0.80f+(i%4)*0.025f),min(w,h)*0.045f,paint)
        }
        repeat(5){i->
            paint.color=colors[i%3]
            canvas.drawRoundRect(RectF(w*(0.12f+i*0.15f),h*(0.32f-(i%2)*0.06f),w*(0.17f+i*0.15f),h*0.58f),min(w,h)*0.02f,min(w,h)*0.02f,paint)
        }
    }

    private fun wellness(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFFF1E4C7.toInt(),0xFF7D9D72.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        paint.color=withAlpha(0xFFFFF6D2.toInt(),0.62f)
        canvas.drawCircle(w*0.72f,h*0.20f,min(w,h)*0.13f,paint)
        repeat(6){i->drawLeaf(canvas,paint,w*(0.05f+i*0.06f),h*(0.72f-i*0.045f),min(w,h)*0.11f,0xFF486D46.toInt(),i%2==0)}
        paint.color=0xFF40513B.toInt()
        canvas.drawCircle(w*0.72f,h*0.64f,min(w,h)*0.07f,paint)
        canvas.drawRoundRect(RectF(w*0.66f,h*0.70f,w*0.78f,h*0.90f),min(w,h)*0.06f,min(w,h)*0.06f,paint)
    }

    private fun resort(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFF7FB5D4.toInt(),0xFFFFC98A.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        paint.color=0xFF124D65.toInt();canvas.drawRect(0f,h*0.56f,w,h,paint)
        paint.color=0xFF4EAAC4.toInt();canvas.drawRect(0f,h*0.62f,w,h*0.80f,paint)
        paint.color=0xFFF1E4D0.toInt()
        canvas.drawRoundRect(RectF(w*0.52f,h*0.34f,w*0.95f,h*0.64f),min(w,h)*0.02f,min(w,h)*0.02f,paint)
        repeat(5){i->
            paint.color=0xFF5D3C28.toInt()
            canvas.drawRect(w*(0.56f+i*0.075f),h*0.42f,w*(0.60f+i*0.075f),h*0.60f,paint)
        }
        drawPalm(canvas,paint,w*0.18f,h*0.65f,min(w,h)*0.28f)
    }

    private fun premiumTech(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFF030713.toInt();canvas.drawRect(0f,0f,w,h,paint)
        repeat(4){i->
            val path=Path().apply{
                moveTo(w*(0.05f+i*0.24f),h)
                cubicTo(w*(0.18f+i*0.18f),h*0.64f,w*(0.14f+i*0.20f),h*0.28f,w*(0.35f+i*0.20f),0f)
            }
            paint.style=Paint.Style.STROKE
            paint.strokeWidth=min(w,h)*(0.04f+i*0.012f)
            paint.color=withAlpha(if(i%2==0)0xFF2468FF.toInt() else 0xFF6C2CFF.toInt(),0.52f)
            canvas.drawPath(path,paint)
        }
        paint.style=Paint.Style.FILL
        paint.color=0xFF10172A.toInt()
        canvas.drawRoundRect(RectF(w*0.55f,h*0.44f,w*0.88f,h*0.72f),min(w,h)*0.055f,min(w,h)*0.055f,paint)
        paint.style=Paint.Style.STROKE
        paint.strokeWidth=max(2f,min(w,h)*0.008f)
        paint.color=0xFF4D79FF.toInt()
        canvas.drawRoundRect(RectF(w*0.57f,h*0.46f,w*0.86f,h*0.70f),min(w,h)*0.045f,min(w,h)*0.045f,paint)
        paint.style=Paint.Style.FILL
    }

    private fun realEstate(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.shader=LinearGradient(0f,0f,0f,h,0xFF7DA7C4.toInt(),0xFFE8C991.toInt(),Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        paint.color=0xFF223443.toInt()
        canvas.drawRect(w*0.45f,h*0.40f,w*0.94f,h*0.76f,paint)
        paint.color=0xFFE8E4DC.toInt()
        canvas.drawRect(w*0.49f,h*0.44f,w*0.90f,h*0.72f,paint)
        repeat(4){i->
            paint.color=0xFF486D86.toInt()
            canvas.drawRect(w*(0.52f+i*0.09f),h*0.48f,w*(0.58f+i*0.09f),h*0.66f,paint)
        }
        paint.color=0xFF294F61.toInt()
        canvas.drawRect(0f,h*0.76f,w,h,paint)
        repeat(5){i->drawLeaf(canvas,paint,w*(0.05f+i*0.06f),h*(0.72f-i*0.02f),min(w,h)*0.085f,0xFF315A35.toInt(),true)}
    }

    private fun beauty(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        paint.color=0xFFF2E9DB.toInt();canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=RadialGradient(w*0.72f,h*0.22f,min(w,h)*0.42f,0x66FFFFFF,0x00FFFFFF,Shader.TileMode.CLAMP)
        canvas.drawRect(0f,0f,w,h,paint);paint.shader=null
        repeat(6){i->drawLeaf(canvas,paint,w*(0.75f+i*0.025f),h*(0.08f+i*0.08f),min(w,h)*0.095f,if(i%2==0)0xFF6E8A50.toInt() else 0xFF90A766.toInt(),false)}
        paint.color=0xFFE2D4C0.toInt()
        canvas.drawOval(RectF(w*0.48f,h*0.68f,w*0.95f,h*0.90f),paint)
        paint.color=0xFFF7F2E9.toInt()
        canvas.drawRoundRect(RectF(w*0.66f,h*0.35f,w*0.82f,h*0.76f),min(w,h)*0.025f,min(w,h)*0.025f,paint)
        paint.color=0xFFB88A4A.toInt()
        canvas.drawRect(w*0.69f,h*0.30f,w*0.79f,h*0.38f,paint)
        paint.color=0xFF3D3428.toInt()
        canvas.drawRect(w*0.695f,h*0.50f,w*0.785f,h*0.515f,paint)
    }

    private fun drawReadability(canvas: Canvas, paint: Paint, preset: FixedCardPreset, w: Float, h: Float) {
        val dark = preset.titleArgb == 0xFFFFFFFF.toInt() || preset.titleArgb == 0xFFFFFAFB.toInt() || preset.titleArgb == 0xFFFFFBF6.toInt()
        paint.shader = if (dark) {
            LinearGradient(
                0f,0f,w,0f,
                intArrayOf(0x88000000.toInt(),0x22000000,0x66000000),
                floatArrayOf(0f,0.52f,1f),
                Shader.TileMode.CLAMP
            )
        } else {
            LinearGradient(
                0f,0f,w,0f,
                intArrayOf(0x55FFFFFF,0x00FFFFFF,0x44FFFFFF),
                floatArrayOf(0f,0.55f,1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f,0f,w,h,paint)
        paint.shader=null
    }

    private fun drawMountain(canvas: Canvas, paint: Paint, left: Float, base: Float, right: Float, peak: Float, color: Int) {
        val p=Path().apply{
            moveTo(left,base)
            lineTo((left+right)/2f,peak)
            lineTo(right,base)
            close()
        }
        paint.color=color
        canvas.drawPath(p,paint)
    }

    private fun drawPine(canvas: Canvas, paint: Paint, x: Float, baseY: Float, size: Float, color: Int) {
        paint.color=color
        repeat(3){i->
            val top=baseY-size*(0.90f-i*0.23f)
            val half=size*(0.30f+i*0.05f)
            val p=Path().apply{moveTo(x,top);lineTo(x-half,baseY-size*(0.32f-i*0.10f));lineTo(x+half,baseY-size*(0.32f-i*0.10f));close()}
            canvas.drawPath(p,paint)
        }
        canvas.drawRect(x-size*0.035f,baseY-size*0.18f,x+size*0.035f,baseY,paint)
    }

    private fun drawLeaf(canvas: Canvas, paint: Paint, x: Float, y: Float, size: Float, color: Int, leftFacing: Boolean) {
        paint.color=color
        val sign=if(leftFacing)-1f else 1f
        val p=Path().apply{
            moveTo(x,y)
            cubicTo(x+sign*size*0.75f,y-size*0.55f,x+sign*size*0.75f,y+size*0.42f,x,y+size*0.18f)
            cubicTo(x+sign*size*0.12f,y+size*0.05f,x+sign*size*0.12f,y-size*0.03f,x,y)
            close()
        }
        canvas.drawPath(p,paint)
    }

    private fun drawPalm(canvas: Canvas, paint: Paint, x: Float, baseY: Float, size: Float) {
        paint.color=0xFF6B4B2A.toInt()
        val trunk=Path().apply{
            moveTo(x-size*0.03f,baseY)
            cubicTo(x-size*0.08f,baseY-size*0.30f,x+size*0.02f,baseY-size*0.55f,x+size*0.01f,baseY-size*0.70f)
            lineTo(x+size*0.06f,baseY-size*0.70f)
            cubicTo(x+size*0.05f,baseY-size*0.50f,x,baseY-size*0.28f,x+size*0.03f,baseY)
            close()
        }
        canvas.drawPath(trunk,paint)
        repeat(7){i->
            val angle=(i-3)*0.35f
            val dx=size*(0.34f+0.04f*(i%2))
            val dy=size*(0.17f+0.03f*(i%3))
            drawLeaf(canvas,paint,x+size*0.03f,baseY-size*0.70f,dx,0xFF2E6D42.toInt(),angle<0)
        }
    }

    private fun withAlpha(color: Int, alpha: Float): Int {
        val a=(255f*alpha.coerceIn(0f,1f)).toInt()
        return (color and 0x00FFFFFF) or (a shl 24)
    }

    private fun blend(a: Int, b: Int, fraction: Float): Int {
        val f=fraction.coerceIn(0f,1f)
        fun ch(shift:Int):Int{
            val ca=(a shr shift) and 0xFF
            val cb=(b shr shift) and 0xFF
            return (ca+(cb-ca)*f).toInt().coerceIn(0,255)
        }
        return (0xFF shl 24) or (ch(16) shl 16) or (ch(8) shl 8) or ch(0)
    }
}
