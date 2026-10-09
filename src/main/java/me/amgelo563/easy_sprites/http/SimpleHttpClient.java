package me.amgelo563.easy_sprites.http;

import java.io.InputStream;
import java.net.URI;

/**
 * "why does a sprites plugin need an http client": we need to get the mojang atlas index and then
 * download the client jar to get the texture names in runtime
 */
public interface SimpleHttpClient {
    SimpleHttpClient DEFAULT = DefaultSimpleHttpClient.INSTANCE;

    String getAsString(URI uri);

    InputStream getAsInputStream(URI uri);
}
