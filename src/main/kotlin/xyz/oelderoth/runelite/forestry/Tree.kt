package xyz.oelderoth.runelite.forestry

import net.runelite.api.GameObject
import net.runelite.api.ObjectID
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

enum class Tree(val durationMs: Int) {
    Oak(8400),
    Willow(8400),
    Teak(9000),
    Maple(35400),
    Mahogany(8400),
    Yew(59400),
    Magic(119400);

    companion object {
        private val treeTypeById: Map<Int, Tree>

        init {
            val treeRegex = """(?:([\w_]+?)_)?TREE_\d+""".toRegex()
            // TODO: Find a more elegant way to populate this than brute forcing reflection
            treeTypeById = ObjectID::class.java.declaredFields
                .filter { Modifier.isStatic(it.modifiers) }
                .filter { it.type == Int::class.java }
                .map { it.name to it.get(null) }
                .mapNotNull { (name, id) ->
                    treeRegex.matchEntire(name)?.let {
                        it.groupValues.getOrNull(1)?.let {
                            Tree.entries.firstOrNull { enumEntry ->
                                enumEntry.name.lowercase() == it.lowercase()
                            }?.let { tree -> id as Int to tree }
                        }
                    }
                }.toMap(mutableMapOf()).also {
                    // For other known tree types that don't have a reflection entry
                    it[10823] = Yew
                }
        }

        val GameObject.isTree
            get() = treeTypeById.containsKey(this.id)

        fun getTree(gameObject: GameObject) = treeTypeById[gameObject.id]
    }
}