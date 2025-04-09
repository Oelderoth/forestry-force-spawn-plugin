package xyz.oelderoth.runelite.forestry.service;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.events.ConfigChanged;
import xyz.oelderoth.runelite.forestry.ForestryPluginConfig;
import xyz.oelderoth.runelite.forestry.domain.TreeType;
import xyz.oelderoth.runelite.forestry.domain.TreeTimer;
import xyz.oelderoth.runelite.forestry.ui.CurrentTreePanel;

@Singleton
public class ForceSpawnService
{
	public static final int MIN_TICK_COUNT = 4;

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Inject
	private CurrentTreePanel currentTreePanel;

	@Inject
	private ForestryPluginConfig config;

	@Inject
	private WoodcuttingService woodcuttingService;

	@Getter
	private final List<TreeTimer> treeTimers = new ArrayList<>();

	public void enable()
	{
		eventBus.register(this);
		woodcuttingService.registerTreeCutDownListener(this::removeTimer);
	}

	public void disable()
	{
		eventBus.unregister(this);
		woodcuttingService.unregisterTreeCutDownListener(this::removeTimer);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged e) {
		if (e.getGroup().equals(ForestryPluginConfig.CONFIG_GROUP) && e.getKey().startsWith("track"))
			treeTimers.removeIf(it -> !isTreeTypeEnabled(it.getTreeType()));
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged e)
	{
		var wcState = woodcuttingService.getWoodcuttingState();

		if (wcState == null)
			return;
		if (e.getGameState() != GameState.HOPPING && e.getGameState() != GameState.LOGIN_SCREEN)
			return;
		if ((client.getTickCount() - wcState.getStartTick()) < ForceSpawnService.MIN_TICK_COUNT)
			return;

		treeTimers.add(new TreeTimer(wcState.getGameObject(), wcState.getTreeType(), wcState.getStartTimeMs(), client.getWorld()));
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		currentTreePanel.update();

		if (config.removeTimersAfter() > 0) {
			var now = Instant.now().toEpochMilli();
			treeTimers.removeIf(timer -> (now - timer.getStartTimeMs() - timer.getTreeType().getDespawnDurationMs()) > (config.removeTimersAfter() * 60000L));
		}
	}

	private boolean isTreeTypeEnabled(TreeType treeType) {
		switch (treeType) {
			case Oak:
				return config.trackOakTree();
			case Willow:
				return config.trackWillowTree();
			case Teak:
				return config.trackTeakTree();
			case Maple:
				return config.trackMapleTree();
			case Mahogany:
				return config.trackMahoganyTree();
			case ArcticPine:
				return config.trackArcticPineTree();
			case Yew:
				return config.trackYewTree();
			case Magic:
				return config.trackMagicTree();
			default:
				return false;
		}
	}

	private void removeTimer(GameObject gameObject) {
		treeTimers.removeIf(timer -> timer.getWorld() == client.getWorld() && timer.getGameObject()
			.getHash() == gameObject.getHash());
	}
}