package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.node
import hunternif.voxarch.dom.space
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.subdivide
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.naturalSize
import hunternif.voxarch.plan.query
import hunternif.voxarch.util.Direction3D
import hunternif.voxarch.util.assertVec3Equals
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class StyleAspectRatioTest {
    @Test
    fun `if size is not set, calculate height from width`() {
        val style = Stylesheet().add {
            style("test") {
                aspectRatioXY { set(2.0) }
            }
        }
        val dom = domRoot {
            node("test")
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(1.0, 0.5, 1.0), node.naturalSize)
    }

    @Test
    fun `use last width`() {
        val style = defaultStyle.add {
            style("container") {
                size(3.vx, 4.vx, 5.vx)
            }
            style("test") {
                width { 100.pct }
                aspectRatioXY { set(2.0) }
            }
        }
        val dom = domRoot {
            node("container") {
                space("test")
            }
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(3.0, 1.5, 5.0), node.naturalSize)
    }

    @Test
    fun `use last height`() {
        val style = defaultStyle.add {
            style("container") {
                size(3.vx, 4.vx, 5.vx)
            }
            style("test") {
                height { 100.pct }
                aspectRatioXY { set(2.0) }
            }
        }
        val dom = domRoot {
            node("container") {
                space("test")
            }
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(8.0, 4.0, 5.0), node.naturalSize)
    }

    @Test
    fun `use last depth`() {
        val style = defaultStyle.add {
            style("container") {
                size(3.vx, 4.vx, 5.vx)
            }
            style("test") {
                width { 100.pct }
                aspectRatioXZ { set(2.0) }
            }
        }
        val dom = domRoot {
            node("container") {
                space("test")
            }
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(3.0, 4.0, 1.5), node.naturalSize)
    }

    @Test
    fun `inherit aspect ratio from parent`() {
        val style = defaultStyle.add {
            style("container") {
                size(10.vx, 5.vx, 1.vx)
            }
            style("test") {
                width { 80.pct }
                // make aspect ratio 50% of the parent's aspect ratio
                aspectRatioXY { 50.pct }
            }
        }
        val dom = domRoot {
            node("container") {
                node("test")
            }
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(8.0, 2.0, 1.0), node.naturalSize)
    }

    @Test
    fun `apply aspect ratio after subdivide X`() {
        val style = defaultStyle.add {
            style("container") {
                size(10.vx, 10.vx, 10.vx)
            }
            style("test") {
                width { 100.pct }
                aspectRatioXY { set(2.0) }
            }
            style("block") {
                width { 2.vx }
            }
        }
        val dom = domRoot {
            node("container") {
                subdivide(Direction3D.EAST) {
                    node("block")
                    node("test")
                    node("block")
                }
            }
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(6.0, 3.0, 1.0), node.naturalSize)
    }

    @Test
    fun `apply aspect ratio after subdivide Y`() {
        val style = defaultStyle.add {
            style("container") {
                size(10.vx, 10.vx, 10.vx)
            }
            style("test") {
                width { 100.pct }
                aspectRatioXY { set(2.0) }
            }
            style("block") {
                height { 2.vx }
            }
        }
        val dom = domRoot {
            node("container") {
                subdivide(Direction3D.UP) {
                    node("block")
                    node("test")
                    node("block")
                }
            }
        }.buildDom(style)
        val node = dom.query<Node>("test").first()
        assertVec3Equals(Vec3(12.0, 6.0, 1.0), node.naturalSize)
    }
}