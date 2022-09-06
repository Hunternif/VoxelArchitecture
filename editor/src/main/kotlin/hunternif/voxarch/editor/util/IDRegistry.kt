package hunternif.voxarch.editor.util

import com.google.common.annotations.VisibleForTesting

interface WithID {
    val id: Int
}

/** Maintains a map of unique IDs to objects. */
class IDRegistry<T : WithID> {
    @VisibleForTesting
    internal var lastID = -1

    val map = LinkedHashMap<Int, T>()

    /** Creates a new ID. */
    fun newID(): Int {
        while (lastID < 0 || lastID in map.keys) lastID++
        return lastID
    }

    /** Stores the object in the ID map.
     * Potentially overrides the previous object. */
    fun save(obj: T) {
        map[obj.id] = obj
        if (lastID < obj.id) lastID = obj.id
    }

    fun clear() {
        lastID = -1
        map.clear()
    }
}