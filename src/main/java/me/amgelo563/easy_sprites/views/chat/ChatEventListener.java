package me.amgelo563.easy_sprites.views.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.amgelo563.easy_sprites.replace.ReplacementService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChatEventListener implements Listener {
    private final static String PERMISSION_SUFFIX = "chat";

    private final ReplacementService replacementService;

    public ChatEventListener(ReplacementService replacementService) {
        this.replacementService = replacementService;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.message(replacementService.checkAndReplace(event.getPlayer(), event.message(), PERMISSION_SUFFIX));
    }
}
