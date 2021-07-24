package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Wall

val defaultStyle get() = Stylesheet().apply {
    styleFor<Node> {
        diameter { 100.pct }
        height { 100.pct }
    }
    styleFor<Wall> {
        height { 100.pct }
    }
}