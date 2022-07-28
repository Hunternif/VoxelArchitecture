package hunternif.voxarch.editor.file

import hunternif.voxarch.plan.PolygonShape
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
          <node class="Room" origin="(0.0, 0.0, 0.0)" size="(5.0, 6.0, 7.0)" start="(0.0, 0.0, 0.0)" centered="true">
            <node class="Wall" start="(0.0, 0.0, 0.0)" end="(1.0, 2.0, 0.0)"/>
            <node class="Wall" start="(0.0, 0.0, 1.0)" end="(1.0, 2.0, 1.0)"/>
            <node class="Floor" height="1.0"/>
          </node>
          <node class="PolygonRoom" origin="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" start="(0.0, 0.0, 0.0)" centered="false" shape="ROUND">
            <polygon class="Path"/>
          </node>
        </node>
        """.trimIndent()

    private val structure = XmlStructure(Vec3(1, 2, 3)).apply {
        children.add(
            XmlRoom(Vec3.ZERO, Vec3(5, 6, 7), Vec3.ZERO, true).apply {
                children.add(XmlWall(Vec3(0, 0, 0), Vec3(1, 2, 0)))
                children.add(XmlWall(Vec3(0, 0, 1), Vec3(1, 2, 1)))
                children.add(XmlFloor(1.0))
            }
        )
        children.add(
            XmlPolygonRoom(
                Vec3.ZERO, Vec3(7, 8, 9), Vec3.ZERO, false, PolygonShape.ROUND
            )
        )
    }

    private val polygonRoomXml = """
        <node class="PolygonRoom" origin="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" start="(0.0, 0.0, 0.0)" centered="false" shape="ROUND">
          <polygon class="Path">
            <point>(0.0, 0.0, 0.0)</point>
            <point>(1.0, 2.0, 3.0)</point>
            <point>(0.0, 0.0, 0.0)</point>
          </polygon>
        </node>
        """.trimIndent()

    private val polygonRoom = XmlPolygonRoom(
        Vec3.ZERO,
        Vec3(7, 8, 9),
        Vec3.ZERO,
        false,
        PolygonShape.ROUND,
        XmlPath(listOf(Vec3(0, 0, 0), Vec3(1, 2, 3), Vec3(0, 0, 0)))
    )

    @Test
    fun `serialize Node tree`() {
        val xml = serializeToXmlStr(structure, true)
        assertEquals(structureXml, xml.trim().replace("\r\n", "\n"))
    }

    @Test
    fun `deserialize Node tree`() {
        val node = deserializeXml(structureXml, XmlStructure::class)
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
        val node = deserializeXml(polygonRoomXml, XmlPolygonRoom::class)
        val reserialized = serializeToXmlStr(node, true)
        assertEquals(polygonRoomXml, reserialized.trim().replace("\r\n", "\n"))
    }
}