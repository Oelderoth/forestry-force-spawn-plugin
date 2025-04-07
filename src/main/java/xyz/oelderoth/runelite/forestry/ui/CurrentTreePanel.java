package xyz.oelderoth.runelite.forestry.ui;

import javax.inject.Inject;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import xyz.oelderoth.runelite.forestry.ForceSpawnService;
import xyz.oelderoth.runelite.forestry.domain.PlayerState;
import xyz.oelderoth.runelite.forestry.ui.builders.border.BorderBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagConstraintsBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagPanelBuilder;

public class CurrentTreePanel extends JPanel
{
	@Inject
	private ForceSpawnService service;

	@Inject
	private Client client;

	private final JLabel title = new LabelBuilder().build();

	private final JLabel infoLabel = new LabelBuilder()
		.foreground(PluginScheme.HINT_COLOR)
		.font(FontManager.getRunescapeSmallFont())
		.border(new EmptyBorder(PluginScheme.DEFAULT_PADDING, 0, 0, 0))
		.build();

	public CurrentTreePanel()
	{
		var headerPanel = new BorderPanelBuilder().background(PluginScheme.PANEL_COLOR)
			.border(BorderBuilder.empty(0, PluginScheme.DEFAULT_PADDING))
			.addCenter(title)
			.build();

		var infoPanel = new BorderPanelBuilder().background(PluginScheme.PANEL_COLOR)
			.border(BorderBuilder.separated(0, PluginScheme.DEFAULT_PADDING))
			.addCenter(infoLabel)
			.build();

		GridBagPanelBuilder.fromPanel(this)
			.background(PluginScheme.PANEL_COLOR)
			.border(BorderBuilder.empty(PluginScheme.SMALL_PADDING, 0))
			.constraints(GridBagConstraintsBuilder.verticalRelative())
			.add(headerPanel)
			.add(infoPanel)
			.build();

		update();
	}

	public void update()
	{
		if (service != null && service.getPlayerState() == PlayerState.Woodcutting)
		{
			var wcStatus = service.getWoodcuttingState();
			if (wcStatus != null)
			{
				title.setText("Cutting " + wcStatus.getTreeType() + " Tree");
				var ticks = client.getTickCount() - wcStatus.getStartTick();
				if (ticks < ForceSpawnService.MIN_TICK_COUNT) {
					infoLabel.setText("Cut for " + (ForceSpawnService.MIN_TICK_COUNT - ticks) + " ticks before hopping");
					infoLabel.setForeground(PluginScheme.HINT_COLOR);
				} else {
					infoLabel.setText("Ready to hop");
					infoLabel.setForeground(PluginScheme.SUCCESS_COLOR);
				}
			}
		}
		else
		{
			title.setText("Not Woodcutting");
			infoLabel.setText("Start cutting an eligible tree");
			infoLabel.setForeground(PluginScheme.HINT_COLOR);
		}
	}
}
