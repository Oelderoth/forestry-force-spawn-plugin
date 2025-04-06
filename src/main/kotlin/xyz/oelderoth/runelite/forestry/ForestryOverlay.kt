package xyz.oelderoth.runelite.forestry

import net.runelite.client.ui.ColorScheme
import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayLayer
import net.runelite.client.ui.overlay.OverlayPosition
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer
import java.awt.Dimension
import java.awt.Graphics2D
import javax.inject.Inject

class ForestryOverlay @Inject private constructor(
    private val service: ForestryService,
    private val renderer: ModelOutlineRenderer
) : Overlay() {
    init {
        position = OverlayPosition.DYNAMIC
        layer = OverlayLayer.ABOVE_SCENE
        priority = PRIORITY_HIGH
    }

    override fun render(graphics: Graphics2D?): Dimension? {
        service.currentTree?.takeIf { service.woodcuttingTicks >= 4 }?.let {
            renderer.drawOutline(it, 1, ColorScheme.PROGRESS_INPROGRESS_COLOR, 0)
        }

        return null
    }
}