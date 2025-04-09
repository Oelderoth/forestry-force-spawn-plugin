package xyz.oelderoth.runelite.forestry.ui;

import java.awt.Cursor;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import javax.inject.Inject;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.tuple.Pair;
import xyz.oelderoth.runelite.forestry.ForceSpawnService;
import xyz.oelderoth.runelite.forestry.ForestryPlugin;
import xyz.oelderoth.runelite.forestry.ForestryPluginConfig;
import xyz.oelderoth.runelite.forestry.WorldHopService;
import xyz.oelderoth.runelite.forestry.domain.TreeTimer;
import xyz.oelderoth.runelite.forestry.ui.builders.border.BorderBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.component.ClickFilter;
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagConstraintsBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.icons.Icons;

@Getter
public class ForestryPluginPanel extends PluginPanel
{
	private final NavigationButton navigationButton = NavigationButton.builder()
		.icon(ImageUtil.loadImageResource(ForestryPluginPanel.class, "/skill_icons_small/woodcutting.png"))
		.tooltip(ForestryPlugin.PLUGIN_NAME)
		.priority(5)
		.panel(this)
		.build();

	private final CurrentTreePanel currentTreePanel;
	private final Map<Pair<Long, Integer>, TreeTimerPanel> timerPanelsByHash = new HashMap<>();
	private final JLabel timerHint = new LabelBuilder()
		.border(new EmptyBorder(0, PluginScheme.DEFAULT_PADDING, 0,0))
		.font(FontManager.getRunescapeSmallFont())
		.foreground(PluginScheme.HINT_COLOR)
		.html(true)
		.text("Start chopping an eligible tree,<br />then hop worlds or logout to track it")
		.build();

	private final JPanel timerListPanel = new GridBagPanelBuilder()
		.build();

	@Inject
	private ForceSpawnService service;

	@Inject
	private ItemManager itemManager;

	private final WorldHopService worldHopService;
	private final ForestryPluginConfig config;

	private final JLabel currentTreeTitle = new LabelBuilder()
		.font(FontManager.getRunescapeSmallFont())
		.border(new EmptyBorder(1, 0, 2, 0))
		.text("Current Tree")
		.build();

	private final JLabel deleteAllTimersButton = new LabelBuilder()
		.icon(Icons.TRASH)
		.onMouseEntered((e, c) -> c.setIcon(Icons.TRASH_HOVER))
		.onMouseExited((e, c) -> c.setIcon(Icons.TRASH))
		.tooltipText("Delete All Timers")
		.cursor(Cursor.HAND_CURSOR)
		.onClick(ClickFilter.LEFT_CLICK, (e, c) -> {
			if (service.getTreeTimers().isEmpty()) return;

			int confirm = JOptionPane.showConfirmDialog(c,
				"Are you sure you want to clear all active timers?",
				"Warning", JOptionPane.OK_CANCEL_OPTION);

			if (confirm == 0)
				service.getTreeTimers().clear();
		})
		.build();

	@Inject
	public ForestryPluginPanel(CurrentTreePanel currentTreePanel, WorldHopService worldHopService, ForestryPluginConfig config)
	{
		this.currentTreePanel = currentTreePanel;
		this.worldHopService = worldHopService;
		this.config = config;

		var panelTitle = new LabelBuilder()
			.text(ForestryPlugin.PLUGIN_NAME)
			.border(new EmptyBorder(1, 0, PluginScheme.DEFAULT_PADDING, 0))
			.build();

		var timerTitle = new LabelBuilder()
			.font(FontManager.getRunescapeSmallFont())
			.border(new EmptyBorder(1, 0, 2, 0))
			.text("Tracked Trees")
			.build();

		var mainPanel = new GridBagPanelBuilder()
			.background(PluginScheme.BACKGROUND_COLOR)
			.constraints(GridBagConstraintsBuilder.verticalRelative())
			.add(currentTreeTitle)
			.add(currentTreePanel)
			.add(new BorderPanelBuilder()
				.background(PluginScheme.BACKGROUND_COLOR)
				.addWest(timerTitle)
				.addEast(deleteAllTimersButton)
				.build())
			.add(timerHint)
			.add(timerListPanel)
			.build();

		BorderPanelBuilder.fromPanel(this)
			.border(BorderBuilder.empty(PluginScheme.DEFAULT_PADDING))
			.background(PluginScheme.BACKGROUND_COLOR)
			.addNorth(panelTitle)
			.addCenter(mainPanel)
			.build();
	}

	public void update() {
		if (config.showCurrentTree()) {
			currentTreePanel.setVisible(true);
			currentTreeTitle.setVisible(true);
		} else {
			currentTreePanel.setVisible(false);
			currentTreeTitle.setVisible(false);
		}

		var isNoActiveTimers = service.getTreeTimers().isEmpty();

		deleteAllTimersButton.setVisible(!isNoActiveTimers);
		timerHint.setVisible(config.showTimerHint() || isNoActiveTimers);

		var unknownKeys = new HashSet<>(timerPanelsByHash.keySet());
		var panelOrderDirty = false;

		for (var timer : service.getTreeTimers()) {
			var key = Pair.of(timer.getGameObject().getHash(), timer.getWorld());
			unknownKeys.remove(key);
			if (timerPanelsByHash.containsKey(key)) continue;

			var panel = new TreeTimerPanel(timer, itemManager, worldHopService, () -> service.getTreeTimers().remove(timer), config);
			timerPanelsByHash.put(key, panel);

			panelOrderDirty = true;
		}

		for (var removedPanelHash : unknownKeys) {
			var panel = timerPanelsByHash.getOrDefault(removedPanelHash, null);
			if (panel == null) continue;

			timerPanelsByHash.remove(removedPanelHash);

			panelOrderDirty = true;
		}

		if (panelOrderDirty)
			rebuildSortedTimerList();

		timerPanelsByHash.values().forEach(TreeTimerPanel::update);
	}

	public void rebuildSortedTimerList() {
		Function<TreeTimer, Long> extractor;
		switch (config.sortOrder()) {
			case START_TIME:
				extractor = TreeTimer::getStartTimeMs;
				break;
			case REMAINING_TIME:
				var now = Instant.now().toEpochMilli();
				extractor = (it) -> it.getTreeType().getDespawnDurationMs() - (now - it.getStartTimeMs());
				break;
			case WORLD:
				extractor = (it) -> (long) it.getWorld();
				break;
			case TREE_TYPE:
				extractor = (it) -> (long) it.getTreeType().ordinal();
				break;
			default:
				return;
		}

		service.getTreeTimers()
			.sort(Comparator.comparing(extractor));

		timerListPanel.removeAll();

		for (var timer : service.getTreeTimers()) {
			var panel = timerPanelsByHash.get(Pair.of(timer.getGameObject().getHash(), timer.getWorld()));
			if (panel != null)
				timerListPanel.add(panel, GridBagConstraintsBuilder.verticalRelative());
		}

		timerListPanel.revalidate();
		timerListPanel.repaint();
	}
}
