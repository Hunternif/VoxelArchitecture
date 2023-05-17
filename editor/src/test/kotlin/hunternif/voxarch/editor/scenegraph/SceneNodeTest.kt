package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.plan.*
import org.junit.Assert
import org.junit.Test

class SceneNodeTest {
    @Test
    fun `query nodes by class and by type`() {
        val root = SceneNode(1, Structure())
        val parent = SceneNode(2, Room().apply { tags += "parent_room" })
        val prop1 = SceneNode(3, Prop("prop").apply { tags += "child" })
        val prop2 = SceneNode(4, Prop("other_prop"))
        val child1 = SceneNode(5, Room().apply {
            tags += "child_room"
            tags += "child"
        })
        val noTags = SceneNode(6, Room())
        root.addChild(parent)
        parent.addChild(prop1)
        parent.addChild(child1)
        parent.addChild(noTags)
        child1.addChild(prop2)

        Assert.assertEquals(
            emptyList<SceneNode>(),
            root.query<Node>("unknown_tag").toList()
        )
        Assert.assertEquals(
            listOf(root, parent, prop1, child1, noTags, prop2),
            root.query<Node>().toList()
        )
        Assert.assertEquals(
            listOf(parent, child1, noTags),
            root.query<Room>().toList()
        )
        Assert.assertEquals(
            listOf(prop1, prop2),
            root.query<Prop>().toList()
        )
        Assert.assertEquals(
            listOf(parent),
            root.query<Node>("parent_room").toList()
        )
        Assert.assertEquals(
            listOf(prop1, child1),
            root.query<Node>("child").toList()
        )
        Assert.assertEquals(
            listOf(child1),
            root.query<Node>("child", "child_room").toList()
        )
        Assert.assertEquals(
            listOf(child1),
            root.query<Room>("child").toList()
        )
    }
}