package hunternif.voxarch.editor

import hunternif.voxarch.editor.gui.FontAwesomeIcons

enum class Tool(
    val icon: String,
    val description: String,
) {
    SELECT(FontAwesomeIcons.VectorSquare, "Select"),
    ADD_NODE(FontAwesomeIcons.Plus, "Add nodes"),
}