package xyz.oelderoth.runelite.forestry;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.World;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
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

	@Inject
	private ChatMessageManager chatMessageManager;

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
		var worldOpt = getWorldById(worldId);
		if (worldOpt.isEmpty())
		{
			resetHop();
			return;
		}

		var world = worldOpt.get();

		printMessageToChat(new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Quick-hopping to World ")
			.append(ChatColorType.HIGHLIGHT)
			.append(Integer.toString(world.getId()))
			.append(ChatColorType.NORMAL)
			.append("..")
			.build());

		targetWorld = world;
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

			if (++retryAttempts >= MAX_RETRY_ATTEMPTS)
			{
				resetHop();
				printMessageToChat(new ChatMessageBuilder()
					.append(ChatColorType.NORMAL)
					.append("Failed to quick-hop after ")
					.append(ChatColorType.HIGHLIGHT)
					.append(Integer.toString(retryAttempts))
					.append(ChatColorType.NORMAL)
					.append(" attempts.")
					.build());
			}
		}
		else
		{
			client.hopToWorld(targetWorld);
			targetWorld = null;
		}
	}

	private void printMessageToChat(String chatMessage) {
		chatMessageManager
			.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(chatMessage)
				.build());
	}
}
