package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

var Node.height: Double
    get() = when (this) {
        is Room -> this.height
        is Wall -> this.height
        else -> 0.0
    }
    set(value) {
        when (this) {
            is Room -> this.height = value
            is Wall -> this.height = value
        }
    }

var Node.width: Double
    get() = when (this) {
        is Room -> this.width
        else -> 0.0
    }
    set(value) {
        when (this) {
            is Room -> this.width = value
        }
    }

var Node.length: Double
    get() = when (this) {
        is Room -> this.length
        is Wall -> this.length
        else -> 0.0
    }
    set(value) {
        when (this) {
            is Room -> this.length = value
            is Wall -> this.length = value
        }
    }

/** Finds offset of this node's origin in global coordinates, i.e.
 * in the coordinate space where its highest parent node exists.
 */
//TODO: cache global position when nodes aren't moved
fun Node.findGlobalPosition(): Vec3 {
    var depth = 0
    val result = origin.clone()
    var parent = this.parent
    while (parent != null) {
        depth++
        if (depth > 10000) {
            println("Possibly infinite recursion!")
            return result
        }
        result.addLocal(parent.origin)
        parent = parent.parent
    }
    return result
}