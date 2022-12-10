package hunternif.voxarch.dom.style

import hunternif.voxarch.generator.TurretGenerator
import hunternif.voxarch.sandbox.castle.turret.BodyShape
import hunternif.voxarch.sandbox.castle.turret.BottomShape
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import hunternif.voxarch.util.RandomOption
import hunternif.voxarch.util.nextWeighted
import kotlin.math.roundToInt
import kotlin.random.Random

class StyleTurretBodyShape : StyleParameter
class StyleTurretRoofShape : StyleParameter
class StyleTurretBottomShape : StyleParameter

/** Offset for borders and spires in all child turrets. */
val PropRoofOffset = newGenProperty<TurretGenerator, Double>("roof offset", 1.0) { value ->
    val baseValue = parentNode.width
    gen.roofOffset = value
        .invoke(baseValue, seed + 10000006)
        .roundToInt()
}

/** Y/X ratio of spires for all child turrets. */
val PropSpireRatio = newGenProperty<TurretGenerator, Double>("spire ratio", 1.5) { value ->
    val baseValue = 1.5
    gen.spireRatio = value.invoke(baseValue, seed + 10000019)
}

/** Y/X ratio of tapered bottoms of turrets. */
val PropTaperRatio = newGenProperty<TurretGenerator, Double>("taper ratio", 0.75) { value ->
    val baseValue = 0.75
    gen.taperRatio = value.invoke(baseValue, seed + 10000020)
}

val PropRoofShape = newGenProperty<TurretGenerator, RoofShape>("roof shape", RoofShape.FLAT_BORDERED) { value ->
    val baseValue = gen.roofShape
    gen.roofShape = value.invoke(baseValue, seed + 10000007)
}

// TODO: this will apply to a turret-with-room generator
//val PropBodyShape = newGenProperty<TurretGenerator, BodyShape>("body shape", BodyShape.SQUARE) { value ->
//    val baseValue = gen.bodyShape
//    gen.bodyShape = value.invoke(baseValue, seed + 10000008)
//}

val PropBottomShape = newGenProperty<TurretGenerator, BottomShape>("bottom shape", BottomShape.FLAT) { value ->
    val baseValue = gen.bottomShape
    gen.bottomShape = value.invoke(baseValue, seed + 10000009)
}

fun Rule.roofOffset(block: StyleSize.() -> Dimension) {
    add(PropRoofOffset, StyleSize().block())
}

fun Rule.spireRatio(block: StyleSize.() -> Value<Double>) {
    add(PropSpireRatio, StyleSize().block())
}

fun Rule.taperRatio(block: StyleSize.() -> Value<Double>) {
    add(PropTaperRatio, StyleSize().block())
}

fun Rule.roofShape(block: StyleTurretRoofShape.() -> Value<RoofShape>) {
    add(PropRoofShape, StyleTurretRoofShape().block())
}

//fun Rule.bodyShape(block: StyleTurretBodyShape.() -> Value<BodyShape>) {
//    add(PropBodyShape, StyleTurretBodyShape().block())
//}

fun Rule.bottomShape(block: StyleTurretBottomShape.() -> Value<BottomShape>) {
    add(PropBottomShape, StyleTurretBottomShape().block())
}

fun StyleTurretRoofShape.randomRoof(): Value<RoofShape> = value { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, RoofShape.FLAT_BORDERED),
        RandomOption(0.5, RoofShape.SPIRE),
        RandomOption(0.5, RoofShape.SPIRE_BORDERED)
    ).value
}

fun StyleTurretBodyShape.randomBody(): Value<BodyShape> = value { _, seed ->
    Random(seed).nextWeighted(
        RandomOption(1.0, BodyShape.SQUARE),
        RandomOption(1.0, BodyShape.ROUND)
    ).value
}