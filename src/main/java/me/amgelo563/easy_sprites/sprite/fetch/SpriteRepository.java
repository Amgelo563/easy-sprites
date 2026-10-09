package me.amgelo563.easy_sprites.sprite.fetch;

import me.amgelo563.easy_sprites.mc.MC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// higher level component that wraps the fetcher in a cache
public class SpriteRepository {
    private final SpriteFetcher fetcher;

    private final Path cacheFile;

    private final Logger logger;

    public SpriteRepository(Path cacheFile, SpriteFetcher fetcher, Logger logger) {
        this.cacheFile = cacheFile;
        this.fetcher = fetcher;
        this.logger = logger;
    }

    public SpriteRepository(Path cacheFile, Logger logger) {
        this.cacheFile = cacheFile;
        this.fetcher = new DefaultSpriteFetcher(logger);
        this.logger = logger;
    }

    public static SpriteRepository fromPluginFolder(Path pluginFolder, Logger logger) {
        return new SpriteRepository(
                pluginFolder.resolve(MC.version() + ".txt"),
                logger
        );
    }

    public List<String> fetch() {
        return fetch(false);
    }

    public List<String> fetch(boolean forceFetch) {
        try {
            if (!forceFetch && Files.exists(cacheFile) && Files.isRegularFile(cacheFile)) {
                return Files.readAllLines(cacheFile);
            }
            List<String> sprites = fetcher.fetch();
            Files.write(cacheFile, sprites);
            return sprites;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to get sprites", e);
            throw new RuntimeException(e);
        }
    }
}
