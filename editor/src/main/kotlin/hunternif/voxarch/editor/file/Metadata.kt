package hunternif.voxarch.editor.file

/** Current version */
const val FORMAT_VERSION: Int = 9

/** Project metadata */
class Metadata(
    var formatVersion: Int = 0,
    var seed: Long = 0L,
)