package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.opposite
import hunternif.voxarch.wfc.tiled.Match.*

/**
 * Memoizes calls to [matchesSide]
 */
abstract class WfcCachingTile : WfcTile {
    internal val memo: MutableMap<Direction3D, MutableMap<WfcTile, Match>> = mutableMapOf()

    override fun matchesSide(other: WfcTile, dir: Direction3D): Boolean {
        return memo.getOrPut(dir) { mutableMapOf() }
            .getOrPut(other) {
                val match = calculateMatch(other, dir).toMatch()
                // store the reverse direction
                (other as? WfcCachingTile)
                    ?.memo?.getOrPut(dir.opposite()) { mutableMapOf() }
                    ?.put(this, match)
                match
            }.toBoolean()
    }

    protected abstract fun calculateMatch(other: WfcTile, dir: Direction3D): Boolean
}

internal enum class Match {
    YES, NO
}
private fun Boolean.toMatch() = if (this) YES else NO
private fun Match.toBoolean() = this == YES