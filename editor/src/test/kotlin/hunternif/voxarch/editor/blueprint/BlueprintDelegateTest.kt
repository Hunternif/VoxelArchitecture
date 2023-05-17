package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.util.assertNodeTreeEqualsRecursive
import hunternif.voxarch.plan.Structure
import hunternif.voxarch.plan.room
import hunternif.voxarch.plan.wall
import hunternif.voxarch.vector.Vec3
import org.junit.Test

class BlueprintDelegateTest : BaseAppTest() {
    @Test
    fun `execute blueprint that runs another blueprint`() {
        // Blueprint 1
        val bp1 = app.addNewBlueprint(app.state.rootNode)

        val n1 = app.newBlueprintNode(bp1, "Room",
            autoLinkFrom = bp1.start.outputs.first())!!

        val refNode = app.newBlueprintNode(bp1, "Blueprint",
            autoLinkFrom = n1.outputs.first())!!

        // Blueprint 2
        val bp2 = app.newBlueprint()

        app.newBlueprintNode(bp2, "Wall",
            autoLinkFrom = bp2.start.outputs.first())!!

        // Blueprint 1 references Blueprint 2
        app.setDelegateBlueprint(refNode, bp2)

        app.generateNodes()

        val refTree = Structure().apply {
            room(Vec3.ZERO, Vec3.ZERO) {
                wall(Vec3.ZERO, Vec3.ZERO)
            }
        }
        assertNodeTreeEqualsRecursive(refTree, app.state.rootNode.node, testTags = false)
    }
}