package xyz.oelderoth.runelite.forestry;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

@PluginDescriptor(
    name = ForestryPlugin.PLUGIN_NAME,
    description = "A plugin to help with force spawning Forestry events"
)
@Slf4j
public class ForestryPlugin extends Plugin {
	static final String PLUGIN_NAME = "Forestry Spawn Helper";

    @Inject
    private ForceSpawnService forceSpawnService;

    @Inject
    private ForceSpawnOverlay forceSpawnOverlay;

    @Inject
    private OverlayManager overlayManager;

	@Override
	protected void startUp() {
		log.info("Starting Forestry Plugin");
        forceSpawnService.enable();
        overlayManager.add(forceSpawnOverlay);
    }

	@Override
    public void shutDown() {
        forceSpawnService.disable();
        overlayManager.remove(forceSpawnOverlay);
    }
}
