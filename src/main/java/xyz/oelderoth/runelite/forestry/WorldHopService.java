package xyz.oelderoth.runelite.forestry;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.World;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.util.WorldUtil;

@Slf4j
@Singleton
public class WorldHopService
{
	private static final int MAX_RETRY_ATTEMPTS = 3;

	@Inject
	private EventBus eventBus;

	@Inject
	private Client client;

	@Inject
	private WorldService worldService;

	public void enable()
	{
		eventBus.register(this);
	}

	public void disable()
	{
		eventBus.unregister(this);
	}

	private World targetWorld = null;
	private int retryAttempts = 0;

	public void hopToWorld(int worldId)
	{
		getWorldById(worldId).ifPresent(world -> targetWorld = world);
	}

	private Optional<World> getWorldById(int worldId)
	{
		return Optional.ofNullable(worldService.getWorlds())
			.map(result -> result.findWorld(worldId))
			.map(apiWorld -> {
				var rsWorld = client.createWorld();
				rsWorld.setActivity(apiWorld.getActivity());
				rsWorld.setAddress(apiWorld.getAddress());
				rsWorld.setId(apiWorld.getId());
				rsWorld.setPlayerCount(apiWorld.getPlayers());
				rsWorld.setLocation(apiWorld.getLocation());
				rsWorld.setTypes(WorldUtil.toWorldTypes(apiWorld.getTypes()));

				return rsWorld;
			});
	}

	private void resetHop()
	{
		targetWorld = null;
		retryAttempts = 0;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (targetWorld == null) return;
		if (client.getWidget(ComponentID.WORLD_SWITCHER_WORLD_LIST) == null)
		{
			client.openWorldHopper();

			if (retryAttempts++ >= MAX_RETRY_ATTEMPTS)
			{
				resetHop();
				log.error("Failed to hop to target world");
			}
		}
		else
		{
			client.hopToWorld(targetWorld);
		}
	}
}
