package hunternif.voxarch.editor.util

import hunternif.voxarch.editor.render.AtlasEntry
import hunternif.voxarch.editor.render.TextureAtlas
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.vector.IntVec3

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

val aoTiles: List<AtlasEntry> by lazy { aoTextureAtlas.entries.toList() }

/**
 * Returns the AO tile for a given voxel position and face.
 * Null means no AO.
 */
fun getAOTile(
    voxels: IStorage3D<out IVoxel?>,
    pos: IntVec3,
    dir: Direction3D,
): AtlasEntry? {
    val neighbors = getNeighbors(dir)
    var index = 0
    for (n in neighbors) {
        index = index shl 1
        if (voxels.get(
                pos.x + dir.vec.x + n.x,
                pos.y + dir.vec.y + n.y,
                pos.z + dir.vec.z + n.z) != null) {
            index += 1
        }
    }
    if (index <= 0 || index >= aoTiles.size) return null
    return aoTiles[index]
}

private fun getNeighbors(dir: Direction3D): Array<IntVec3> = when (dir) {
    UP    -> neighborsUp
    DOWN  -> neighborsDown
    EAST  -> neighborsRight
    WEST  -> neighborsLeft
    SOUTH -> neighborsFront
    NORTH -> neighborsBack
}


// Below are cached arrays of coordinates of blocks around a given direction,
// matching the order of iterations in AO.png

private val neighborsUp = arrayOf(
    IntVec3( 1,  0,  1),
    IntVec3( 0,  0,  1),
    IntVec3(-1,  0,  1),
    IntVec3( 1,  0,  0),
    // skip center
    IntVec3(-1,  0,  0),
    IntVec3( 1,  0, -1),
    IntVec3( 0,  0, -1),
    IntVec3(-1,  0, -1),
)

private val neighborsDown = arrayOf(
    IntVec3(-1,  0,  1),
    IntVec3( 0,  0,  1),
    IntVec3( 1,  0,  1),
    IntVec3(-1,  0,  0),
    // skip center
    IntVec3( 1,  0,  0),
    IntVec3(-1,  0, -1),
    IntVec3( 0,  0, -1),
    IntVec3( 1,  0, -1),
)

private val neighborsLeft = arrayOf(
    IntVec3( 0, -1,  1),
    IntVec3( 0, -1,  0),
    IntVec3( 0, -1, -1),
    IntVec3( 0,  0,  1),
    // skip center
    IntVec3( 0,  0, -1),
    IntVec3( 0,  1,  1),
    IntVec3( 0,  1,  0),
    IntVec3( 0,  1, -1),
)

private val neighborsRight = arrayOf(
    IntVec3( 0, -1, -1),
    IntVec3( 0, -1,  0),
    IntVec3( 0, -1,  1),
    IntVec3( 0,  0, -1),
    // skip center
    IntVec3( 0,  0,  1),
    IntVec3( 0,  1, -1),
    IntVec3( 0,  1,  0),
    IntVec3( 0,  1,  1),
)

private val neighborsFront = arrayOf(
    IntVec3( 1, -1,  0),
    IntVec3( 0, -1,  0),
    IntVec3(-1, -1,  0),
    IntVec3( 1,  0,  0),
    // skip center
    IntVec3(-1,  0,  0),
    IntVec3( 1,  1,  0),
    IntVec3( 0,  1,  0),
    IntVec3(-1,  1,  0),
)

private val neighborsBack = arrayOf(
    IntVec3(-1, -1,  0),
    IntVec3( 0, -1,  0),
    IntVec3( 1, -1,  0),
    IntVec3(-1,  0,  0),
    // skip center
    IntVec3( 1,  0,  0),
    IntVec3(-1,  1,  0),
    IntVec3( 0,  1,  0),
    IntVec3( 1,  1,  0),
)