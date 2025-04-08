package xyz.oelderoth.runelite.forestry.ui;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import xyz.oelderoth.runelite.forestry.ForceSpawnService;
import xyz.oelderoth.runelite.forestry.ForestryPluginConfig;
import xyz.oelderoth.runelite.forestry.domain.PlayerState;
import xyz.oelderoth.runelite.forestry.ui.builders.border.BorderBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagConstraintsBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagPanelBuilder;

@Singleton
public class CurrentTreePanel extends JPanel
{
	@Inject
	private ForceSpawnService service;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ForestryPluginConfig config;

	private final JLabel titleLabel = new LabelBuilder()
		.font(FontManager.getRunescapeSmallFont())
		.text("<PLACEHOLDER>")
		.build();

	private final JLabel hintLabel = new LabelBuilder()
		.foreground(PluginScheme.HINT_COLOR)
		.font(FontManager.getRunescapeSmallFont())
		.build();

	private final JLabel icon = new LabelBuilder()
			.text("\u00a0")
			.bounds(0, 0, Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT)
			.preferredSize(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT)
			.build();

	public CurrentTreePanel()
	{
		var infoPanel = new GridBagPanelBuilder()
			.constraints(GridBagConstraintsBuilder.verticalRelative(2))
			.add(titleLabel)
			.add(hintLabel)
			.build();

		BorderPanelBuilder.fromPanel(this)
			.background(PluginScheme.PANEL_COLOR)
			.border(BorderBuilder.empty(PluginScheme.DEFAULT_PADDING))
			.addWest(icon)
			.addCenter(infoPanel)
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
				itemManager.getImage(wcStatus.getTreeType().getItemId()).addTo(icon);
				titleLabel.setText(wcStatus.getTreeType() + " Tree");

				var existingTimerOpt = service.getTreeTimers().stream().filter(it -> it.getGameObject().getHash() == wcStatus.getGameObject().getHash() && it.getWorld() == client.getWorld()).findAny();
				if (existingTimerOpt.isEmpty()) {
					var ticks = client.getTickCount() - wcStatus.getStartTick();
					var remaining = ForceSpawnService.MIN_TICK_COUNT - ticks;
					if (remaining > 0)
					{
						hintLabel.setText("Cut for " + remaining + " ticks before hopping");
						hintLabel.setForeground(PluginScheme.HINT_COLOR);
					}
					else
					{
						hintLabel.setText("Ready to hop");
						hintLabel.setForeground(config.completedOutline());
					}
				} else {
					var timer = existingTimerOpt.get();
					var elapsed = Instant.now().toEpochMilli() - timer.getStartTimeMs();
					var remaining = timer.getTreeType().getDespawnDurationMs() - elapsed;
					if (remaining > 0)
					{
						var duration = Duration.of(remaining, ChronoUnit.MILLIS);
						hintLabel.setText(String.format("Ready to harvest in %02d:%02d", duration.toMinutesPart(), duration.toSecondsPart()));
						hintLabel.setForeground(config.inProgressOutline());
					}
					else
					{
						hintLabel.setText("Ready to harvest");
						hintLabel.setForeground(config.completedOutline());
					}
				}
			}
		}
		else
		{
			titleLabel.setText("Not Woodcutting");
			hintLabel.setText("Start cutting an eligible tree");
			hintLabel.setForeground(PluginScheme.HINT_COLOR);
			icon.setIcon(null);
		}
	}
}
