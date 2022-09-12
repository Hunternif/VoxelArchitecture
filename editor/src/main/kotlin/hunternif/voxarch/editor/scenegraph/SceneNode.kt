package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.editor.util.add
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.max
import hunternif.voxarch.util.min
import hunternif.voxarch.vector.Vec3

class SceneNode(
    id: Int,
    val node: Node,
    color: ColorRGBa = Colors.defaultNodeBox,
    isGenerated: Boolean = false,
) : SceneObject(
    id,
    color = color,
    isGenerated = isGenerated,
) {
    val generators = mutableListOf<IGenerator>()

    init {
        update()
    }

    override fun addChild(child: SceneObject) {
        super.addChild(child)
        (child as? SceneNode)?.node?.let {
            // prevent double-adding, especially when generating nodes
            if (it.parent != node) node.addChild(it)
        }
    }

    override fun removeChild(child: SceneObject): Boolean {
        if (super.removeChild(child)) {
            (child as? SceneNode)?.node?.let {
                node.removeChild(it)
            }
            return true
        }
        return false
    }

    override fun update() {
        updateFaces()
        val origin = node.findGlobalPosition()
        when (node) {
            is Room -> {
                wrapVoxels(origin + node.start, node.size)
            }
            is Wall -> {
                //TODO: figure out how to render walls at non-right angles
                val innerMin = min(Vec3.ZERO, node.innerEnd)
                val innerMax = max(Vec3.ZERO, node.innerEnd)
                wrapVoxels(origin + innerMin, innerMax - innerMin)
            }
            is Path -> {
                //TODO: render path as a line
                val innerMin = node.points.fold(Vec3.ZERO) { a, b -> min(a, b) }
                val innerMax = node.points.fold(Vec3.ZERO) { a, b -> max(a, b) }
                wrapVoxels(origin + innerMin, innerMax - innerMin)
            }
            is Floor -> {
                val p = parent
                if (p != null) {
                    // Floor fills a horizontal plane within its parent's bounds
                    //TODO: indicate that floor is a potentially infinite plane
                    p.update()
                    start.set(p.start).add(node.origin)
                    size.set(p.size.x, 1f, p.size.z)
                } else {
                    // if no parent, render a small flat 3x3 square
                    wrapVoxels(origin + Vec3(-1, 0, -1), Vec3(2, 0, 2))
                }
            }
            else -> {
                wrapVoxels(origin, Vec3.ZERO)
            }
        }
    }

    val nodeClassName: String = node.javaClass.simpleName
    private val strRepr: String by lazy { "$nodeClassName $id" }
    override fun toString() = strRepr
}