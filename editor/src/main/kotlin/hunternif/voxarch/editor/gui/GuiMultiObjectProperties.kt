package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.unselectObject
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.util.isCollectionEqual
import imgui.ImGui
import imgui.type.ImBoolean

/**
 * Displays a list of objects and their properties
 */
class GuiMultiObjectProperties(
    private val app: EditorApp,
    private val guiBase: GuiBase,
) {
    /** List of displayed entries */
    private val list = arrayListOf<Entry>()

    /** Lazy map of entry id to gui. */
    private val guiMap = mutableMapOf<Int, GuiObjectProperties>()

    // Update timer
    private val nodeTimer = Timer(0.02)
    private var isListDirty = false

    fun render() {
        checkSelectedNodes()
        renderHeaderText()
        if (list.size == 1) {
            // Single item
            list.first().gui.render()
        } else {
            // Multiple items as collapsing headers
            list.forEach {
                if (ImGui.collapsingHeader(it.labelForImgui, it.visibleFlag)) {
                    it.gui.render()
                }
                if (!it.visibleFlag.get()) {
                    app.unselectObject(it.obj)
                    isListDirty = true
                }
            }
        }
    }

    /** Check which nodes are currently selected, and update the state of gui */
    private fun checkSelectedNodes() {
        nodeTimer.runAtInterval {
            // check if list has changed:
            if (!isCollectionEqual(list, app.state.selectedObjects)) isListDirty = true
        }
        if (isListDirty) updateList()
    }

    private fun updateList() {
        list.clear()
        list.addAll(app.state.selectedObjects.map {
            Entry(it, sceneObjectToSingleLine(it))
        })
        isListDirty = false
    }

    private fun renderHeaderText() {
        if (list.size > 1) {
            ImGui.text(list.size.toString())
            ImGui.sameLine()
            ImGui.text("objects")
        }
    }

    private val Entry.gui: GuiObjectProperties
        get() = guiMap.getOrPut(id) { GuiObjectProperties(app, guiBase) }.also {
            it.obj = obj
        }

    private data class Entry(
        val obj: SceneObject,
        val label: String,
    ) {
        val id = obj.id
        val labelForImgui = "$label##$id"

        /** Used for closing this entry */
        val visibleFlag = ImBoolean(true)
    }
}