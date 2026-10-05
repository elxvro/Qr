package com.elxvro.scan.qrcard

import kotlin.math.min

enum class FixedCardTextAlignment {
    LEFT,
    CENTER,
    RIGHT
}

data class FixedCardLayout(
    val brandRect: LayoutRect,
    val titleRect: LayoutRect,
    val qrRect: LayoutRect,
    val ctaRect: LayoutRect,
    val textAlignment: FixedCardTextAlignment
)

object FixedCardLayoutPolicy {
    fun resolve(
        width: Int,
        height: Int,
        variant: FixedCardLayoutVariant
    ): FixedCardLayout {
        require(width > 0 && height > 0)
        return if (width >= height) landscape(width, height, variant) else portrait(width, height, variant)
    }

    private fun landscape(width: Int, height: Int, variant: FixedCardLayoutVariant): FixedCardLayout {
        val w = width.toFloat()
        val h = height.toFloat()
        val s = min(w, h)

        fun rect(l: Float, t: Float, r: Float, b: Float) = LayoutRect(w*l, h*t, w*r, h*b)
        fun qr(cx: Float, cy: Float, side: Float): LayoutRect {
            val d = s * side
            return LayoutRect(w*cx-d/2f, h*cy-d/2f, w*cx+d/2f, h*cy+d/2f)
        }

        return when (variant) {
            FixedCardLayoutVariant.TEXT_LEFT_QR_RIGHT ->
                FixedCardLayout(rect(.07f,.11f,.48f,.19f), rect(.07f,.22f,.49f,.56f), qr(.76f,.43f,.43f), rect(.61f,.70f,.91f,.82f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.QR_LEFT_TEXT_RIGHT ->
                FixedCardLayout(rect(.51f,.11f,.92f,.19f), rect(.51f,.22f,.92f,.56f), qr(.23f,.43f,.43f), rect(.56f,.70f,.90f,.82f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.CENTER_STACK ->
                FixedCardLayout(rect(.12f,.08f,.88f,.16f), rect(.16f,.18f,.84f,.36f), qr(.50f,.59f,.40f), rect(.34f,.82f,.66f,.91f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.TOP_COPY_BOTTOM_QR ->
                FixedCardLayout(rect(.07f,.08f,.55f,.16f), rect(.07f,.18f,.60f,.43f), qr(.80f,.57f,.36f), rect(.08f,.69f,.38f,.81f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.SPLIT_BAND ->
                FixedCardLayout(rect(.08f,.12f,.43f,.20f), rect(.08f,.24f,.44f,.61f), qr(.74f,.48f,.48f), rect(.08f,.71f,.39f,.83f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.CORNER_QR ->
                FixedCardLayout(rect(.07f,.10f,.62f,.18f), rect(.07f,.22f,.62f,.56f), qr(.83f,.68f,.30f), rect(.07f,.68f,.40f,.80f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.FLOATING_QR ->
                FixedCardLayout(rect(.08f,.10f,.48f,.18f), rect(.08f,.23f,.47f,.63f), qr(.74f,.39f,.40f), rect(.60f,.70f,.91f,.82f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.SIDE_RAIL ->
                FixedCardLayout(rect(.14f,.12f,.47f,.20f), rect(.14f,.24f,.49f,.64f), qr(.79f,.48f,.40f), rect(.14f,.71f,.45f,.83f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.EDITORIAL ->
                FixedCardLayout(rect(.07f,.10f,.46f,.18f), rect(.07f,.21f,.48f,.54f), qr(.76f,.43f,.42f), rect(.07f,.69f,.38f,.81f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.SIGNATURE ->
                FixedCardLayout(rect(.08f,.11f,.48f,.19f), rect(.08f,.22f,.49f,.55f), qr(.76f,.43f,.42f), rect(.60f,.70f,.91f,.82f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.HERO_TOP ->
                FixedCardLayout(rect(.58f,.10f,.91f,.18f), rect(.53f,.22f,.91f,.54f), qr(.23f,.54f,.40f), rect(.54f,.67f,.90f,.80f), FixedCardTextAlignment.RIGHT)
            FixedCardLayoutVariant.HERO_BOTTOM ->
                FixedCardLayout(rect(.08f,.09f,.42f,.17f), rect(.08f,.20f,.43f,.53f), qr(.75f,.57f,.42f), rect(.08f,.66f,.40f,.79f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.OFFSET_LEFT ->
                FixedCardLayout(rect(.42f,.09f,.88f,.17f), rect(.42f,.20f,.90f,.54f), qr(.20f,.52f,.36f), rect(.51f,.67f,.88f,.79f), FixedCardTextAlignment.RIGHT)
            FixedCardLayoutVariant.OFFSET_RIGHT ->
                FixedCardLayout(rect(.08f,.09f,.53f,.17f), rect(.08f,.20f,.55f,.54f), qr(.82f,.52f,.34f), rect(.10f,.67f,.47f,.79f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.MAGAZINE ->
                FixedCardLayout(rect(.08f,.09f,.38f,.16f), rect(.08f,.19f,.53f,.51f), qr(.74f,.57f,.34f), rect(.58f,.76f,.90f,.87f), FixedCardTextAlignment.LEFT)
        }
    }

    private fun portrait(width: Int, height: Int, variant: FixedCardLayoutVariant): FixedCardLayout {
        val w = width.toFloat()
        val h = height.toFloat()
        val s = min(w, h)

        fun rect(l: Float, t: Float, r: Float, b: Float) = LayoutRect(w*l, h*t, w*r, h*b)
        fun qr(cx: Float, cy: Float, side: Float): LayoutRect {
            val d = s * side
            return LayoutRect(w*cx-d/2f, h*cy-d/2f, w*cx+d/2f, h*cy+d/2f)
        }

        return when (variant) {
            FixedCardLayoutVariant.TEXT_LEFT_QR_RIGHT ->
                FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.30f), qr(.50f,.54f,.44f), rect(.24f,.77f,.76f,.84f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.QR_LEFT_TEXT_RIGHT ->
                FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.30f), qr(.50f,.54f,.44f), rect(.24f,.77f,.76f,.84f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.CENTER_STACK ->
                FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.29f), qr(.50f,.53f,.44f), rect(.25f,.76f,.75f,.83f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.TOP_COPY_BOTTOM_QR ->
                FixedCardLayout(rect(.10f,.06f,.90f,.12f), rect(.10f,.14f,.90f,.26f), qr(.50f,.55f,.42f), rect(.20f,.74f,.80f,.81f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.SPLIT_BAND ->
                FixedCardLayout(rect(.10f,.09f,.90f,.15f), rect(.10f,.17f,.90f,.31f), qr(.50f,.58f,.43f), rect(.17f,.80f,.83f,.87f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.CORNER_QR ->
                FixedCardLayout(rect(.08f,.07f,.92f,.13f), rect(.08f,.15f,.92f,.30f), qr(.68f,.56f,.36f), rect(.10f,.72f,.49f,.79f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.FLOATING_QR ->
                FixedCardLayout(rect(.09f,.08f,.91f,.14f), rect(.09f,.16f,.91f,.29f), qr(.36f,.55f,.38f), rect(.52f,.73f,.89f,.80f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.SIDE_RAIL ->
                FixedCardLayout(rect(.16f,.08f,.90f,.14f), rect(.16f,.16f,.90f,.30f), qr(.52f,.55f,.41f), rect(.19f,.77f,.81f,.84f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.EDITORIAL ->
                FixedCardLayout(rect(.09f,.08f,.91f,.14f), rect(.09f,.16f,.91f,.30f), qr(.50f,.54f,.43f), rect(.24f,.77f,.76f,.84f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.SIGNATURE ->
                FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.30f), qr(.50f,.54f,.43f), rect(.25f,.77f,.75f,.84f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.HERO_TOP ->
                FixedCardLayout(rect(.10f,.43f,.90f,.49f), rect(.10f,.51f,.90f,.65f), qr(.50f,.25f,.38f), rect(.22f,.76f,.78f,.83f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.HERO_BOTTOM ->
                FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.30f), qr(.50f,.69f,.38f), rect(.22f,.87f,.78f,.93f), FixedCardTextAlignment.CENTER)
            FixedCardLayoutVariant.OFFSET_LEFT ->
                FixedCardLayout(rect(.14f,.08f,.88f,.14f), rect(.14f,.16f,.88f,.31f), qr(.34f,.57f,.36f), rect(.48f,.73f,.88f,.80f), FixedCardTextAlignment.LEFT)
            FixedCardLayoutVariant.OFFSET_RIGHT ->
                FixedCardLayout(rect(.12f,.08f,.86f,.14f), rect(.12f,.16f,.86f,.31f), qr(.67f,.57f,.36f), rect(.12f,.73f,.52f,.80f), FixedCardTextAlignment.RIGHT)
            FixedCardLayoutVariant.MAGAZINE ->
                FixedCardLayout(rect(.08f,.07f,.92f,.13f), rect(.08f,.15f,.76f,.29f), qr(.66f,.54f,.38f), rect(.10f,.72f,.54f,.79f), FixedCardTextAlignment.LEFT)
        }
    }
}
