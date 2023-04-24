package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.actions.history.HistoryAction

/**
 * To be dispatched on the event bus
 */
interface Event

/** Working with blueprints and their contents */
interface BlueprintEvent : Event

/** Working with files */
interface FileEvent : Event {
    companion object {
        val SAVE_PROJECT = object : FileEvent {}
    }
}

/** Running procedural generation */
interface GenEvent : Event

/** Changing the structure of the scene tree */
interface SceneEvent : Event

/** Selecting objects in the scene */
interface SelectEvent : Event

/** Changing the stylesheet */
interface StyleEvent : Event

/** Changing properties of nodes */
interface TransformEvent : Event

/** Changing things in the UI that don't affect underlying data */
interface UIEvent : Event {
    companion object {
        val RELOAD_STYLE_EDITOR = object : UIEvent {}
        val OPEN_DIALOG = object : UIEvent {}
        val SET_TOOL = object : UIEvent {}
        val SET_NODE_TYPE = object : UIEvent {}
        val SET_RENDER_MODE = object : UIEvent {}
        val SET_SHADING_MODE = object : UIEvent {}
        val CENTER_CAMERA = object : UIEvent {}
        val REDRAW_NODES = object : UIEvent {}
        val REDRAW_VOXELS = object : UIEvent {}
        val CLEAR_NEW_NODE_FRAME = object : UIEvent {}
        val HIGHLIGHT_FACE = object : UIEvent {}
        val SET_OVERLAY_TEXT = object : UIEvent {}
        val TOGGLE_LOGS = object : UIEvent {}
    }
}

/** Indicates that [action] was reverted */
class UndoEvent(action: HistoryAction) : Event