package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.sandbox.castle.turret.Turret
import hunternif.voxarch.util.RandomOption
import hunternif.voxarch.util.nextWeighted
import kotlin.math.roundToInt
import kotlin.random.Random

/** Offset for borders and spires in all child turrets. */
fun StyledNode<Turret>.roofOffset(block: StyleSize.() -> Dimension) {
    val baseValue = node.width
    val style = StyleSize()
    node.roofOffset = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000006)
        .roundToInt()
}

/** Y/X ratio of spires for all child turrets. */
var StyledNode<Turret>.spireRatio: Double
    get() = node.spireRatio
    set(value) { node.spireRatio = value }

/** Y/X ratio of tapered bottoms of turrets. */
var StyledNode<Turret>.taperRatio: Double
    get() = node.taperRatio
    set(value) { node.taperRatio = value }

class StyleTurretShape : StyleParameter

fun StyledNode<Turret>.roofShape(block: StyleTurretShape.() -> Option<RoofShape>) {
    val base = when (val parent = node.parent) {
        is Turret -> parent.roofShape
        else -> RoofShape.FLAT_BORDERED
    }
    roofShape = StyleTurretShape().block().invoke(base, seed + 10000007)
}

var StyledNode<Turret>.roofShape: RoofShape
    get() = node.roofShape
    set(value) { node.roofShape = value }

fun StyledNode<Turret>.bodyShape(block: StyleTurretShape.() -> Option<BodyShape>) {
    val base = when (val parent = node.parent) {
        is Turret -> parent.bodyShape
        else -> BodyShape.SQUARE
    }
    bodyShape = StyleTurretShape().block().invoke(base, seed + 10000008)
}

var StyledNode<Turret>.bodyShape: BodyShape
    get() = node.bodyShape
    set(value) {
        node.bodyShape = value
        node.shape = node.bodyShape.toPolygonShape()
    }

private fun BodyShape.toPolygonShape(): PolygonShape = when(this) {
    BodyShape.SQUARE -> PolygonShape.SQUARE
    BodyShape.ROUND -> PolygonShape.ROUND
}

fun StyledNode<Turret>.bottomShape(block: StyleTurretShape.() -> Option<BottomShape>) {
    val base = when (val parent = node.parent) {
        is Turret -> parent.bottomShape
        else -> BottomShape.FLAT
    }
    bottomShape = StyleTurretShape().block().invoke(base, seed + 10000009)
}

var StyledNode<Turret>.bottomShape: BottomShape
    get() = node.bottomShape
    set(value) { node.bottomShape = value }

fun StyleTurretShape.randomRoof(): Option<RoofShape> = { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, RoofShape.FLAT_BORDERED),
        RandomOption(0.5, RoofShape.SPIRE),
        RandomOption(0.5, RoofShape.SPIRE_BORDERED)
    ).value
}

fun StyleTurretShape.randomBody(): Option<BodyShape> = { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, BodyShape.SQUARE),
        RandomOption(1.0, BodyShape.ROUND)
    ).value
}