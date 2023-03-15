package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.Property
import hunternif.voxarch.dom.style.property.PropDepth
import hunternif.voxarch.dom.style.property.PropHeight
import hunternif.voxarch.dom.style.property.PropWidth
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.rotateY

interface IDirectionBuilder {
    /** Children will be laid out in this direction. */
    var dir: Direction3D

    /** Node "natural" size in the direction [dir], accounting for its rotation. */
    var Node.dirSize: Double
        get() = when (dir.rotateY(rotationY)) {
            UP, DOWN -> naturalHeight
            EAST, WEST -> naturalWidth
            NORTH, SOUTH -> naturalDepth
        }
        set(value) {
            when (dir.rotateY(rotationY)) {
                UP, DOWN -> naturalHeight = value
                EAST, WEST -> naturalWidth = value
                NORTH, SOUTH -> naturalDepth = value
            }
        }

    /** Starting coordinate in the direction [dir] */
    var Node.dirX: Double
        get() = when (dir) {
            UP -> minY
            DOWN -> maxY
            EAST -> minX
            WEST -> maxX
            SOUTH -> minZ
            NORTH -> maxZ
        }
        set(value) {
            when (dir) {
                UP -> minY = value
                DOWN -> maxY = value
                EAST -> minX = value
                WEST -> maxX = value
                SOUTH -> minZ = value
                NORTH -> maxZ = value
            }
        }

    /** Property that calculates size in the direction [dir]. */
    val Node.propertyForDir: Property<Double>
        get() = when (dir.rotateY(rotationY)) {
            UP, DOWN -> PropHeight
            EAST, WEST -> PropWidth
            NORTH, SOUTH -> PropDepth
        }

    /** Initial coordinate value when laying out inside this Node
     * in the direction [dir]. */
    val Node.initPos: Double
        get() = when (dir) {
            UP -> start.y
            EAST -> start.x
            SOUTH -> start.z
            DOWN -> start.y + height
            WEST -> start.x + width
            NORTH -> start.z + depth
        }

    /** Increment sign when laying out inside this Node
     * in the direction [dir]. */
    val sign: Double
        get() = when (dir) {
            UP, EAST, SOUTH -> 1.0
            DOWN, WEST, NORTH -> -1.0
        }
}