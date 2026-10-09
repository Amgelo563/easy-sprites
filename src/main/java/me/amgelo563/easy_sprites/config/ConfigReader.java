package me.amgelo563.easy_sprites.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
    public static Config read(FileConfiguration config) {
        boolean replaceSigns = config.getBoolean("replace.signs", Config.DEFAULT.replaceSigns());
        boolean replaceChat = config.getBoolean("replace.chat", Config.DEFAULT.replaceChat());
        boolean permission = config.getBoolean("permission", Config.DEFAULT.permission());
        return new Config(replaceSigns, replaceChat, permission);
    }
}
