package me.amgelo563.easy_sprites.replace;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.permissions.Permissible;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// higher level class that wraps the replacement codec with a permission check
public class ReplacementService {
    private final ComponentReplacementCodec replacer;

    private final @Nullable String permission;

    private final Map<String, Component> replacements;

    public ReplacementService(ComponentReplacementCodec replacer, Map<String, Component> replacements, @Nullable String permission) {
        this.replacer = replacer;
        this.permission = permission;
        this.replacements = new HashMap<>(replacements);
    }

    public ReplacementService(Map<String, Component> replacements, @Nullable String permission) {
        this.replacer = new DefaultTextReplacer();
        this.permission = permission;
        this.replacements = new HashMap<>(replacements);
    }

    public Component checkAndReplace(Permissible source, Component component, String suffix) {
        if (cannotReplace(source, suffix)) return component;
        return replacer.replace(component);
    }

    /**
     * returns null if no matches were found
     */
    public @Nullable Component checkAndUnreplace(Permissible source, Component component, String suffix) {
        if (cannotReplace(source, suffix)) return null;
        return this.replacer.unreplace(component);
    }

    private boolean cannotReplace(Permissible source, String suffix) {
        String finalPermission = this.permission == null
                ? null
                : this.permission + "." + suffix;
        return finalPermission == null || source.hasPermission(finalPermission);
    }

    // lower level interface that actually does the replacing
    public interface ComponentReplacementCodec {
        Component replace(Component component);

        @Nullable
        Component unreplace(Component component);
    }

    public class DefaultTextReplacer implements ComponentReplacementCodec {
        protected final TextReplacementConfig replaceConfig = TextReplacementConfig.builder()
                .match(":([A-Za-z_]+):")
                .replacement((match, builder) -> {
                    String key = match.group(1);
                    Component replacement = replacements.get(key);
                    return replacement != null ? replacement : builder;
                })
                .build();

        public Component replace(Component component) {
            return component.replaceText(replaceConfig);
        }

        public @Nullable Component unreplace(Component component) {
            List<Component> children = new ArrayList<>(component.children());
            boolean replaced = false;

            for (int i = 0; i < children.size(); i++) {
                Component child = children.get(i);

                if (child instanceof ObjectComponent objectChild
                        && objectChild.contents() instanceof SpriteObjectContents spriteObjectContents) {
                    String sprite = spriteObjectContents.sprite().value().substring("item/".length());
                    children.set(i, Component.text(":" + sprite + ":"));
                    replaced = true;
                } else {
                    Component replacement = unreplace(child);
                    if (replacement != null) {
                        children.set(i, replacement);
                        replaced = true;
                    }
                }
            }

            return replaced ? component.children(children) : null;
        }
    }
}
