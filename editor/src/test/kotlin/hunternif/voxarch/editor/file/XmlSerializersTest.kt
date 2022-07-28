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
        <node class="Structure" origin="(1.0, 2.0, 3.0)">
          <node class="Room" origin="(0.0, 0.0, 0.0)" size="(5.0, 6.0, 7.0)" start="(-2.5, 0.0, -3.5)" centered="true">
            <node class="Wall" start="(0.0, 0.0, 0.0)" end="(1.0, 2.0, 0.0)" transparent="true"/>
            <node class="Wall" start="(0.0, 0.0, 1.0)" end="(1.0, 2.0, 1.0)" transparent="false"/>
            <node class="Floor" height="1.0"/>
          </node>
          <node class="PolygonRoom" origin="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" start="(0.0, 0.0, 0.0)" centered="false" shape="ROUND">
            <polygon class="Path"/>
          </node>
        </node>
        """.trimIndent()

    private val structure = Structure(Vec3(1, 2, 3)).apply {
        centeredRoom(Vec3.ZERO, Vec3(5, 6, 7)) {
            wall(Vec3(0, 0, 0), Vec3(1, 2, 0)) { transparent = true }
            wall(Vec3(0, 0, 1), Vec3(1, 2, 1))
            floor(1.0)
            addChild(Gate()) // missing node type
        }
        polygonRoom(Vec3.ZERO, Vec3(7, 8, 9)) { shape = PolygonShape.ROUND }
    }

    private val polygonRoomXml = """
        <node class="PolygonRoom" origin="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" start="(0.0, 0.0, 0.0)" centered="false" shape="ROUND">
          <polygon class="Path">
            <point>(0.0, 0.0, 0.0)</point>
            <point>(1.0, 2.0, 3.0)</point>
          </polygon>
        </node>
        """.trimIndent()

    private val polygonRoom = PolygonRoom(Vec3.ZERO, Vec3(7, 8, 9)).apply {
        start = Vec3.ZERO
        shape = PolygonShape.ROUND
        polygon.apply {
            addPoint(Vec3.ZERO)
            addPoint(Vec3(1, 2, 3))
            loopToStart()
        }
    }

    @Test
    fun `serialize Node tree`() {
        val xml = serializeToXmlStr(structure, true)
        assertEquals(structureXml, xml.trim().replace("\r\n", "\n"))
    }

    @Test
    fun `deserialize Node tree`() {
        val node = deserializeXml(structureXml, Node::class)
        assertEquals(Structure::class, node::class)
        assertEquals(2, node.children.size)
        assertEquals(Room::class, node.children[0]::class)
        assertEquals(PolygonRoom::class, node.children[1]::class)
        val reserialized = serializeToXmlStr(node, true)
        assertEquals(structureXml, reserialized.trim().replace("\r\n", "\n"))
    }

    @Test
    fun `serialize PolygonRoom`() {
        val xml = serializeToXmlStr(polygonRoom, true)
        assertEquals(polygonRoomXml, xml.trim().replace("\r\n", "\n"))
    }

    @Test
    fun `deserialize PolygonRoom`() {
        val node = deserializeXml(polygonRoomXml, Node::class)
        assertEquals(PolygonRoom::class, node::class)
        val reserialized = serializeToXmlStr(node, true)
        assertEquals(polygonRoomXml, reserialized.trim().replace("\r\n", "\n"))
    }
}