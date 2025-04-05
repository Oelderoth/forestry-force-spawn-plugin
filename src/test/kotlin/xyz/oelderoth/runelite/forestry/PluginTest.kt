package xyz.oelderoth.runelite.forestry

import net.runelite.client.RuneLite
import net.runelite.client.externalplugins.ExternalPluginManager

object PluginTest {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        ExternalPluginManager.loadBuiltin(ForestryPlugin::class.java)
        RuneLite.main(args)
    }
}