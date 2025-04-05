package xyz.oelderoth.runelite.forestry

import net.runelite.api.GameObject
import net.runelite.api.ObjectID.*

enum class Tree(private val ids: Set<Int>) {
    Normal(
        setOf(
            TREE,
            TREE_1277,
            TREE_1278,
            TREE_1279,
            TREE_1280,
        )
    ),
    Oak(emptySet());

    companion object {
        private val treeTypeById = entries.flatMap { tree ->
            tree.ids.map { it to tree }
        }.toMap()

        val GameObject.isTree
            get() = treeTypeById.containsKey(this.id)

        fun getTree(gameObject: GameObject) = treeTypeById[gameObject.id]
    }
}