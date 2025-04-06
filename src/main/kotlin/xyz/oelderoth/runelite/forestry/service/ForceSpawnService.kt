package xyz.oelderoth.runelite.forestry.service

import com.google.common.collect.ImmutableSet
import net.runelite.api.*
import net.runelite.api.coords.Angle
import net.runelite.api.coords.Direction
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameStateChanged
import net.runelite.api.events.GameTick
import net.runelite.api.events.ScriptPreFired
import net.runelite.client.eventbus.EventBus
import net.runelite.client.eventbus.Subscribe
import org.slf4j.LoggerFactory
import xyz.oelderoth.runelite.forestry.Tree
import xyz.oelderoth.runelite.forestry.Tree.Companion.isTree
import xyz.oelderoth.runelite.forestry.service.ForceSpawnService.PlayerState.*
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForceSpawnService @Inject private constructor(
    private val client: Client,
    private val eventBus: EventBus
) {
    sealed class PlayerState {
        data object NotWoodcutting : PlayerState()
        data class Woodcutting(
            val tree: Tree, val startTick: Int, val startTimeMs: Long, val gameObject: GameObject
        ) : PlayerState()
    }

    var playerState: PlayerState = NotWoodcutting
        private set

    data class TreeTimer(val world: Int, val gameObject: GameObject, val tree: Tree, val startTimeMs: Long)

    fun enable() {
        eventBus.register(this)
    }

    fun disable() {
        eventBus.unregister(this)
    }

    private val _timers = mutableListOf<TreeTimer>()
    val timers: List<TreeTimer>
        get() = _timers

    @Subscribe
    private fun onGameStateChanged(e: GameStateChanged) {
        if (e.gameState != GameState.HOPPING && e.gameState == GameState.LOGIN_SCREEN) return

        val timer = when (val state = playerState) {
            is Woodcutting -> {
                if ((client.tickCount - state.startTick) < 4) return
                TreeTimer(client.world, state.gameObject, state.tree, state.startTimeMs)
            }

            else -> return
        }

        log.info("Starting tree timer: $timer")
        _timers.add(timer)
    }

    @Subscribe
    private fun onGameTick(tick: GameTick) {
        val state = playerState

        if (WOODCUTTING_ANIMATION_IDS.contains(client.localPlayer.animation)) {
            // TODO: Handle off-angle animations better
            val facingTree = getFacingTree(client.localPlayer)

            if (state is Woodcutting) {
                if (state.gameObject.hash == facingTree?.hash) return
                else {
                    onStopCutTree(state.gameObject)
                }
            }

            val treeType = facingTree?.let(Tree.Companion::getTree) ?: return

            onStartCutTree(facingTree, treeType)

        } else if (state is Woodcutting) {
            onStopCutTree(state.gameObject)
        }
    }

    @Subscribe
    fun onScriptPreFired(scriptPreFired: ScriptPreFired) {
        if (scriptPreFired.scriptId == ScriptID.ADD_OVERLAYTIMER_LOC) {
            val args = scriptPreFired.scriptEvent.arguments
            val locCoord = args[1] as Int
            val locType = args[4] as Int

            if (locType == 2) { // Tree despawned
                val worldPoint = WorldPoint.fromCoord(locCoord)
                val eventTree = getTreeFromCoord(worldPoint)
                if (_timers.removeAll { timer ->
                        timer.world == client.world && timer.gameObject.hash == eventTree?.hash
                    }) log.info("Removed timer after tree was cut down")
            }

        }
    }

    private fun getFacingTree(player: Player): GameObject? {
        val worldPoint = player.worldLocation
        val facingPoint = when (Angle(player.orientation).nearestDirection) {
            Direction.NORTH -> worldPoint.dy(1)
            Direction.SOUTH -> worldPoint.dy(-1)
            Direction.EAST -> worldPoint.dx(1)
            Direction.WEST -> worldPoint.dx(-1)
            else -> return null
        }
        return getTreeFromCoord(facingPoint)
    }

    private fun getTreeFromCoord(worldPoint: WorldPoint) =
        LocalPoint.fromWorld(client.topLevelWorldView, worldPoint)?.let { localPoint ->
            val tile = client.topLevelWorldView?.scene?.tiles?.get(worldPoint.plane)?.get(localPoint.sceneX)
                ?.get(localPoint.sceneY)
            val objects = tile?.gameObjects
            val tree = objects?.filterNotNull()?.firstOrNull { it.isTree }
            tree
        }

    private fun onStartCutTree(gameObject: GameObject, tree: Tree) {
        playerState = Woodcutting(tree, client.tickCount, Instant.now().toEpochMilli(), gameObject)
    }

    private fun onStopCutTree(gameObject: GameObject) {
        _timers.removeAll { it.gameObject.hash == gameObject.hash && it.world == client.world }
        playerState = NotWoodcutting
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val WOODCUTTING_ANIMATION_IDS: Set<Int> = ImmutableSet.of(
            AnimationID.WOODCUTTING_BRONZE,
            AnimationID.WOODCUTTING_IRON,
            AnimationID.WOODCUTTING_STEEL,
            AnimationID.WOODCUTTING_BLACK,
            AnimationID.WOODCUTTING_MITHRIL,
            AnimationID.WOODCUTTING_ADAMANT,
            AnimationID.WOODCUTTING_RUNE,
            AnimationID.WOODCUTTING_GILDED,
            AnimationID.WOODCUTTING_DRAGON,
            AnimationID.WOODCUTTING_DRAGON_OR,
            AnimationID.WOODCUTTING_INFERNAL,
            AnimationID.WOODCUTTING_3A_AXE,
            AnimationID.WOODCUTTING_CRYSTAL,
            AnimationID.WOODCUTTING_TRAILBLAZER,
            AnimationID.WOODCUTTING_2H_BRONZE,
            AnimationID.WOODCUTTING_2H_IRON,
            AnimationID.WOODCUTTING_2H_STEEL,
            AnimationID.WOODCUTTING_2H_BLACK,
            AnimationID.WOODCUTTING_2H_MITHRIL,
            AnimationID.WOODCUTTING_2H_ADAMANT,
            AnimationID.WOODCUTTING_2H_RUNE,
            AnimationID.WOODCUTTING_2H_DRAGON,
            AnimationID.WOODCUTTING_2H_CRYSTAL,
            AnimationID.WOODCUTTING_2H_CRYSTAL_INACTIVE,
            AnimationID.WOODCUTTING_2H_3A
        )
    }
}