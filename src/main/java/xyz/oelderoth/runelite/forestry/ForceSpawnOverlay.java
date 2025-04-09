package xyz.oelderoth.runelite.forestry;

import javax.inject.Singleton;
import lombok.val;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import javax.inject.Inject;
import xyz.oelderoth.runelite.forestry.domain.PlayerState;
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService;

@Singleton
public class ForceSpawnOverlay extends Overlay {

	@Inject
	private ForceSpawnService forceSpawnService;

	@Inject
	private ModelOutlineRenderer renderer;

	@Inject
	private Client client;

	@Inject
	private ForestryPluginConfig config;

	public ForceSpawnOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_HIGH);
    }

	@Override
    public Dimension render(Graphics2D graphics) {
		val state = forceSpawnService.getPlayerState();
		val wcState = forceSpawnService.getWoodcuttingState();
		if (state == PlayerState.Woodcutting && wcState != null && config.highlightInProgressTree()) {
			if (client.getTickCount() - wcState.getStartTick() > ForceSpawnService.MIN_TICK_COUNT) {
				renderer.drawOutline(wcState.getGameObject(), config.inProgressWidth(), config.inProgressOutline(), config.inProgressFeather());
			}
		}

        long now = Instant.now().toEpochMilli();
		forceSpawnService.getTreeTimers().stream()
			.filter(timer -> timer.getWorld() == client.getWorld())
			.forEach(timer -> {
				if (now - timer.getStartTimeMs() > timer.getTreeType().getDespawnDurationMs())
				{
					if (config.highlightCompleteTree())
						renderer.drawOutline(timer.getGameObject(), config.completedWidth(), config.completedOutline(), config.completedFeather());
				}
				else if (config.highlightInProgressTree())
				{
					renderer.drawOutline(timer.getGameObject(), config.inProgressWidth(), config.inProgressOutline(), config.inProgressFeather());
				}
			});

        return null;
    }
}