package xyz.oelderoth.runelite.forestry;

import com.google.inject.Provides;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import xyz.oelderoth.runelite.forestry.ui.ForestryPluginPanel;

@PluginDescriptor(name = ForestryPlugin.PLUGIN_NAME, description = "A plugin to help with force spawning Forestry events")
@Slf4j
public class ForestryPlugin extends Plugin
{
	public static final String PLUGIN_NAME = "Forestry Spawn Helper";

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ForceSpawnService forceSpawnService;

	@Inject
	private ForceSpawnOverlay forceSpawnOverlay;

	@Inject
	private WorldHopService worldHopService;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ScheduledExecutorService executorService;

	@Getter
	private static ForestryPlugin instance;

	private ForestryPluginPanel pluginPanel;
	private ScheduledFuture<?> panelUpdateFuture;

	public ForestryPlugin() {
		instance = this;
	}

	@Override
	protected void startUp()
	{
		// Inject after startup to avoid incorrect default Swing styling
		if (pluginPanel == null) pluginPanel = injector.getInstance(ForestryPluginPanel.class);

		forceSpawnService.enable();
		worldHopService.enable();
		overlayManager.add(forceSpawnOverlay);
		clientToolbar.addNavigation(pluginPanel.getNavigationButton());
		panelUpdateFuture = executorService.scheduleAtFixedRate(pluginPanel::update, 200, 200, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void shutDown()
	{
		forceSpawnService.disable();
		worldHopService.disable();
		overlayManager.remove(forceSpawnOverlay);
		clientToolbar.removeNavigation(pluginPanel.getNavigationButton());
		if (panelUpdateFuture != null)
		{
			panelUpdateFuture.cancel(true);
			panelUpdateFuture = null;
		}
		pluginPanel = null;
	}

	@Provides
	private ForestryPluginConfig getConfig(ConfigManager configManager) { return configManager.getConfig(ForestryPluginConfig.class); }
}
