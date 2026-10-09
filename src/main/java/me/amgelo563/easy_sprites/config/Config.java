package me.amgelo563.easy_sprites.config;

public record Config(
        boolean replaceSigns,
        boolean replaceChat,
        boolean permission
) {
    public final static Config DEFAULT = new Config(true, true, false);
}
