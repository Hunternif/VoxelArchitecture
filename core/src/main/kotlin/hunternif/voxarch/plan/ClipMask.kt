package hunternif.voxarch.plan

/**
 * A Node's children will only place voxels inside this mask.
 */
enum class ClipMask {
    /** No clipping */
    OFF,

    /** Allow building within this node's box defined by
     * [Node.origin], [Node.start] and [Node.size] */
    BOX,

    /** Allow building within [Node.getGroundBoundaries] */
    BOUNDARY,

    /** Allow building within the voxels that this node built.
     * The node's actual voxels in the world should be cleared first. */
    //VOXELS,
}