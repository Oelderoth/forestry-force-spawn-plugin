package xyz.oelderoth.runelite.forestry.ui;

import java.awt.Cursor;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.ThinProgressBar;
import xyz.oelderoth.runelite.forestry.ForestryPluginConfig;
import xyz.oelderoth.runelite.forestry.service.WorldHopService;
import xyz.oelderoth.runelite.forestry.domain.TreeTimer;
import xyz.oelderoth.runelite.forestry.ui.builders.border.BorderBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.component.ClickFilter;
import xyz.oelderoth.runelite.forestry.ui.builders.component.LabelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.component.MenuItemBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.BorderPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagConstraintsBuilder;
import xyz.oelderoth.runelite.forestry.ui.builders.panel.GridBagPanelBuilder;
import xyz.oelderoth.runelite.forestry.ui.icons.Icons;

@Slf4j
public class TreeTimerPanel extends JPanel
{
	private final ForestryPluginConfig config;
	private final TreeTimer timer;

	private final JLabel estimateLabel = new LabelBuilder()
		.foreground(PluginScheme.HINT_COLOR)
		.font(FontManager.getRunescapeSmallFont())
		.build();

	private final ThinProgressBar progressBar = new ThinProgressBar();

	public TreeTimerPanel(ForestryPluginConfig config, ItemManager itemManager, WorldHopService hopService, TreeTimer timer, Consumer<JComponent> onDeleteRequested)
	{
		this.config = config;
		this.timer = timer;

		progressBar.setValue(0);
		progressBar.setMaximumValue(timer.getTreeType()
			.getDespawnDurationMs());
		progressBar.setForeground(config.inProgressBorderColor());

		var icon = new LabelBuilder()
			.bounds(0, 0, Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT)
			.icon(itemManager.getImage(timer.getTreeType()
				.getItemId()))
			.build();

		var treeTypeLabel = new LabelBuilder()
			.font(FontManager.getRunescapeSmallFont())
			.text(timer.getTreeType() + " Tree")
			.build();

		var deleteLabel = new LabelBuilder()
			.icon(Icons.TRASH)
			.onMouseEntered((e, c) -> c.setIcon(Icons.TRASH_HOVER))
			.onMouseExited((e, c) -> c.setIcon(Icons.TRASH))
			.onClick((e, c) -> onDeleteRequested.accept(this))
			.cursor(Cursor.HAND_CURSOR)
			.tooltipText("Delete timer")
			.build();

		var worldLabel = new LabelBuilder()
			.foreground(PluginScheme.HINT_COLOR)
			.font(FontManager.getRunescapeSmallFont())
			.text("World " + timer.getWorld())
			.tooltipText("Hop to world " + timer.getWorld())
			.cursor(Cursor.HAND_CURSOR)
			.onMouseEntered((e, c) -> c.setForeground(PluginScheme.HINT_HOVER_COLOR))
			.onMouseExited((e, c) -> c.setForeground(PluginScheme.HINT_COLOR))
			.onMousePressed(ClickFilter.DOUBLE_CLICK, (e, c) -> c.setForeground(PluginScheme.HINT_CLICK_COLOR))
			.onClick(ClickFilter.DOUBLE_CLICK, (e, c) -> hopService.hopToWorld(timer.getWorld()))
			.build();

		var infoPanel = new GridBagPanelBuilder()
			.constraints(GridBagConstraintsBuilder.verticalRelative(2))
			.add(new BorderPanelBuilder()
				.addWest(treeTypeLabel)
				.addEast(worldLabel)
				.build())
			.add(new BorderPanelBuilder()
				.addWest(estimateLabel)
				.addEast(deleteLabel)
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
			.menuItem(new MenuItemBuilder()
				.text("Remove timer")
				.actionListener(e -> onDeleteRequested.accept(this))
				.build())
			.menuItem(new MenuItemBuilder()
				.text("Hop to world " + timer.getWorld())
				.actionListener(e -> hopService.hopToWorld(timer.getWorld()))
				.build())
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
			estimateLabel.setForeground(config.completedBorderColor());

			progressBar.setValue(timer.getTreeType()
				.getDespawnDurationMs());
			progressBar.setForeground(config.completedBorderColor());
		}
		else
		{
			var duration = Duration.of(remaining, ChronoUnit.MILLIS);
			estimateLabel.setText(String.format("Ready in %02d:%02d", duration.toMinutesPart(), duration.toSecondsPart()));
			estimateLabel.setForeground(PluginScheme.HINT_COLOR);

			progressBar.setValue((int) elapsed);
			progressBar.setForeground(config.inProgressBorderColor());
		}
	}
}
