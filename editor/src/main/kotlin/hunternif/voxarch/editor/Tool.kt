package hunternif.voxarch.editor

import hunternif.voxarch.editor.gui.FontAwesomeIcons

enum class Tool(
    val icon: String,
    val toolName: String,
    val description: String = toolName,
) {
    SELECT(FontAwesomeIcons.Expand, "Select"),
    ADD_NODE(FontAwesomeIcons.Plus, "Add nodes"),
    MOVE(FontAwesomeIcons.ArrowsAlt, "Move",
    "Move selected nodes horizontally"),
}