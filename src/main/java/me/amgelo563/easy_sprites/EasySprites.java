package me.amgelo563.easy_sprites;

import me.amgelo563.easy_sprites.config.Config;
import me.amgelo563.easy_sprites.config.ConfigReader;
import me.amgelo563.easy_sprites.mc.MC;
import me.amgelo563.easy_sprites.replace.ReplacementService;
import me.amgelo563.easy_sprites.sprite.SpriteComponentsService;
import me.amgelo563.easy_sprites.views.chat.ChatEventListener;
import me.amgelo563.easy_sprites.views.sign.SignChangeListener;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

public final class EasySprites extends JavaPlugin {
    private final static String PERMISSION = "easy_sprites.replace";

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        PluginManager pluginManager = getServer().getPluginManager();

        if (MC.version().isOlderThan(MC.V_1_21_9)) {
            logger.severe("This plugin requires Minecraft version 1.21.9 or newer.");
            pluginManager.disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        Config config = ConfigReader.read(getConfig());

        Map<String, Component> components = new SpriteComponentsService(getDataPath(), logger).fetchComponents();
        ReplacementService replacementService = new ReplacementService(components, config.permission() ? PERMISSION : null);

        if (config.replaceSigns()) {
            pluginManager.registerEvents(new SignChangeListener(replacementService), this);
        }

        if (config.replaceChat()) {
            pluginManager.registerEvents(new ChatEventListener(replacementService), this);
        }
    }
}
