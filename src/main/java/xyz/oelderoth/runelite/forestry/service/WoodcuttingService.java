package xyz.oelderoth.runelite.forestry.service;

import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.ScriptID;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import xyz.oelderoth.runelite.forestry.domain.TreeCutDownListener;
import xyz.oelderoth.runelite.forestry.domain.ObjectPosition;
import xyz.oelderoth.runelite.forestry.domain.TreeType;
import xyz.oelderoth.runelite.forestry.domain.WoodcuttingState;
import xyz.oelderoth.runelite.forestry.domain.WoodcuttingStateListener;

@Slf4j
@SuppressWarnings("UnusedReturnValue")
@Singleton
public class WoodcuttingService
{
	private static final Set<Integer> WOODCUTTING_ANIMATION_IDS = Sets.newHashSet(AnimationID.WOODCUTTING_BRONZE, AnimationID.WOODCUTTING_IRON, AnimationID.WOODCUTTING_STEEL, AnimationID.WOODCUTTING_BLACK, AnimationID.WOODCUTTING_MITHRIL, AnimationID.WOODCUTTING_ADAMANT, AnimationID.WOODCUTTING_RUNE, AnimationID.WOODCUTTING_GILDED, AnimationID.WOODCUTTING_DRAGON, AnimationID.WOODCUTTING_DRAGON_OR, AnimationID.WOODCUTTING_INFERNAL, AnimationID.WOODCUTTING_3A_AXE, AnimationID.WOODCUTTING_CRYSTAL, AnimationID.WOODCUTTING_TRAILBLAZER, AnimationID.WOODCUTTING_2H_BRONZE, AnimationID.WOODCUTTING_2H_IRON, AnimationID.WOODCUTTING_2H_STEEL, AnimationID.WOODCUTTING_2H_BLACK, AnimationID.WOODCUTTING_2H_MITHRIL, AnimationID.WOODCUTTING_2H_ADAMANT, AnimationID.WOODCUTTING_2H_RUNE, AnimationID.WOODCUTTING_2H_DRAGON, AnimationID.WOODCUTTING_2H_CRYSTAL, AnimationID.WOODCUTTING_2H_CRYSTAL_INACTIVE, AnimationID.WOODCUTTING_2H_3A);
	private static final int TREE_DESPAWNED_LOC_TYPE = 2;

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Getter
	@Nullable
	private WoodcuttingState woodcuttingState = null;

	private final List<WoodcuttingStateListener> stateChangeListeners = new ArrayList<>();
	private final List<TreeCutDownListener> treeCutDownListeners = new ArrayList<>();
	private final HashMap<Integer, GameObject> objectCache = new HashMap<>();

	public void enable()
	{
		eventBus.register(this);
	}

	public void disable()
	{
		eventBus.unregister(this);
	}

	public void registerStateListener(WoodcuttingStateListener handler)
	{
		stateChangeListeners.add(handler);
	}

	public void unregisterStateListener(WoodcuttingStateListener handler)
	{
		stateChangeListeners.remove(handler);
	}

	public void registerTreeCutDownListener(TreeCutDownListener handler)
	{
		treeCutDownListeners.add(handler);
	}

	public void unregisterTreeCutDownListener(TreeCutDownListener handler)
	{
		treeCutDownListeners.remove(handler);
	}

	public Optional<GameObject> getTreeObject(ObjectPosition tree) {
		var hash = tree.hashCode();
		if (objectCache.containsKey(hash)) return Optional.of(objectCache.get(hash));

		var objOpt = getTreeFromWorldPoint(tree.getPoint());
		objOpt.ifPresent(obj -> objectCache.put(hash, obj));
		return objOpt;
	}

	@Subscribe
	private void onScriptPreFired(ScriptPreFired scriptPreFired)
	{
		if (scriptPreFired.getScriptId() == ScriptID.ADD_OVERLAYTIMER_LOC)
		{
			var args = scriptPreFired.getScriptEvent()
				.getArguments();
			var locCoord = (int) args[1];
			var locType = (int) args[4];

			if (locType == TREE_DESPAWNED_LOC_TYPE)
			{ // Tree despawned
				var worldPoint = WorldPoint.fromCoord(locCoord);
				var eventTreeOpt = getTreeFromWorldPoint(worldPoint);
				eventTreeOpt.ifPresent(this::onTreeCutDown);
			}
		}
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		var player = client.getLocalPlayer();

		if (WOODCUTTING_ANIMATION_IDS.contains(player.getAnimation()))
		{
			// TODO: Handle off-angle animations better
			var facingTree = getFacingTree(player);

			if (woodcuttingState != null)
			{
				var isSameTree = facingTree.map(TileObject::getHash)
					.filter(it -> it == woodcuttingState.getGameObject()
						.getHash())
					.isPresent();
				if (!isSameTree)
				{
					onStopCutTree(woodcuttingState.getGameObject());
					facingTree.ifPresent(this::onStartCutTree);
				}
			}
			else
			{
				facingTree.ifPresent(this::onStartCutTree);
			}
		}
		else if (woodcuttingState != null)
		{
			onStopCutTree(woodcuttingState.getGameObject());
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged e) {
		if (e.getGameState() != GameState.LOGGED_IN)
			objectCache.clear();
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned e) {
		if (TreeType.isTree(e.getGameObject())) {
			var p = new ObjectPosition(e.getGameObject());
			objectCache.remove(p.hashCode());
		}
	}

	private void onStopCutTree(GameObject gameObject)
	{
		woodcuttingState = null;
		stateChangeListeners.forEach(handler -> handler.onWoodcuttingStateChanged(null));
	}

	private void onTreeCutDown(GameObject gameObject)
	{
		treeCutDownListeners.forEach(handler -> handler.onTreeCutDown(gameObject));
	}

	private Optional<GameObject> getTreeFromWorldPoint(WorldPoint worldPoint)
	{
		return Optional.ofNullable(LocalPoint.fromWorld(client.getTopLevelWorldView(), worldPoint))
			.map(localPoint -> client.getTopLevelWorldView()
				.getScene()
				.getTiles()[worldPoint.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()])
			.map(Tile::getGameObjects)
			.flatMap(objects -> Arrays.stream(objects)
				.filter(it -> TreeType.getTreeType(it)
					.isPresent())
				.findFirst());
	}


	private Optional<GameObject> getFacingTree(Player player)
	{
		var facingLocation = getFacingLocation(player);
		return getTreeFromWorldPoint(facingLocation);
	}

	private WorldPoint getFacingLocation(Player player)
	{
		var worldPoint = player.getWorldLocation();
		var direction = new Angle(player.getOrientation()).getNearestDirection();
		switch (direction)
		{
			case NORTH:
				return worldPoint.dy(1);
			case SOUTH:
				return worldPoint.dy(-1);
			case EAST:
				return worldPoint.dx(1);
			case WEST:
				return worldPoint.dx(-1);
			default:
				throw new IllegalStateException("Unexpected value for direction: " + direction);
		}
	}

	private void onStartCutTree(GameObject gameObject)
	{
		TreeType.getTreeType(gameObject)
			.ifPresent(type -> {
				woodcuttingState = new WoodcuttingState(gameObject, type, client.getTickCount(), Instant.now()
					.toEpochMilli());

				stateChangeListeners.forEach(handler -> handler.onWoodcuttingStateChanged(woodcuttingState));
			});
	}
}
