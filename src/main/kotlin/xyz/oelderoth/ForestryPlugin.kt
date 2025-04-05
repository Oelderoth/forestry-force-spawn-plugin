package xyz.oelderoth

import com.google.inject.Provides
import net.runelite.api.ChatMessageType
import net.runelite.api.Client
import net.runelite.api.GameState
import net.runelite.api.events.GameStateChanged
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import org.slf4j.LoggerFactory
import javax.inject.Inject

@PluginDescriptor(name = "Forestry Spawn Helper")
class ForestryPlugin : Plugin() {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var config: PluginConfig

    @Throws(Exception::class)
    override fun startUp() {
        log.info("Example started!")
    }

    @Throws(Exception::class)
    override fun shutDown() {
        log.info("Example stopped!")
    }

    @Subscribe
    fun onGameStateChanged(gameStateChanged: GameStateChanged) {
        if (gameStateChanged.gameState == GameState.LOGGED_IN) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null)
        }
    }

    @Provides
    fun provideConfig(configManager: ConfigManager): PluginConfig {
        return configManager.getConfig(PluginConfig::class.java)
    }
}
