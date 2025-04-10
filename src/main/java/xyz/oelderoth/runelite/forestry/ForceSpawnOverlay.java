package xyz.oelderoth.runelite.forestry;

import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import javax.inject.Inject;
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
				renderer.drawOutline(wcState.getGameObject(), config.inProgressWidth(), config.inProgressOutline(), config.inProgressFeather());
			}
		}

		long now = Instant.now()
			.toEpochMilli();
		forceSpawnService.getTreeTimers()
			.stream()
			.filter(timer -> timer.getWorld() == client.getWorld())
			.forEach(timer -> {
				if (now - timer.getStartTimeMs() > timer.getTreeType()
					.getDespawnDurationMs())
				{
					if (config.highlightCompleteTree())
						woodcuttingService.getTreeObject(timer.getTree()).ifPresent(obj -> renderer.drawOutline(obj, config.completedWidth(), config.completedOutline(), config.completedFeather()));
				}
				else if (config.drawOutlineInProgressTree())
				{
					woodcuttingService.getTreeObject(timer.getTree()).ifPresent(obj -> renderer.drawOutline(obj, config.inProgressWidth(), config.inProgressOutline(), config.inProgressFeather()));
				}
			});

		return null;
	}
}