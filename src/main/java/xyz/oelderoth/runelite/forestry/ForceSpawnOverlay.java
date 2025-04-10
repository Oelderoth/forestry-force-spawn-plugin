package xyz.oelderoth.runelite.forestry;

import com.google.common.base.MoreObjects;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.client.util.ColorUtil;
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService;
import xyz.oelderoth.runelite.forestry.service.WoodcuttingService;

@Singleton
public class ForceSpawnOverlay extends Overlay
{

	@Inject
	private Client client;

	@Inject
	private ModelOutlineRenderer renderer;

	@Inject
	private ForestryPluginConfig config;

	@Inject
	private ForceSpawnService forceSpawnService;

	@Inject
	private WoodcuttingService woodcuttingService;

	public ForceSpawnOverlay()
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		var wcState = woodcuttingService.getWoodcuttingState();
		if (wcState != null && config.drawOutlineInProgressTree())
		{
			if (client.getTickCount() - wcState.getStartTick() > ForceSpawnService.MIN_TICK_COUNT)
			{
				renderHighlight(graphics,
					wcState.getGameObject(),
					config.inProgressBorderColor(),
					config.inProgressFillColor(),
					config.inProgressWidth(),
					config.completedFeather(),
					config.drawOutlineInProgressTree(),
					config.drawHullInProgressTree(),
					config.drawClickboxInProgressTree(),
					config.drawTileInProgressTree()
				);
			}
		}

		long now = Instant.now()
			.toEpochMilli();
		forceSpawnService.getTreeTimers()
			.stream()
			.filter(timer -> timer.getWorld() == client.getWorld())
			.forEach(timer -> {
				var completed = (now - timer.getStartTimeMs() > timer.getTreeType()
					.getDespawnDurationMs());
				var borderColor = completed ? config.completedBorderColor() : config.inProgressBorderColor();
				var fillColor = completed ? config.completedFillColor() : config.inProgressFillColor();
				var width = completed ? config.completedWidth() : config.inProgressWidth();
				var feather = completed ? config.completedFeather() : config.inProgressFeather();
				var drawOutline = completed ? config.drawOutlineCompleteTree() : config.drawOutlineInProgressTree();
				var drawHull = completed ? config.drawHullCompleteTree() : config.drawHullInProgressTree();
				var drawClickbox = completed ? config.drawClickboxCompleteTree() : config.drawClickboxInProgressTree();
				var drawTile = completed ? config.drawTileCompleteTree() : config.drawTileInProgressTree();

				woodcuttingService.getTreeObject(timer.getTree())
					.ifPresent(obj -> renderHighlight(graphics, obj, borderColor, fillColor, width, feather, drawOutline, drawHull, drawClickbox, drawTile));
			});

		return null;
	}

	private void renderHighlight(Graphics2D graphics, GameObject obj, Color borderColor, Color fillColor, int width, int feather, boolean drawOutline, boolean drawHull, boolean drawClickbox, boolean drawTile)
	{
		if (drawOutline)
		{
			renderer.drawOutline(obj, width, borderColor, feather);
		}
		if (drawHull)
		{
			var hull = obj.getConvexHull();
			if (hull != null)
			{
				// Default to a=50 to match default ObjectIndicators
				var color = MoreObjects.firstNonNull(fillColor, new Color(0, 0, 0, 50));
				OverlayUtil.renderPolygon(graphics, hull, borderColor, color, new BasicStroke(width));
			}
		}
		if (drawClickbox)
		{
			var clickbox = obj.getClickbox();
			if (clickbox != null)
			{
				// Default to using a/12 to match default ObjectIndicators
				var color = MoreObjects.firstNonNull(fillColor, ColorUtil.colorWithAlpha(borderColor, borderColor.getAlpha() / 12));
				OverlayUtil.renderPolygon(graphics, clickbox, borderColor, color, new BasicStroke(width));
			}
		}
		if (drawTile)
		{
			var tile = obj.getCanvasTilePoly();
			if (tile != null)
			{
				// Default to using a/12 to match default ObjectIndicators
				var color = MoreObjects.firstNonNull(fillColor, ColorUtil.colorWithAlpha(borderColor, borderColor.getAlpha() / 12));
				OverlayUtil.renderPolygon(graphics, tile, borderColor, color, new BasicStroke(width));
			}
		}
	}
}