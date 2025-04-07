package xyz.oelderoth.runelite.forestry.ui;

import java.awt.GridBagConstraints;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.val;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import xyz.oelderoth.runelite.forestry.ForestryPlugin;
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.ErrorPanelBuilder;
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

	public ForestryPluginPanel()
	{
		val panelTitle = new LabelBuilder()
			.text(ForestryPlugin.PLUGIN_NAME)
			.visible(false)
			.build();

		val errorPanel = new ErrorPanelBuilder()
			.title(ForestryPlugin.PLUGIN_NAME)
			.description("Start chopping an eligible tree, then hop worlds or logout")
			.build();

		val timerPanel = new GridBagPanelBuilder()
			.background(PluginColorScheme.pluginPanelColor)
			.constraints(new GridBagConstraintsBuilder()
				.fill(GridBagConstraints.HORIZONTAL)
				.weightx(1.0)
				.gridx(0)
				.build())
			.add(errorPanel)
			.build();

		BorderPanelBuilder.fromPanel(this)
			.border(new EmptyBorder(10, 10, 10, 10))
			.background(PluginColorScheme.pluginPanelColor)
			.addNorth(panelTitle)
			.addCenter(timerPanel)
			.build();
	}
}
