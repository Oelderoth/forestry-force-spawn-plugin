package xyz.oelderoth.runelite.forestry.ui;

import java.awt.Cursor;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.ThinProgressBar;
import xyz.oelderoth.runelite.forestry.WorldHopService;
import xyz.oelderoth.runelite.forestry.domain.TreeTimer;
import xyz.oelderoth.runelite.forestry.ui.builders.border.BorderBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagConstraintsBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagPanelBuilder;

@Slf4j
public class TreeTimerPanel extends JPanel
{
	private final TreeTimer timer;

	private final JLabel estimateLabel = new LabelBuilder()
		.foreground(PluginScheme.HINT_COLOR)
		.font(FontManager.getRunescapeSmallFont())
		.build();

	private final ThinProgressBar progressBar = new ThinProgressBar();

	public TreeTimerPanel(TreeTimer timer, ItemManager itemManager, WorldHopService hopService)
	{
		this.timer = timer;
		progressBar.setValue(0);
		progressBar.setMaximumValue(timer.getTreeType()
			.getDespawnDurationMs());
		progressBar.setForeground(PluginScheme.INCOMPLETE_COLOR);

		var icon = new LabelBuilder()
			.bounds(0, 0, Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT)
			.icon(itemManager.getImage(timer.getTreeType()
				.getItemId()))
			.build();

		var treeTypeLabel = new LabelBuilder()
			.font(FontManager.getRunescapeSmallFont())
			.text(timer.getTreeType() + " Tree")
			.build();

		var worldLabel = new LabelBuilder()
			.foreground(PluginScheme.HINT_COLOR)
			.font(FontManager.getRunescapeSmallFont())
			.text("World " + timer.getWorld())
			.tooltipText("Hop to world " + timer.getWorld())
			.cursor(Cursor.HAND_CURSOR)
			.onHover((label, hovering) -> label.setForeground(hovering ? PluginScheme.HINT_HOVER_COLOR : PluginScheme.HINT_COLOR))
			.onLeftClick(() -> hopService.hopToWorld(timer.getWorld()))
			.build();

		var infoPanel = new GridBagPanelBuilder()
			.constraints(GridBagConstraintsBuilder.verticalRelative(2))
			.add(treeTypeLabel)
			.add(new BorderPanelBuilder()
				.addWest(estimateLabel)
				.addEast(worldLabel)
				.build())
			.build();

		var mainPanel = new BorderPanelBuilder()
			.background(PluginScheme.PANEL_COLOR)
			.border(BorderBuilder.empty(PluginScheme.DEFAULT_PADDING))
			.addWest(icon)
			.addCenter(infoPanel)
			.build();

		BorderPanelBuilder.fromPanel(this)
			.addCenter(mainPanel)
			.addSouth(progressBar)
			.build();

		update();
	}

	public void update()
	{
		var now = Instant.now()
			.toEpochMilli();
		var elapsed = now - timer.getStartTimeMs();
		var remaining = timer.getTreeType()
			.getDespawnDurationMs() - elapsed;

		if (remaining <= 0)
		{
			estimateLabel.setText("Ready to harvest");
			estimateLabel.setForeground(PluginScheme.SUCCESS_COLOR);

			progressBar.setValue(timer.getTreeType()
				.getDespawnDurationMs());
			progressBar.setForeground(PluginScheme.SUCCESS_COLOR);
		}
		else
		{
			var duration = Duration.of(remaining, ChronoUnit.MILLIS);
			estimateLabel.setText(String.format("Ready in %02d:%02d", duration.toMinutesPart(), duration.toSecondsPart()));
			estimateLabel.setForeground(PluginScheme.HINT_COLOR);

			progressBar.setValue((int) elapsed);
			progressBar.setForeground(PluginScheme.INCOMPLETE_COLOR);
		}
	}
}
