package com.elxvro.scan.qrcard

import kotlin.math.min

data class FixedCardLayout(
    val brandRect: LayoutRect,
    val titleRect: LayoutRect,
    val qrRect: LayoutRect,
    val ctaRect: LayoutRect
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
            FixedCardLayoutVariant.TEXT_LEFT_QR_RIGHT -> FixedCardLayout(rect(.07f,.10f,.48f,.20f), rect(.07f,.22f,.52f,.67f), qr(.79f,.43f,.48f), rect(.66f,.73f,.92f,.86f))
            FixedCardLayoutVariant.QR_LEFT_TEXT_RIGHT -> FixedCardLayout(rect(.54f,.10f,.92f,.20f), rect(.54f,.22f,.93f,.67f), qr(.24f,.43f,.48f), rect(.56f,.73f,.88f,.86f))
            FixedCardLayoutVariant.CENTER_STACK -> FixedCardLayout(rect(.08f,.09f,.92f,.17f), rect(.08f,.19f,.58f,.42f), qr(.77f,.50f,.43f), rect(.12f,.72f,.46f,.84f))
            FixedCardLayoutVariant.TOP_COPY_BOTTOM_QR -> FixedCardLayout(rect(.07f,.08f,.55f,.16f), rect(.07f,.18f,.60f,.43f), qr(.78f,.57f,.39f), rect(.10f,.67f,.42f,.80f))
            FixedCardLayoutVariant.SPLIT_BAND -> FixedCardLayout(rect(.08f,.12f,.42f,.20f), rect(.08f,.25f,.45f,.63f), qr(.74f,.49f,.52f), rect(.08f,.72f,.38f,.84f))
            FixedCardLayoutVariant.CORNER_QR -> FixedCardLayout(rect(.07f,.10f,.60f,.18f), rect(.07f,.22f,.62f,.58f), qr(.82f,.67f,.33f), rect(.08f,.70f,.41f,.83f))
            FixedCardLayoutVariant.FLOATING_QR -> FixedCardLayout(rect(.08f,.10f,.48f,.18f), rect(.08f,.23f,.48f,.64f), qr(.73f,.40f,.42f), rect(.60f,.70f,.90f,.83f))
            FixedCardLayoutVariant.SIDE_RAIL -> FixedCardLayout(rect(.12f,.12f,.46f,.20f), rect(.12f,.24f,.50f,.66f), qr(.78f,.48f,.44f), rect(.12f,.72f,.44f,.84f))
            FixedCardLayoutVariant.EDITORIAL -> FixedCardLayout(rect(.06f,.08f,.38f,.15f), rect(.06f,.18f,.50f,.54f), qr(.76f,.46f,.46f), rect(.06f,.63f,.36f,.76f))
            FixedCardLayoutVariant.SIGNATURE -> FixedCardLayout(rect(.10f,.14f,.46f,.22f), rect(.10f,.27f,.50f,.62f), qr(.76f,.44f,.45f), rect(.60f,.72f,.91f,.85f))
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
            FixedCardLayoutVariant.TEXT_LEFT_QR_RIGHT -> FixedCardLayout(rect(.10f,.07f,.90f,.13f), rect(.10f,.15f,.90f,.29f), qr(.50f,.53f,.52f), rect(.23f,.75f,.77f,.82f))
            FixedCardLayoutVariant.QR_LEFT_TEXT_RIGHT -> FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.30f), qr(.50f,.56f,.48f), rect(.18f,.78f,.82f,.85f))
            FixedCardLayoutVariant.CENTER_STACK -> FixedCardLayout(rect(.12f,.07f,.88f,.13f), rect(.12f,.15f,.88f,.27f), qr(.50f,.50f,.50f), rect(.24f,.70f,.76f,.77f))
            FixedCardLayoutVariant.TOP_COPY_BOTTOM_QR -> FixedCardLayout(rect(.10f,.06f,.90f,.12f), rect(.10f,.14f,.90f,.26f), qr(.50f,.55f,.44f), rect(.20f,.74f,.80f,.81f))
            FixedCardLayoutVariant.SPLIT_BAND -> FixedCardLayout(rect(.10f,.09f,.90f,.15f), rect(.10f,.17f,.90f,.31f), qr(.50f,.58f,.46f), rect(.17f,.80f,.83f,.87f))
            FixedCardLayoutVariant.CORNER_QR -> FixedCardLayout(rect(.09f,.07f,.91f,.13f), rect(.09f,.15f,.91f,.31f), qr(.67f,.56f,.40f), rect(.10f,.72f,.49f,.79f))
            FixedCardLayoutVariant.FLOATING_QR -> FixedCardLayout(rect(.10f,.08f,.90f,.14f), rect(.10f,.16f,.90f,.29f), qr(.38f,.55f,.42f), rect(.52f,.68f,.89f,.75f))
            FixedCardLayoutVariant.SIDE_RAIL -> FixedCardLayout(rect(.16f,.08f,.90f,.14f), rect(.16f,.16f,.90f,.30f), qr(.52f,.55f,.45f), rect(.19f,.77f,.81f,.84f))
            FixedCardLayoutVariant.EDITORIAL -> FixedCardLayout(rect(.08f,.06f,.92f,.12f), rect(.08f,.14f,.92f,.28f), qr(.50f,.53f,.48f), rect(.08f,.72f,.53f,.79f))
            FixedCardLayoutVariant.SIGNATURE -> FixedCardLayout(rect(.12f,.08f,.88f,.14f), rect(.12f,.16f,.88f,.29f), qr(.50f,.54f,.46f), rect(.25f,.75f,.75f,.82f))
        }
    }
}
