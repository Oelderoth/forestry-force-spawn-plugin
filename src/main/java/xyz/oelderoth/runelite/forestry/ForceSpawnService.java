package xyz.oelderoth.runelite.forestry;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.val;
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
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Singleton;
import xyz.oelderoth.runelite.forestry.domain.PlayerState;
import xyz.oelderoth.runelite.forestry.domain.TreeType;
import xyz.oelderoth.runelite.forestry.domain.TreeTimer;
import xyz.oelderoth.runelite.forestry.domain.WoodcuttingState;
import xyz.oelderoth.runelite.forestry.ui.CurrentTreePanel;

@Singleton
public class ForceSpawnService
{
	@Inject
	private CurrentTreePanel currentTreePanel;

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Getter
	private PlayerState playerState = PlayerState.NotWoodcutting;

	@Getter
	@Nullable
	private WoodcuttingState woodcuttingState = null;

	public void enable()
	{
		eventBus.register(this);
	}

	public void disable()
	{
		eventBus.unregister(this);
	}

	@Getter
	private final List<TreeTimer> treeTimers = new ArrayList<>();

	@Subscribe
	private void onGameStateChanged(GameStateChanged e)
	{
		if (e.getGameState() != GameState.HOPPING && e.getGameState() == GameState.LOGIN_SCREEN)
			return;
		if (playerState == PlayerState.NotWoodcutting || woodcuttingState == null)
			return;
		if ((client.getTickCount() - woodcuttingState.getStartTick()) < ForceSpawnService.MIN_TICK_COUNT)
			return;

		treeTimers.add(new TreeTimer(client.getWorld(), woodcuttingState.getGameObject(), woodcuttingState.getTreeType(), woodcuttingState.getStartTimeMs()));
		onStopCutTree();
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		val player = client.getLocalPlayer();

		if (WOODCUTTING_ANIMATION_IDS.contains(player.getAnimation()))
		{
			// TODO: Handle off-angle animations better
			val facingTree = getFacingTree(player);

			if (playerState == PlayerState.Woodcutting && woodcuttingState != null)
			{
				val isSameTree = facingTree.map(TileObject::getHash)
					.filter(it -> it == woodcuttingState.getGameObject()
						.getHash())
					.isPresent();
				if (!isSameTree)
				{
					removeTimer(woodcuttingState.getGameObject());
					onStopCutTree();
					facingTree.ifPresent(this::onStartCutTree);
				}
			}
			else
			{
				facingTree.ifPresent(this::onStartCutTree);
			}
		}
		else if (playerState == PlayerState.Woodcutting && woodcuttingState != null)
		{
			removeTimer(woodcuttingState.getGameObject());
			onStopCutTree();
		}

		currentTreePanel.update();
	}

	@Subscribe
	private void onScriptPreFired(ScriptPreFired scriptPreFired)
	{
		if (scriptPreFired.getScriptId() == ScriptID.ADD_OVERLAYTIMER_LOC)
		{
			val args = scriptPreFired.getScriptEvent()
				.getArguments();
			val locCoord = (int) args[1];
			val locType = (int) args[4];

			if (locType == TREE_DESPAWNED_LOC_TYPE)
			{ // Tree despawned
				val worldPoint = WorldPoint.fromCoord(locCoord);
				val eventTreeOpt = getTreeFromCoord(worldPoint);
				eventTreeOpt.ifPresent(this::removeTimer);
			}
		}
	}

	private Optional<GameObject> getFacingTree(Player player)
	{
		val facingLocation = getFacingLocation(player);
		return getTreeFromCoord(facingLocation);
	}

	private WorldPoint getFacingLocation(Player player)
	{
		val worldPoint = player.getWorldLocation();
		val direction = new Angle(player.getOrientation()).getNearestDirection();
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

	private Optional<GameObject> getTreeFromCoord(WorldPoint worldPoint)
	{
		return Optional.ofNullable(
				LocalPoint.fromWorld(client.getTopLevelWorldView(), worldPoint))
			.map(localPoint -> client.getTopLevelWorldView()
				.getScene()
				.getTiles()[worldPoint.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()])
			.map(Tile::getGameObjects)
			.flatMap(objects -> Arrays.stream(objects)
				.filter(it -> TreeType.getTreeTypeOf(it)
					.isPresent())
				.findFirst());
	}

	private void onStartCutTree(GameObject gameObject)
	{
		val tree = TreeType.getTreeTypeOf(gameObject);
		if (tree.isEmpty())
		{
			return;
		}

		playerState = PlayerState.Woodcutting;
		woodcuttingState = new WoodcuttingState(tree.get(), gameObject, client.getTickCount(), Instant.now()
			.toEpochMilli());
	}

	private void onStopCutTree()
	{
		playerState = PlayerState.NotWoodcutting;
		woodcuttingState = null;
	}

	private void removeTimer(GameObject gameObject) {
		treeTimers.removeIf(timer -> timer.getWorld() == client.getWorld() && timer.getGameObject()
			.getHash() == gameObject.getHash());
	}

	public static final int MIN_TICK_COUNT = 4;
	private static final Set<Integer> WOODCUTTING_ANIMATION_IDS = Sets.newHashSet(AnimationID.WOODCUTTING_BRONZE, AnimationID.WOODCUTTING_IRON, AnimationID.WOODCUTTING_STEEL, AnimationID.WOODCUTTING_BLACK, AnimationID.WOODCUTTING_MITHRIL, AnimationID.WOODCUTTING_ADAMANT, AnimationID.WOODCUTTING_RUNE, AnimationID.WOODCUTTING_GILDED, AnimationID.WOODCUTTING_DRAGON, AnimationID.WOODCUTTING_DRAGON_OR, AnimationID.WOODCUTTING_INFERNAL, AnimationID.WOODCUTTING_3A_AXE, AnimationID.WOODCUTTING_CRYSTAL, AnimationID.WOODCUTTING_TRAILBLAZER, AnimationID.WOODCUTTING_2H_BRONZE, AnimationID.WOODCUTTING_2H_IRON, AnimationID.WOODCUTTING_2H_STEEL, AnimationID.WOODCUTTING_2H_BLACK, AnimationID.WOODCUTTING_2H_MITHRIL, AnimationID.WOODCUTTING_2H_ADAMANT, AnimationID.WOODCUTTING_2H_RUNE, AnimationID.WOODCUTTING_2H_DRAGON, AnimationID.WOODCUTTING_2H_CRYSTAL, AnimationID.WOODCUTTING_2H_CRYSTAL_INACTIVE, AnimationID.WOODCUTTING_2H_3A);
	private static final int TREE_DESPAWNED_LOC_TYPE = 2;
}