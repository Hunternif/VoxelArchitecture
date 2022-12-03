package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.findParentNode
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
val PropRoofOffset = newGenProperty<TurretGenerator, Dimension> { value ->
    val baseValue = domBuilder.findParentNode().width
    gen.roofOffset = value
        .invoke(baseValue, seed + 10000006)
        .roundToInt()
}

/** Y/X ratio of spires for all child turrets. */
val PropSpireRatio = newGenProperty<TurretGenerator, Value<Double>> { value ->
    val baseValue = 1.5
    gen.spireRatio = value.invoke(baseValue, seed + 10000014)
}

/** Y/X ratio of tapered bottoms of turrets. */
val PropTaperRatio = newGenProperty<TurretGenerator, Value<Double>> { value ->
    val baseValue = 0.75
    gen.taperRatio = value.invoke(baseValue, seed + 10000015)
}

val PropRoofShape = newGenProperty<TurretGenerator, Value<RoofShape>> { value ->
    val baseValue = gen.roofShape
    gen.roofShape = value.invoke(baseValue, seed + 10000007)
}

// TODO: this will apply to a turret-with-room generator
//val PropBodyShape = newGenProperty<TurretGenerator, Value<BodyShape>> { value ->
//    val baseValue = gen.bodyShape
//    gen.bodyShape = value.invoke(baseValue, seed + 10000008)
//}

val PropBottomShape = newGenProperty<TurretGenerator, Value<BottomShape>> { value ->
    val baseValue = gen.bottomShape
    gen.bottomShape = value.invoke(baseValue, seed + 10000009)
}

fun Rule.roofOffset2(block: StyleSize.() -> Dimension) {
    add(PropRoofOffset, StyleSize().block())
}

fun Rule.spireRatio2(block: StyleSize.() -> Value<Double>) {
    add(PropSpireRatio, StyleSize().block())
}

fun Rule.taperRatio2(block: StyleSize.() -> Value<Double>) {
    add(PropTaperRatio, StyleSize().block())
}

fun Rule.roofShape2(block: StyleTurretRoofShape.() -> Value<RoofShape>) {
    add(PropRoofShape, StyleTurretRoofShape().block())
}

//fun Rule.bodyShape2(block: StyleTurretBodyShape.() -> Value<BodyShape>) {
//    add(PropBodyShape, StyleTurretBodyShape().block())
//}

fun Rule.bottomShape2(block: StyleTurretBottomShape.() -> Value<BottomShape>) {
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