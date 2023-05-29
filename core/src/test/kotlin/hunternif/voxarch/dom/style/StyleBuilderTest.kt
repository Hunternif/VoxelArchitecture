package hunternif.voxarch.dom.style

import hunternif.voxarch.builder.DefaultBuilders
import hunternif.voxarch.dom.domRoot
import hunternif.voxarch.dom.room
import hunternif.voxarch.dom.style.property.builder
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.query
import org.junit.Assert.*
import org.junit.Test

class StyleBuilderTest {
    @Test
    fun `override node builder via style`() {
        val style = defaultStyle.add {
            style("fill") {
                builder { set(DefaultBuilders.Fill) }
            }
        }
        val dom = domRoot {
            room("fill")
            room("default")
        }.buildDom(style)

        val roomFill = dom.query<Room>("fill").first()
        val roomDefault = dom.query<Room>("default").first()

        assertEquals(DefaultBuilders.Fill, roomFill.builder)
        assertEquals(null, roomDefault.builder)
    }
}