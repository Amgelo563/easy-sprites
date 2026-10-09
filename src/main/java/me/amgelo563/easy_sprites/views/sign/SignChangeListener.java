package me.amgelo563.easy_sprites.views.sign;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import me.amgelo563.easy_sprites.replace.ReplacementService;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.permissions.Permissible;

import java.util.List;

public final class SignChangeListener implements Listener {
    private final static String PERMISSION_SUFFIX = "signs";

    private final ReplacementService replacementService;

    public SignChangeListener(ReplacementService replacementService) {
        this.replacementService = replacementService;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        List<Component> lines = event.lines();
        Permissible source = event.getPlayer();
        for (int i = 0; i < lines.size(); i++) {
            Component replaced = replacementService.checkAndReplace(source, lines.get(i), PERMISSION_SUFFIX);
            event.line(i, replaced);
        }
    }

    @EventHandler
    public void onSignOpen(PlayerOpenSignEvent event) {
        Sign sign = event.getSign();
        SignSide side = sign.getSide(event.getSide());
        Permissible source = event.getPlayer();

        List<Component> lines = side.lines();
        boolean anyReplaced = false;
        for (int i = 0; i < lines.size(); i++) {
            Component replaced = replacementService.checkAndUnreplace(source, lines.get(i), PERMISSION_SUFFIX);
            if (replaced == null) continue;
            side.line(i, replaced);
            anyReplaced = true;
        }

        if (!anyReplaced) return;

        // we need this next part to forcefully replace the sign and sign edit screen with the new components

        sign.update(true);
        event.setCancelled(true);

        Player player = event.getPlayer();
        Block signBlock = sign.getBlock();
        player.sendBlockUpdate(signBlock.getLocation(), (TileState) signBlock.getState());
        player.openSign(sign, event.getSide());
    }
}
