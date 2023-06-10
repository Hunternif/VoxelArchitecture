package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.builder.HintDir.*
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.plan.findGlobalRotation
import hunternif.voxarch.util.isRightAngle
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.util.round
import hunternif.voxarch.vector.LinearTransformation

/**
 * See [applyHint]
 */
enum class HintDir {
    /** No hinting */
    OFF,

    /** Round to the nearest int */
    ROUND,

    /** X value rounds up to int, Z rounds both ways */
    OUT_X,

    /** X & Z values round up to int */
    // This should be used by corner builders, so that hinting is symmetric
    // for all corners.
    OUT_X_Z,
}

/**
 * Move this [node]'s origin and size so that its global position rounds to int,
 * using the given strategy [dir].
 *
 * Moving origin instead of start, because that leads to cleaner final positions
 * for all children.
 */
fun applyHint(node: Node, dir: HintDir) {
    // TODO: hint rotated nodes
    if (!node.rotationY.isRightAngle()) return
    when (dir) {
        OFF -> {}
        ROUND -> {
            val globalPos = node.findGlobalPosition()
            val globalRot = node.findGlobalRotation()
            val localToGlobal = LinearTransformation().apply {
                translate(globalPos)
                rotateY(globalRot)
            }
            val globalToLocal = LinearTransformation().apply {
                rotateY(-globalRot)
                translate(-globalPos)
            }

            val globalStart = localToGlobal.transform(node.start)
            val globalStartHint = globalStart.round()
            val localStartHint = globalToLocal.transform(globalStartHint)

            val globalEnd = localToGlobal.transform(node.start + node.size)
            val globalEndHint = globalEnd.round()
            val localEndHint = globalToLocal.transform(globalEndHint)

            val deltaStart = localStartHint - node.start
            val newSize = localEndHint - node.start - deltaStart

            // rotate to local angle because we're moving origin, not start:
            node.origin += deltaStart.rotateY(node.rotationY)
            node.size = newSize
        }
        OUT_X -> { /* not implemented */ }
        OUT_X_Z -> { /* not implemented */ }
    }
}