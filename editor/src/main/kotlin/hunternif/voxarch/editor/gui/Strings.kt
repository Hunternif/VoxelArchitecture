package hunternif.voxarch.editor.gui

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
