package xyz.oelderoth.runelite.forestry;

import java.awt.BasicStroke;
import javax.inject.Singleton;
import net.runelite.api.Client;
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
				renderer.drawOutline(wcState.getGameObject(), config.inProgressWidth(), config.inProgressColor(), config.inProgressFeather());
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
				var baseColor = completed ? config.completedColor() : config.inProgressColor();
				var width = completed ? config.completedWidth() : config.inProgressWidth();
				var feather = completed ? config.completedFeather() : config.inProgressFeather();
				var drawOutline = completed ? config.drawOutlineCompleteTree() : config.drawOutlineInProgressTree();
				var drawHull = completed ? config.drawHullCompleteTree() : config.drawHullInProgressTree();
				var drawClickbox = completed ? config.drawClickboxCompleteTree() : config.drawClickboxInProgressTree();
				var drawTile = completed ? config.drawTileCompleteTree() : config.drawTileInProgressTree();

				woodcuttingService.getTreeObject(timer.getTree())
					.ifPresent(obj -> {
						if (drawOutline) {
							renderer.drawOutline(obj, width, baseColor, feather);
						}
						if (drawHull)
						{
							var hull = obj.getConvexHull();
							if (hull != null) {
								// Default to a=50 to match default ObjectIndicators
								var fillColor = ColorUtil.colorWithAlpha(baseColor, 50);
								OverlayUtil.renderPolygon(graphics, hull, baseColor, fillColor, new BasicStroke(width));
							}
						}
						if (drawClickbox) {
							var clickbox = obj.getClickbox();
							if (clickbox != null) {
								// Default to using a/12 to match default ObjectIndicators
								var fillColor = ColorUtil.colorWithAlpha(baseColor, baseColor.getAlpha()/12);
								OverlayUtil.renderPolygon(graphics, clickbox, baseColor, fillColor, new BasicStroke(width));
							}
						}
						if (drawTile) {
							var tile = obj.getCanvasTilePoly();
							if (tile != null) {
								// Default to using a/12 to match default ObjectIndicators
								var fillColor = ColorUtil.colorWithAlpha(baseColor, baseColor.getAlpha()/12);
								OverlayUtil.renderPolygon(graphics, tile, baseColor, fillColor, new BasicStroke(width));
							}
						}
					});
			});

		return null;
	}
}