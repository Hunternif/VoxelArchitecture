package hunternif.voxarch.builder

import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.PositionTransformer
import hunternif.voxarch.util.RoomConstrainedStorage
import hunternif.voxarch.vector.Vec3
import kotlin.math.roundToInt

fun IBlockStorage.transformer() =
    this as? PositionTransformer ?: PositionTransformer(this)

/**
 * Runs the function [buildAtXZ] at every (X,Z) point inside the room's walls.
 *
 * **Reference frame origin must be set to the low-XZ corner of the room!**
 *
 * When the room is rotated to a non-right angle, offsets are enabled:
 * * [rotationMargin] is distance inward from the walls to building site.
 * * [rotationOffset] is distance inward from the walls for constraint checking.
 * * [rotationStep] is increment for X & Z.
 * TODO: remove excessive margins & offsets.
 */
fun IBlockStorage.fillXZ(
    node: Room,
    rotationOffset: Double = 0.0,
    rotationMargin: Double = 0.25,
    rotationStep: Double = 0.5,
    buildAtXZ: (x: Double, z: Double) -> Unit
) {
    val transformer = this.transformer()
    val constraint = RoomConstrainedStorage(transformer, node)

    val isRightAngle = transformer.angleY.roundToInt() % 90 == 0
    // Step by 0.5 to prevent gaps when the node is rotated
    // Optimization: if the angle is right, use 1.0 increments.
    val step = if (isRightAngle) 1.0 else rotationStep

    val isIntegerMove = transformer.transform(1.0, 1.0, 1.0).isInteger
    // Extra margin on the edges is to prevent building outside walls.
    // Optimization: if it maps precisely onto world blocks, use 0 margin.
    val margin = if (isRightAngle) 0.0 else rotationMargin

    val offset = if (isRightAngle && isIntegerMove) 0.0 else rotationOffset
    constraint.setOffset(offset)

    var x = margin
    while (x <= node.width - margin) {
        var z = margin
        while (z <= node.length - margin) {
            if (constraint.isWithinRoom(x, 0.0, z)) {
                buildAtXZ(x, z)
            }
            z += step
        }
        x += step
    }
}

fun line(
    p1: Vec3,
    p2: Vec3,
    step: Double = 1.0,
    offset: Double = 0.0, // from start and end
    build: (pos: Vec3) -> Unit
) {
    val lineVec = p2.subtract(p1).normalizeLocal()
    val stepVec = lineVec.multiply(step)
    val length = p2.distanceTo(p1)
    val pos = p1.add(lineVec.multiply(offset))
    var x = offset
    while (x <= length - offset) {
        build(pos)
        pos.addLocal(stepVec)
        x += step
    }
}
