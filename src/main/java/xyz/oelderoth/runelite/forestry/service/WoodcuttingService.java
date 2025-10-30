package xyz.oelderoth.runelite.forestry.service;

import com.google.common.collect.Sets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.ObjectID;
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
	private static final Set<Integer> WOODCUTTING_ANIMATION_IDS = Sets.newHashSet(
		AnimationID.HUMAN_WOODCUTTING_BRONZE_AXE,
		AnimationID.HUMAN_WOODCUTTING_IRON_AXE,
		AnimationID.HUMAN_WOODCUTTING_STEEL_AXE,
		AnimationID.HUMAN_WOODCUTTING_BLACK_AXE,
		AnimationID.HUMAN_WOODCUTTING_MITHRIL_AXE,
		AnimationID.HUMAN_WOODCUTTING_ADAMANT_AXE,
		AnimationID.HUMAN_WOODCUTTING_RUNE_AXE,
		AnimationID.HUMAN_WOODCUTTING_GILDED_AXE,
		AnimationID.HUMAN_WOODCUTTING_DRAGON_AXE,
		AnimationID.HUMAN_WOODCUTTING_TRAILBLAZER_AXE_NO_INFERNAL,
		AnimationID.HUMAN_WOODCUTTING_TRAILBLAZER_RELOADED_AXE_NO_INFERNAL,
		AnimationID.HUMAN_WOODCUTTING_INFERNAL_AXE,
		AnimationID.HUMAN_WOODCUTTING_TRAILBLAZER_AXE,
		AnimationID.HUMAN_WOODCUTTING_TRAILBLAZER_RELOADED_AXE,
		AnimationID.HUMAN_WOODCUTTING_3A_AXE,
		AnimationID.HUMAN_WOODCUTTING_CRYSTAL_AXE,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_BRONZE,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_IRON,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_STEEL,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_BLACK,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_MITHRIL,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_ADAMANT,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_RUNE,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_DRAGON,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_CRYSTAL,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_CRYSTAL_INACTIVE,
		AnimationID.FORESTRY_2H_AXE_CHOPPING_3A
	);

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

	private final Map<String, TreeType> treeTypeByNameCache = new HashMap<>();
	private final Map<Integer, TreeType> treeTypeByIdCache = new HashMap<>();
	private final Set<Integer> checkedObjectIdCache = new HashSet<>();
	private final Set<Integer> excludedTreeIds = new HashSet<>();

	public WoodcuttingService() {
		for (var type : TreeType.values()) {
			treeTypeByNameCache.put(type.getObjectName().toLowerCase(), type);
		}
		excludedTreeIds.addAll(Arrays.asList(
			ObjectID.FARMING_TREE_PATCH_1,
			ObjectID.FARMING_TREE_PATCH_2,
			ObjectID.FARMING_TREE_PATCH_3,
			ObjectID.FARMING_TREE_PATCH_4,
			ObjectID.FARMING_TREE_PATCH_5,
			ObjectID.FARMING_TREE_PATCH_6,
			56953, // Auburnvale Tree Patch,
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_1,
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_2,
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_3,
			ObjectID.FARMING_HARDWOOD_TREE_PATCH_4
		));
	}

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
		if (getTreeType(e.getGameObject()).isPresent()) {
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
				.filter(it -> getTreeType(it)
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
		getTreeType(gameObject)
			.ifPresent(type -> {
				woodcuttingState = new WoodcuttingState(gameObject, type, client.getTickCount(), Instant.now()
					.toEpochMilli());

				stateChangeListeners.forEach(handler -> handler.onWoodcuttingStateChanged(woodcuttingState));
			});
	}

	private Optional<TreeType> getTreeType(GameObject gameObject) {
		if (gameObject == null) return Optional.empty();

		var objectId = gameObject.getId();
		if (excludedTreeIds.contains(objectId)) return Optional.empty();

		var cachedType = treeTypeByIdCache.get(objectId);
		if (cachedType != null) return Optional.of(cachedType);

		// If we don't have a cached match, and we haven't already tried to check if this object is a known tree type,
		// attempt to populate the cache by looking at the name of the object composition (and impostors if present)
		if (checkedObjectIdCache.contains(objectId)) return Optional.empty();
		var composition = client.getObjectDefinition(objectId);
		if (composition != null) {
			var type = treeTypeByNameCache.get(composition.getName().toLowerCase());
			if (type != null) {
				treeTypeByIdCache.put(objectId, type);
			} else {
				var impostorIds = composition.getImpostorIds();
				if (impostorIds != null && impostorIds.length > 0) {
					var impostor = composition.getImpostor();
					var impostorType = treeTypeByNameCache.get(impostor.getName().toLowerCase());
					if (impostorType != null) {
						treeTypeByIdCache.put(objectId, impostorType);
					}
				}
			}
		}

		// Cache the objectId so we don't have to look it up again next time if it wasn't a tree
		checkedObjectIdCache.add(objectId);

		return Optional.ofNullable(treeTypeByIdCache.get(objectId));
	}
}
