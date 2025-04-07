package xyz.oelderoth.runelite.forestry.ui;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import xyz.oelderoth.runelite.forestry.ForestryPlugin;
import xyz.oelderoth.runelite.forestry.domain.TreeTimer;
import xyz.oelderoth.runelite.forestry.domain.TreeType;
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
	private final List<TreeTimerPanel> timerPanels = new ArrayList<>();

	private final JLabel timerHint = new LabelBuilder()
		.border(new EmptyBorder(0, PluginScheme.DEFAULT_PADDING, 0,0))
		.font(FontManager.getRunescapeSmallFont())
		.foreground(PluginScheme.HINT_COLOR)
		.html(true)
		.text("Start chopping an eligible tree,<br />then hop worlds or logout to track it")
		.build();

	@Inject
	public ForestryPluginPanel(CurrentTreePanel currentTreePanel, ItemManager itemManager)
	{
		this.currentTreePanel = currentTreePanel;

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

		var testPanel = new TreeTimerPanel(new TreeTimer(111, null, TreeType.Teak, Instant.now().toEpochMilli()), itemManager);
		timerPanels.add(testPanel);

		var timerPanel = new GridBagPanelBuilder()
			.background(PluginScheme.BACKGROUND_COLOR)
			.constraints(GridBagConstraintsBuilder.verticalRelative())
			.add(currentTreeTitle)
			.add(currentTreePanel)
			.add(timerTitle)
			.add(timerHint)
			.add(testPanel)
			.build();

		BorderPanelBuilder.fromPanel(this)
			.border(new EmptyBorder(10, 10, 10, 10))
			.background(PluginScheme.BACKGROUND_COLOR)
			.addNorth(panelTitle)
			.addCenter(timerPanel)
			.build();
	}

	public void update() {
		currentTreePanel.update();
		timerPanels.forEach(TreeTimerPanel::update);
	}
}
