package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.BaseAppTest
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.util.assertNodeTreeEqualsRecursive
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.junit.Before
import org.junit.Test

class BlueprintDelegateTest : BaseAppTest() {
    private lateinit var mainBp: Blueprint
    private lateinit var delegateBp: Blueprint
    private lateinit var refNode: BlueprintNode
    private lateinit var wallNode: BlueprintNode

    @Before
    fun setup() {
        // Main BP:
        mainBp = app.addNewBlueprint(app.state.rootNode)

        val n1 = app.newBlueprintNode(mainBp, "Room",
            autoLinkFrom = mainBp.start.outputs.first())!!

        refNode = app.newBlueprintNode(mainBp, "Blueprint",
            autoLinkFrom = n1.outputs.first())!!

        // Delegate BP:
        delegateBp = app.newBlueprint()

        wallNode = app.newBlueprintNode(delegateBp, "Wall",
            autoLinkFrom = delegateBp.start.outputs.first())!!

        // Main BP references Delegate BP:
        app.setDelegateBlueprint(refNode, delegateBp)
    }

    @Test
    fun `execute blueprint that runs another blueprint`() {
        app.generateNodes()

        val refTree = Structure().apply {
            room(Vec3.ZERO, Vec3.ZERO) {
                wall(Vec3.ZERO, Vec3.ZERO)
            }
        }
        assertNodeTreeEqualsRecursive(refTree, app.state.rootNode.node, testTags = false)
    }

    @Test
    fun `execute blueprint with out slots`() {
        // Create "out slot" nodes in Delegate BP:
        app.newBlueprintNode(delegateBp, "Out slot",
            autoLinkFrom = delegateBp.start.outputs.first())
        app.newBlueprintNode(delegateBp, "Out slot",
            autoLinkFrom = wallNode.outputs.first())

        // This should result in a new slot on the reference node in Main BP.
        // Link new nodes to that slot:
        val (outSlot1, outSlot2) = refNode.outputs
        app.newBlueprintNode(mainBp, "Room",
            autoLinkFrom = outSlot1
        )
        app.newBlueprintNode(mainBp, "Node",
            autoLinkFrom = outSlot2
        )
        app.newBlueprintNode(mainBp, "Floor",
            autoLinkFrom = outSlot2
        )
        app.generateNodes()

        val refTree = Structure().apply {
            room(Vec3.ZERO, Vec3.ZERO) {
                wall(Vec3.ZERO, Vec3.ZERO) {
                    node(Vec3.ZERO)
                    floor()
                }
                room(Vec3.ZERO, Vec3.ZERO)
            }
        }
        assertNodeTreeEqualsRecursive(refTree, app.state.rootNode.node, testTags = false)
    }
}