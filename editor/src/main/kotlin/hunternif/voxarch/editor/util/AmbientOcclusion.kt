package hunternif.voxarch.editor.util

import hunternif.voxarch.editor.render.TextureAtlas

/*
There are 8 blocks around a block:
 O O O
 O x O
 O O O

Each position can be occupied, giving us 2^8 = 256 configurations.

The texture file AO.png lists them all filled one by one, as a binary number.
Courtesy of Noobody: https://www.minecraftforum.net/forums/minecraft-java-edition/suggestions/25745-ambient-occlusion
 */

val aoTextureAtlas by lazy {
    val path = TextureAtlas.resourcePath("textures/AO.png")
    TextureAtlas.loadFromFile(path, 32, 32)
}