# 🖼️ `easy-sprites`

[![Build Status](https://img.shields.io/github/actions/workflow/status/Amgelo563/easy-sprites/build.yml?style=for-the-badge&logo=github)](https://github.com/Amgelo563/javalens-bot/actions/workflows/build.yml)
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/easy-sprites)

A PaperMC plugin to let players easily send sprite textures in signs and chat. Supports 1.21.9 (where the object sprite
component was introduced) and above.

## Usage

1. Install the plugin.
2. On the first start, the plugin will fetch the sprite textures and cache them locally. They will be
   stored in a `<version>.txt` file under the plugin's folder (e.g. `26.2.txt`). You shouldn't touch this file, but if
   you delete it the plugin will simply re-fetch. If you ever change versions, feel free to delete the older version's
   file.
3. Players can send sprite textures in signs and chat by using the format `:<sprite>:` (e.g. `:apple:`). The plugin will
   replace the `:<sprite>:` with the corresponding sprite component.

## Configuration

| Option          | Type      | Default | Description                                                                                                            |
|-----------------|-----------|---------|------------------------------------------------------------------------------------------------------------------------|
| `replace.signs` | `boolean` | `true`  | Whether to replace sprite textures in signs.                                                                           |
| `replace.chat`  | `boolean` | `true`  | Whether to replace sprite textures in chat.                                                                            |
| `permission`    | `boolean` | `false` | If turned on, the above will require permission to work: `easy_sprites.replace.chat` for chat, and `.signs` for signs. |
