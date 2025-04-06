package xyz.oelderoth.runelite.forestry;

import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService;
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService.PlayerState.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import javax.inject.Inject;

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
		ForceSpawnService.PlayerState state = forceSpawnService.getPlayerState();
		if (state instanceof Woodcutting) {
			Woodcutting wcState = (Woodcutting) state;
			if (client.getTickCount() - wcState.getStartTick() >= 4) {
				renderer.drawOutline(wcState.getGameObject(), 1, Color.BLUE, 0);
			}
		}

        long now = Instant.now().toEpochMilli();
		forceSpawnService.getTimers().stream()
			.filter(timer -> timer.getWorld() == client.getWorld())
			.forEach(timer -> {
				Color color = (now - timer.getStartTimeMs() > timer.getTree().getDurationMs()) ? Color.GREEN : Color.YELLOW;
				renderer.drawOutline(timer.getGameObject(), 1, color, 0);
			});

        return null;
    }
}