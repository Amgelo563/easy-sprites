package me.amgelo563.easy_sprites.sprite.fetch;

import me.amgelo563.easy_sprites.http.SimpleHttpClient;
import me.amgelo563.easy_sprites.mc.MC;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// personal, simpler impl of https://github.com/haroldDOTsh/sprite/blob/master/src/main/java/sh/harold/sprite/atlas/AtlasCacheService.java
class DefaultSpriteFetcher implements SpriteFetcher {
    private static final URI MANIFEST = URI.create("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");

    private static final ObjectReader JSON_READER = new ObjectMapper().reader();

    private static final String ITEM_TEXTURES_FOLDER = "assets/minecraft/textures/item";

    private final Logger logger;

    private final SimpleHttpClient httpClient;

    public DefaultSpriteFetcher(Logger logger, SimpleHttpClient client) {
        this.logger = logger;
        this.httpClient = client;
    }

    public DefaultSpriteFetcher(Logger logger) {
        this.logger = logger;
        this.httpClient = SimpleHttpClient.DEFAULT;
    }

    public List<String> fetch() {
        // format (unused props omitted): { versions: Array<{ id: String (eg "26.1" or "26.4-snapshot-3"), url: String }> }
        JsonNode manifestNode = fetchJson(MANIFEST);
        JsonNode versionsNode = manifestNode.get("versions").asArray();

        URI currentVersionURI = findCurrentVersionURI(versionsNode);
        // format (unused props omitted): { downloads: { client: { sha1: String, url: String } } }
        JsonNode currentVersionNode = fetchJson(currentVersionURI);

        JsonNode clientNode = currentVersionNode.at("/downloads/client");
        String url = clientNode.get("url").asString();

        Set<String> sprites = new TreeSet<>();
        try (
                InputStream clientStream = httpClient.getAsInputStream(URI.create(url));
                ZipInputStream zipStream = new ZipInputStream(clientStream)
        ) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entryName.startsWith(ITEM_TEXTURES_FOLDER) && entryName.endsWith(".png")) {
                    String relativeName = entryName.substring(ITEM_TEXTURES_FOLDER.length() + 1);
                    String fileName = relativeName.substring(0, relativeName.lastIndexOf(".png")).replaceFirst("_\\d+$", "_00");
                    sprites.add(fileName);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading client JAR from input stream", e);
        }

        return sprites.stream().toList();
    }

    private URI findCurrentVersionURI(JsonNode versions) {
        JsonNode result = null;
        String currentVersion = MC.version().toString();

        for (JsonNode node : versions) {
            String id = node.path("id").asString();
            if (currentVersion.equals(id)) {
                result = node;
                break;
            }
        }

        if (result == null)
            throw new IllegalStateException("Could not find current version in manifest: " + MC.version());
        return URI.create(result.path("url").asString());
    }

    private JsonNode fetchJson(URI uri) {
        String response = httpClient.getAsString(uri);
        return JSON_READER.readTree(response);
    }
}
