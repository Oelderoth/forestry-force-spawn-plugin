package xyz.oelderoth.runelite.forestry;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import xyz.oelderoth.runelite.forestry.ui.ForestryPluginPanel;

@PluginDescriptor(
    name = ForestryPlugin.PLUGIN_NAME,
    description = "A plugin to help with force spawning Forestry events"
)
@Slf4j
public class ForestryPlugin extends Plugin {
	public static final String PLUGIN_NAME = "Forestry Spawn Helper";

	@Inject
	private ClientToolbar clientToolbar;

    @Inject
    private ForceSpawnService forceSpawnService;

    @Inject
    private ForceSpawnOverlay forceSpawnOverlay;

    @Inject
    private OverlayManager overlayManager;

	private ForestryPluginPanel pluginPanel;

	@Override
	protected void startUp() {
		// Instantiate after startup to avoid incorrect default Swing styling
		pluginPanel = new ForestryPluginPanel();

		forceSpawnService.enable();
        overlayManager.add(forceSpawnOverlay);
		clientToolbar.addNavigation(pluginPanel.getNavigationButton());
    }

	@Override
    public void shutDown() {
        forceSpawnService.disable();
        overlayManager.remove(forceSpawnOverlay);
		clientToolbar.removeNavigation(pluginPanel.getNavigationButton());
		pluginPanel = null;
    }
}
