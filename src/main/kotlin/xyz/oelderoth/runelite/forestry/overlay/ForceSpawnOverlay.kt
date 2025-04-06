package xyz.oelderoth.runelite.forestry.overlay

import net.runelite.api.Client
import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayLayer
import net.runelite.client.ui.overlay.OverlayPosition
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService.PlayerState.*
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.time.Instant
import javax.inject.Inject

class ForceSpawnOverlay @Inject private constructor(
    private val forceSpawnService: ForceSpawnService,
    private val renderer: ModelOutlineRenderer,
    private val client: Client
) : Overlay() {
    init {
        position = OverlayPosition.DYNAMIC
        layer = OverlayLayer.ABOVE_SCENE
        priority = PRIORITY_HIGH
    }

    override fun render(graphics: Graphics2D?): Dimension? {
        (forceSpawnService.playerState as? Woodcutting)?.let {
            if (client.tickCount - it.startTick >= 4) {
                renderer.drawOutline(it.gameObject, 1, Color.BLUE, 0)
            }
        }

        val now = Instant.now().toEpochMilli()
        forceSpawnService.timers.filter { it.world == client.world }.forEach {
            val color = if (now - it.startTimeMs > it.tree.durationMs) Color.GREEN else Color.YELLOW
            renderer.drawOutline(it.gameObject, 1, color, 0)
        }

        return null
    }
}