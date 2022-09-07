package hunternif.voxarch.editor.file

import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneRegistry
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.junit.Assert.assertEquals
import org.junit.Test

class XmlSceneObjectSerializerTest {

    private val nodeXml = """
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

    private val sceneXml = """
        <obj class="SceneNode" id="0" generated="false" color="4296fa" alpha="0.2">
          <obj class="SceneNode" id="1" generated="false" color="4296fa" alpha="0.2">
            <obj class="SceneNode" id="3" generated="false" color="4296fa" alpha="0.2">
              <node class="Wall" start="(0.0, 0.0, 0.0)" end="(1.0, 2.0, 0.0)" transparent="true"/>
            </obj>
            <obj class="SceneNode" id="4" generated="false" color="4296fa" alpha="0.2">
              <node class="Wall" start="(0.0, 0.0, 1.0)" end="(1.0, 2.0, 1.0)" transparent="false"/>
            </obj>
            <obj class="SceneNode" id="5" generated="false" color="4296fa" alpha="0.2">
              <node class="Floor" height="1.0"/>
            </obj>
            <node class="Room" origin="(0.0, 0.0, 0.0)" size="(5.0, 6.0, 7.0)" start="(-2.5, 0.0, -3.5)" centered="true"/>
          </obj>
          <obj class="SceneNode" id="2" generated="false" color="4296fa" alpha="0.2">
            <node class="PolygonRoom" origin="(0.0, 0.0, 0.0)" size="(7.0, 8.0, 9.0)" start="(0.0, 0.0, 0.0)" centered="false" shape="ROUND">
              <polygon class="Path"/>
            </node>
          </obj>
          <node class="Structure" origin="(1.0, 2.0, 3.0)"/>
        </obj>
        """.trimIndent()

    private val structure = Structure(Vec3(1, 2, 3)).apply {
        centeredRoom(Vec3.ZERO, Vec3(5, 6, 7)) {
            wall(Vec3(0, 0, 0), Vec3(1, 2, 0)) { transparent = true }
            wall(Vec3(0, 0, 1), Vec3(1, 2, 1))
            floor(1.0)
        }
        polygonRoom(Vec3.ZERO, Vec3(7, 8, 9)) { shape = PolygonShape.ROUND }
    }

    // Don't clear the registry between runs!
    // This test uses the same tree with the same IDs, so we should verify those IDs.
    private val registry = SceneRegistry()

    @Test
    fun `serialize Node tree as SceneObject`() {
        val obj = registry.createNodes(structure)
        val actualSceneXml = serializeToXmlStr(obj, true).trimXml()
        val actualNodeXml = serializeToXmlStr(obj.node, true).trimXml()
        assertEquals(sceneXml, actualSceneXml)
        assertEquals(nodeXml, actualNodeXml)
    }

    @Test
    fun `deserialize Node tree as SceneObject`() {
        val obj = deserializeXml(sceneXml, SceneObject::class)
        assertEquals(SceneNode::class, obj::class)
        val parent = obj as SceneNode
        assertEquals(Structure::class, parent.node::class)
        assertEquals(2, obj.children.size)
        assertEquals(2, obj.node.children.size)

        val room1 = obj.children.toList()[0] as SceneNode
        val room2 = obj.children.toList()[1] as SceneNode
        assertEquals(Room::class, room1.node::class)
        assertEquals(PolygonRoom::class, room2.node::class)
        assertEquals(room1.node, parent.node.children[0])
        assertEquals(room2.node, parent.node.children[1])

        val reserialized = serializeToXmlStr(obj, true)
        assertEquals(sceneXml, reserialized.trimXml())
    }
}