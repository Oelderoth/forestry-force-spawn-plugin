package xyz.oelderoth.runelite.forestry

import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import org.slf4j.LoggerFactory
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
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var overlay: ForestryOverlay

    @Inject
    lateinit var service: ForestryService

    override fun startUp() {
        overlayManager.add(overlay)
        service.enable()
    }

    override fun shutDown() {
        overlayManager.remove(overlay)
        service.disable()
    }
}
