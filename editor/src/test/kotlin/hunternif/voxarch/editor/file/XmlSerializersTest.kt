package hunternif.voxarch.editor.file

import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.*
import org.junit.Test

class XmlSerializersTest {
    @Test
    fun `serialize Vec3`() {
        assertEquals("<Vec3>(1.0, 2.0, 3.0)</Vec3>",
            serializeToXmlStr(Vec3(1, 2, 3)))
        assertEquals("<Vec3>(-1.1, 2.999999999, 3.0)</Vec3>",
            serializeToXmlStr(Vec3(-1.1, 2.999999999, 3.0)))
    }

    @Test
    fun `deserialize Vec3`() {
        assertEquals(Vec3(1, 2, 3),
            deserializeXml("<Vec3>(1.0, 2.0, 3.0)</Vec3>", Vec3::class))
        assertEquals(Vec3(-1.1, 2.999999999, 3.0),
            deserializeXml("<Vec3>(-1.1, 2.999999999, 3.0)</Vec3>", Vec3::class))
    }

    private val structureXml = """
        <node class="Structure" origin="(1.0, 2.0, 3.0)" start="(0.0, 0.0, 0.0)" size="(0.0, 0.0, 0.0)" rotationY="0.0">
          <node class="Room" origin="(0.0, 0.0, 0.0)" start="(-2.5, 0.0, -3.5)" size="(5.0, 6.0, 7.0)" rotationY="0.0" centered="true">
            <tag>my_tag</tag>
            <node class="Wall" origin="(0.0, 0.0, 0.0)" start="(0.0, 0.0, 0.0)" size="(1.0, 2.0, 0.0)" rotationY="0.0" transparent="true"/>
            <node class="Wall" origin="(0.0, 0.0, 1.0)" start="(0.0, 0.0, 0.0)" size="(1.0, 2.0, 0.0)" rotationY="-90.0" transparent="false"/>
            <node class="Floor" origin="(0.0, 0.0, 0.0)" start="(-2.5, 0.0, -3.5)" size="(0.0, 0.0, 0.0)" rotationY="0.0" y="1.0"/>
          </node>
          <node class="PolyRoom" origin="(0.0, 0.0, 0.0)" start="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" rotationY="0.0" centered="false" shape="ROUND">
            <polygon class="Path" origin="(0.0, 0.0, 0.0)" start="(0.0, 0.0, 0.0)" size="(0.0, 0.0, 0.0)" rotationY="0.0"/>
          </node>
        </node>
        """.trimIndent()

    private val structure = Structure(Vec3(1, 2, 3)).apply {
        centeredRoom(Vec3.ZERO, Vec3(5, 6, 7)) {
            tags += "my_tag"
            wall(Vec3(0, 0, 0), Vec3(1, 2, 0)) { transparent = true }
            wall(Vec3(0, 0, 1), Vec3(0, 2, 2))
            floor(1.0)
            addChild(Gate()) // missing node class
        }
        polyRoom(Vec3.ZERO, Vec3(7, 8, 9)) { shape = PolyShape.ROUND }
    }

    private val polyRoomXml = """
        <node class="PolyRoom" origin="(0.0, 0.0, 0.0)" start="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" rotationY="0.0" centered="false" shape="ROUND">
          <polygon class="Path" origin="(0.0, 0.0, 0.0)" start="(0.0, 0.0, 0.0)" size="(0.0, 0.0, 0.0)" rotationY="0.0">
            <point>(0.0, 0.0, 0.0)</point>
            <point>(1.0, 2.0, 3.0)</point>
          </polygon>
        </node>
        """.trimIndent()

    private val polygonRoom = PolyRoom(Vec3.ZERO, Vec3(7, 8, 9)).apply {
        start = Vec3.ZERO
        shape = PolyShape.ROUND
        polygon.apply {
            addPoint(Vec3.ZERO)
            addPoint(Vec3(1, 2, 3))
            loopToStart()
        }
    }

    @Test
    fun `serialize Node tree`() {
        val xml = serializeToXmlStr(structure, true)
        assertEquals(structureXml, xml.trimXml())
    }

    @Test
    fun `deserialize Node tree`() {
        val node = deserializeXml(structureXml, Node::class)
        assertEquals(Structure::class, node::class)
        assertEquals(2, node.children.size)
        assertEquals(Room::class, node.children[0]::class)
        assertEquals(PolyRoom::class, node.children[1]::class)
        val reserialized = serializeToXmlStr(node, true)
        assertEquals(structureXml, reserialized.trimXml())
    }

    @Test
    fun `serialize PolyRoom`() {
        val xml = serializeToXmlStr(polygonRoom, true)
        assertEquals(polyRoomXml, xml.trimXml())
    }

    @Test
    fun `deserialize PolyRoom`() {
        val node = deserializeXml(polyRoomXml, Node::class)
        assertEquals(PolyRoom::class, node::class)
        val reserialized = serializeToXmlStr(node, true)
        assertEquals(polyRoomXml, reserialized.trimXml())
    }
}

fun String.trimXml() = trim().replace("\r\n", "\n")