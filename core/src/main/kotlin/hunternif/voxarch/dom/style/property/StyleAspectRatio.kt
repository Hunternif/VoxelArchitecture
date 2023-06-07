package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.Value
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.naturalHeight
import hunternif.voxarch.plan.naturalWidth


/**
 * Aspect ratio of width / height.
 * If any value is changed, recalculates the other value.
 * The order of properties decides which one is changed last.
 * If none was changed, then changes height based on width.
 */
val PropAspectRatioXY = newNodeProperty<Node, Number>("aspect-ratio-xy", 1.0) { value ->
    val baseValue = node.run {
        if (naturalHeight == 0.0) 0.0
        else naturalWidth / naturalHeight
    }
    val newValue = value.invoke(baseValue, seed + 10000038).toDouble()
    if (newValue == 0.0) return@newNodeProperty

    // Find which property was changed last
    val properties = ctx.stylesheet.getProperties(this)
    val lastProp = properties.values.lastOrNull {
        it.property === PropHeight || it.property === PropWidth
    }
    when (lastProp?.property) {
        PropWidth, null -> {
            // Calculate height
            node.naturalHeight = node.naturalWidth / newValue
        }
        PropHeight -> {
            // Calculate width
            node.naturalWidth = node.naturalHeight * newValue
        }
    }
}

/** Aspect ratio of width / height. See [PropAspectRatioXY] */
fun Rule.aspectRatioXY(block: StyleSize.() -> Value<Number>) {
    add(PropAspectRatioXY, StyleSize().block())
}