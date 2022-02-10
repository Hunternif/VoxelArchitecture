package hunternif.voxarch.editor

import hunternif.voxarch.editor.gui.FontAwesomeIcons

enum class Tool(
    val icon: String,
    val toolName: String,
    val shortcut: Char,
    val description: String = toolName,
    val fullDescription: String = "$description ($shortcut)"
) {
    SELECT(FontAwesomeIcons.Expand, "Select", 'M'),
    ADD_NODE(FontAwesomeIcons.ClinicMedical, "Add nodes", 'A'),
    MOVE(FontAwesomeIcons.ArrowsAlt, "Move", 'V',
    "Move selected nodes horizontally"),
    RESIZE(FontAwesomeIcons.ExpandAlt, "Resize", 'S',
        "Resize selected nodes"),
}