package xyz.oelderoth.runelite.forestry;

import javax.inject.Singleton;
import lombok.val;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import javax.inject.Inject;
import xyz.oelderoth.runelite.forestry.domain.PlayerState;
import xyz.oelderoth.runelite.forestry.ui.PluginScheme;

@Singleton
public class ForceSpawnOverlay extends Overlay {

	@Inject
	private ForceSpawnService forceSpawnService;

	@Inject
	private ModelOutlineRenderer renderer;

	@Inject
	private Client client;

	public ForceSpawnOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_HIGH);
    }

	@Override
    public Dimension render(Graphics2D graphics) {
		val state = forceSpawnService.getPlayerState();
		val wcState = forceSpawnService.getWoodcuttingState();
		if (state == PlayerState.Woodcutting && wcState != null) {
			if (client.getTickCount() - wcState.getStartTick() >= ForceSpawnService.MIN_TICK_COUNT) {
				renderer.drawOutline(wcState.getGameObject(), 1, PluginScheme.INCOMPLETE_COLOR, 0);
			}
		}

        long now = Instant.now().toEpochMilli();
		forceSpawnService.getTreeTimers().stream()
			.filter(timer -> timer.getWorld() == client.getWorld())
			.forEach(timer -> {
				Color color = (now - timer.getStartTimeMs() > timer.getTreeType().getDespawnDurationMs()) ? PluginScheme.SUCCESS_COLOR : PluginScheme.INCOMPLETE_COLOR;
				renderer.drawOutline(timer.getGameObject(), 1, color, 0);
			});

        return null;
    }
}