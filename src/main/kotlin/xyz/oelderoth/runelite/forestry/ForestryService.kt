package xyz.oelderoth.runelite.forestry

import com.google.common.collect.ImmutableSet
import net.runelite.api.*
import net.runelite.api.coords.Angle
import net.runelite.api.coords.Direction
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameStateChanged
import net.runelite.api.events.GameTick
import net.runelite.client.eventbus.EventBus
import net.runelite.client.eventbus.Subscribe
import org.slf4j.LoggerFactory
import xyz.oelderoth.runelite.forestry.Tree.Companion.isTree
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

data class TreeInfo(
    val tree: Tree,
    val world: Int,
    val gameObject: GameObject,
    val startTime: Long,
    val duration: Int
)

@Singleton
class ForestryService @Inject private constructor(
    private val client: Client,
    private val eventBus: EventBus,
) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        private val WOODCUTTING_ANIMS: Set<Int> = ImmutableSet.of(
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

    var playerWoodcutting = false
        private set
    var currentTree: GameObject? = null
        private set
    var woodcuttingTicks: Int = 0
        private set
    private var startCutTime: Instant? = null
    val trackedTrees: MutableList<TreeInfo> = mutableListOf()

    fun enable() = eventBus.register(this)
    fun disable() = eventBus.unregister(this)

    @Subscribe
    private fun onGameTick(tick: GameTick) {
        if (WOODCUTTING_ANIMS.contains(client.localPlayer.animation)) {
            val facingTree = getFacingTree(client.localPlayer)
            when {
                !playerWoodcutting && facingTree != null -> {
                    onStartWoodcutting(facingTree)
                }
                playerWoodcutting && facingTree != null && facingTree != currentTree -> {
                    onStopWoodcutting()
                    onStartWoodcutting(facingTree)
                }
            }
        } else {
            if (playerWoodcutting) onStopWoodcutting()
        }

        if (playerWoodcutting)
            woodcuttingTicks++

        val now = Instant.now().toEpochMilli()
        trackedTrees.filter { it.startTime + it.duration <= now }.forEach {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Forestry Plugin", "${it.tree} on world ${it.world} is ready", null)
        }
        trackedTrees.removeAll { it.startTime + it.duration <= now }
    }

    @Subscribe
    private fun onGameStateChanged(e: GameStateChanged) {
        if (playerWoodcutting && woodcuttingTicks >= 4 && currentTree != null && (e.gameState == GameState.HOPPING || e.gameState == GameState.LOGIN_SCREEN)) {
            val tree = Tree.getTree(currentTree!!)!!
            trackedTrees.add(TreeInfo(tree, client.world, currentTree!!, startCutTime!!.toEpochMilli(), tree.durationMs))
        }
    }

    private fun onStartWoodcutting(tree: GameObject) {
        playerWoodcutting = true
        woodcuttingTicks = 0
        currentTree = tree
        startCutTime = Instant.now()
    }

    private fun onStopWoodcutting() {
        playerWoodcutting = false
        woodcuttingTicks = 0
        currentTree = null
        startCutTime = null
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
        val localPoint = LocalPoint.fromWorld(client.topLevelWorldView, facingPoint)!!
        val tile = client.topLevelWorldView?.scene?.tiles?.get(facingPoint.plane)?.get(localPoint.sceneX)
                    ?.get(localPoint.sceneY)
        val objects = tile?.gameObjects
        val tree = objects?.filterNotNull()?.firstOrNull { it.isTree }
        return tree
    }
}