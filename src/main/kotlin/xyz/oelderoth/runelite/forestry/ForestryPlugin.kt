package xyz.oelderoth.runelite.forestry

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import org.slf4j.LoggerFactory
import xyz.oelderoth.runelite.forestry.overlay.ForceSpawnOverlay
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService
import javax.inject.Inject

@PluginDescriptor(
    name = ForestryPlugin.PLUGIN_NAME,
    description = "A plugin to help with force spawning Forestry events"
)
class ForestryPlugin : Plugin() {
    companion object {
        const val PLUGIN_NAME = "Forestry Spawn Helper"
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Inject
    lateinit var forceSpawnService: ForceSpawnService

    @Inject
    lateinit var forceSpawnOverlay: ForceSpawnOverlay

    @Inject
    lateinit var overlayManager: OverlayManager

    override fun startUp() {
        forceSpawnService.enable()
        overlayManager.add(forceSpawnOverlay)
    }

    override fun shutDown() {
        forceSpawnService.disable()
        overlayManager.remove(forceSpawnOverlay)
    }
}
