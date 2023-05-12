package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.util.getOrInsert
import java.util.*


/** for [memoStrWithIndex] */
private val indexedStringMap = WeakHashMap<String, ArrayList<String?>>()

/**
 * Given string "str" and number i, returns "str##i".
 * This is used for ImGui window labels.
 * Optimizes dynamic strings that are called every frame.
 */
fun memoStrWithIndex(str: String, i: Int): String = indexedStringMap
    .getOrDefault(str, ArrayList()).getOrInsert(i) { "$str##$i" }

/**
 * Reusable single-line representation of a SceneObject in gui.
 */
fun sceneObjectToSingleLine(obj: SceneObject): String = when (obj) {
    is SceneNode -> {
        var result = obj.nodeClassName
        val type = obj.node.tags.firstOrNull()
        if (!type.isNullOrEmpty()) result += " $type"
        if (obj.blueprints.isNotEmpty()) result += " []"
        result
    }
    is SceneVoxelGroup -> obj.label
    else -> obj.toString()
}