package hunternif.voxarch.editor.render

import org.joml.Vector2f
import org.joml.Vector2i
import kotlin.math.max

/**
 * Stitches textures together row by row.
 */
class TextureAtlas(
    val sheet: Texture,
) {
    private val entries = linkedSetOf<AtlasEntry>()

    /** Points to the nearest free space, to its top-left corner. */
    private val cursor = Vector2i()

    /** Max height of textures in the current row. */
    private var rowHeight = 0

    /** Adds a new texture to the atlas, if it fits. */
    fun add(texture: Texture): AtlasEntry {
        if (cursor.x + texture.width > sheet.width) {
            // start a new row
            if (cursor.y + rowHeight > sheet.height) {
                throw ArrayIndexOutOfBoundsException(
                    "Texture ${texture.filepath} doesn't fit into atlas."
                )
            }
            cursor.y += rowHeight
            cursor.x = 0
            rowHeight = 0
        }
        if (cursor.y + texture.height > sheet.height) {
            throw ArrayIndexOutOfBoundsException(
                "Texture ${texture.filepath} doesn't fit into atlas."
            )
        }
        val entry = AtlasEntry(
            texture, Vector2f(cursor),
            Vector2f(cursor).add(texture.width.toFloat(), texture.height.toFloat())
        )
        entries.add(entry)
        cursor.x += texture.width
        rowHeight = max(rowHeight, texture.height)
        return entry
    }
}

data class AtlasEntry(
    val texture: Texture,
    val uvStart: Vector2f,
    val uvEnd: Vector2f,
)