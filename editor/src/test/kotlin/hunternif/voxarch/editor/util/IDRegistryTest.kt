package hunternif.voxarch.editor.util

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class IDRegistryTest {
    private val registry = IDRegistry<Minion>()
    private val minion0 = Minion(0)
    private val minion2 = Minion(2)
    private val minion4 = Minion(4)

    @Before
    fun setup() {
        registry.clear()
    }

    @Test
    fun `create ids in order`() {
        assertEquals(0, registry.newID())
        // no new IDs while nothing is saved:
        assertEquals(0, registry.newID())
        assertEquals(emptyMap<Int, Minion>(), registry.map)

        assertEquals(0, registry.lastID)
        registry.save(minion0)
        assertEquals(0, registry.lastID)
        assertEquals(mapOf(0 to minion0), registry.map)

        assertEquals(1, registry.newID())
        assertEquals(1, registry.newID())
        assertEquals(1, registry.lastID)

        // save() advances ID
        registry.save(Minion(2))
        assertEquals(2, registry.lastID)
        assertEquals(mapOf(0 to minion0, 2 to minion2), registry.map)

        assertEquals(3, registry.newID())
        assertEquals(3, registry.newID())
        assertEquals(3, registry.lastID)

        // save() advances ID
        registry.save(minion4)
        assertEquals(4, registry.lastID)
        assertEquals(mapOf(0 to minion0, 2 to minion2, 4 to minion4), registry.map)

        assertEquals(5, registry.newID())
        assertEquals(5, registry.lastID)

        registry.clear()
        assertEquals(-1, registry.lastID)
        assertEquals(emptyMap<Int, Minion>(), registry.map)
    }

    @Test
    fun `save overwrites old object`() {
        registry.save(minion0)
        registry.save(minion2)
        assertEquals(mapOf(0 to minion0, 2 to minion2), registry.map)

        val minion2copy = Minion(2)
        registry.save(minion2copy)
        assertEquals(mapOf(0 to minion0, 2 to minion2copy), registry.map)
    }

    @Test
    fun `create and remove`() {
        registry.save(minion0)
        registry.save(minion2)
        assertEquals(mapOf(0 to minion0, 2 to minion2), registry.map)

        registry.remove(minion0)
        assertEquals(mapOf(2 to minion2), registry.map)
    }

    data class Minion(override val id: Int) : WithID
}