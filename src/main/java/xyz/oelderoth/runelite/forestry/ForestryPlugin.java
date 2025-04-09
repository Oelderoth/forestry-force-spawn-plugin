package xyz.oelderoth.runelite.forestry;

import com.google.inject.Provides;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService;
import xyz.oelderoth.runelite.forestry.service.WoodcuttingService;
import xyz.oelderoth.runelite.forestry.service.WorldHopService;
import xyz.oelderoth.runelite.forestry.ui.ForestryPluginPanel;

@PluginDescriptor(name = ForestryPlugin.PLUGIN_NAME, description = "A plugin to help with force spawning Forestry events")
@Slf4j
public class ForestryPlugin extends Plugin
{
	public static final String PLUGIN_NAME = "Forestry Spawn Helper";

	@Getter
	private static ForestryPlugin instance;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ScheduledExecutorService executorService;

	@Inject
	private ForceSpawnOverlay forceSpawnOverlay;

	@Inject
	private ForceSpawnService forceSpawnService;

	@Inject
	private WoodcuttingService woodcuttingService;

	@Inject
	private WorldHopService worldHopService;

	private ForestryPluginPanel pluginPanel;
	private ScheduledFuture<?> panelUpdateFuture;

	public ForestryPlugin()
	{
		instance = this;
	}

	@Override
	protected void startUp()
	{
		// Inject after startup to avoid incorrect default Swing styling
		if (pluginPanel == null) pluginPanel = injector.getInstance(ForestryPluginPanel.class);

		forceSpawnService.enable();
		worldHopService.enable();
		woodcuttingService.enable();
		overlayManager.add(forceSpawnOverlay);
		clientToolbar.addNavigation(pluginPanel.getNavigationButton());
		panelUpdateFuture = executorService.scheduleAtFixedRate(pluginPanel::update, 200, 200, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void shutDown()
	{
		forceSpawnService.disable();
		worldHopService.disable();
		woodcuttingService.disable();
		overlayManager.remove(forceSpawnOverlay);
		clientToolbar.removeNavigation(pluginPanel.getNavigationButton());
		if (panelUpdateFuture != null)
		{
			panelUpdateFuture.cancel(true);
			panelUpdateFuture = null;
		}
		pluginPanel = null;
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged e)
	{
		if (e.getGroup().equals(ForestryPluginConfig.CONFIG_GROUP) && e.getKey().equals("sortOrder"))
			pluginPanel.rebuildSortedTimerList();
	}

	@Provides
	private ForestryPluginConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ForestryPluginConfig.class);
	}
}
