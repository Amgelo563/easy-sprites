package me.amgelo563.easy_sprites.sprite;

import me.amgelo563.easy_sprites.mc.MC;
import me.amgelo563.easy_sprites.sprite.fetch.SpriteRepository;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SpriteComponentsService {
    private final SpriteRepository repository;

    public SpriteComponentsService(SpriteRepository repository) {
        this.repository = repository;
    }

    public SpriteComponentsService(Path pluginFolder, Logger logger) {
        this.repository = SpriteRepository.fromPluginFolder(pluginFolder, logger);
    }

    public Map<String, Component> fetchComponents() {
        List<String> sprites = repository.fetch();
        Map<String, Component> mapped = new HashMap<>();
        Key atlas = Key.key(MC.version().isOlderThan(MC.V_1_21_11) ? "blocks" : "items");

        for (String spriteName : sprites) {
            String userFriendlyName = spriteName.replace("_00", "");
            Component component = Component.text().append(
                            Component
                                    .object()
                                    .contents(
                                            ObjectContents.sprite(
                                                    atlas,
                                                    Key.key("item/" + spriteName)
                                            )
                                    )
                                    .hoverEvent(
                                            Component
                                                    .text()
                                                    .content(":" + userFriendlyName + ":")
                                                    .color(NamedTextColor.GRAY)
                                                    .build()
                                    )
                                    .color(NamedTextColor.WHITE)
                    )
                    .build();
            mapped.put(userFriendlyName, component);
        }
        return mapped;
    }
}
