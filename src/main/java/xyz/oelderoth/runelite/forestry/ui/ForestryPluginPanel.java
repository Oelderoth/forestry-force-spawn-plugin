package xyz.oelderoth.runelite.forestry.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.JLabel;
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
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagConstraintsBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagPanelBuilder;

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

	@Inject
	public ForestryPluginPanel(CurrentTreePanel currentTreePanel, WorldHopService worldHopService)
	{
		this.currentTreePanel = currentTreePanel;
		this.worldHopService = worldHopService;

		var panelTitle = new LabelBuilder()
			.text(ForestryPlugin.PLUGIN_NAME)
			.border(new EmptyBorder(0, 0, PluginScheme.DEFAULT_PADDING, 0))
			.build();

		var currentTreeTitle = new LabelBuilder()
			.font(FontManager.getRunescapeSmallFont())
			.text("Current Tree")
			.build();

		var timerTitle = new LabelBuilder()
			.font(FontManager.getRunescapeSmallFont())
			.text("Tracked Trees")
			.build();

		var mainPanel = new GridBagPanelBuilder()
			.background(PluginScheme.BACKGROUND_COLOR)
			.constraints(GridBagConstraintsBuilder.verticalRelative())
			.add(currentTreeTitle)
			.add(currentTreePanel)
			.add(timerTitle)
			.add(timerHint)
			.add(timerListPanel)
			.build();

		BorderPanelBuilder.fromPanel(this)
			.border(new EmptyBorder(10, 10, 10, 10))
			.background(PluginScheme.BACKGROUND_COLOR)
			.addNorth(panelTitle)
			.addCenter(mainPanel)
			.build();
	}

	public void update() {
		var unknownKeys = new HashSet<>(timerPanelsByHash.keySet());

		for (var timer : service.getTreeTimers()) {
			var key = Pair.of(timer.getGameObject().getHash(), timer.getWorld());
			unknownKeys.remove(key);
			if (timerPanelsByHash.containsKey(key)) continue;

			var panel = new TreeTimerPanel(timer, itemManager, worldHopService);
			timerPanelsByHash.put(key, panel);
			timerListPanel.add(panel, GridBagConstraintsBuilder.verticalRelative());

			timerListPanel.revalidate();
			timerListPanel.repaint();
		}

		for (var removedPanelHash : unknownKeys) {
			var panel = timerPanelsByHash.getOrDefault(removedPanelHash, null);
			if (panel == null) continue;

			timerListPanel.remove(panel);
			timerPanelsByHash.remove(removedPanelHash);

			timerListPanel.revalidate();
			timerListPanel.repaint();
		}

		timerPanelsByHash.values().forEach(TreeTimerPanel::update);

	}
}
