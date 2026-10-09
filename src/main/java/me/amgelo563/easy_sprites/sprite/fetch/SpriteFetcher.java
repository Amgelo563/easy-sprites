package me.amgelo563.easy_sprites.sprite.fetch;

import java.util.List;

// lower level interface that actually does the fetching
public interface SpriteFetcher {
    List<String> fetch();
}
