package hunternif.voxarch.editor.render

import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.opengl.GL32.*
import kotlin.math.max

/**
 * Stitches textures together row by row.
 */
class TextureAtlas(val width: Int, val height: Int) {
    val sheet = Texture("sheet")
    private var mainFboID: Int = 0

    private val entries = linkedSetOf<AtlasEntry>()

    /** Points to the nearest free space, to its top-left corner. */
    private val cursor = Vector2i()

    /** Max height of textures in the current row. */
    private var rowHeight = 0

    fun init() {
        sheet.generate(width, height)
        mainFboID = glGenFramebuffers()
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mainFboID)
        glFramebufferTexture2D(
            GL_DRAW_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            sheet.texID,
            0
        )
    }

    /** Adds a new texture to the atlas, if it fits. */
    fun add(texture: Texture): AtlasEntry {
        if (!texture.isLoaded) texture.load()
        updateCursor(texture)

        val entry = AtlasEntry(this, texture, cursor)
        entries.add(entry)

        copyTexture(entry)

        cursor.x += texture.width
        rowHeight = max(rowHeight, texture.height)
        return entry
    }

    /** Shifts cursor to accommodate a given new texture. */
    private fun updateCursor(newTexture: Texture) {
        if (cursor.x + newTexture.width > width) {
            // start a new row
            if (cursor.y + rowHeight > height) {
                throw ArrayIndexOutOfBoundsException(
                    "Texture ${newTexture.filepath} doesn't fit into atlas."
                )
            }
            cursor.y += rowHeight
            cursor.x = 0
            rowHeight = 0
        }
        if (cursor.y + newTexture.height > height) {
            throw ArrayIndexOutOfBoundsException(
                "Texture ${newTexture.filepath} doesn't fit into atlas."
            )
        }
    }

    /** Renders the entry's texture onto the main sheet texture at [cursor]. */
    private fun copyTexture(entry: AtlasEntry) {
        val texWidth = entry.texture.width
        val texHeight = entry.texture.height

        val texFboID = glGenFramebuffers()
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mainFboID)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, texFboID)
        glFramebufferTexture2D(
            GL_READ_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            entry.texture.texID,
            0
        )
        glBlitFramebuffer(
            0, 0, texWidth, texHeight,
            entry.start.x, entry.start.y,
            entry.end.x, entry.end.y,
            GL_COLOR_BUFFER_BIT, GL_NEAREST,
        )
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
}

class AtlasEntry(
    /** The combined atlas texture. */
    atlas: TextureAtlas,
    /** The original texture file. */
    val texture: Texture,
    start: Vector2i,
) {
    /** Start corner in absolute texture coordinates. */
    val start: Vector2i = Vector2i(start)

    /** End corner in absolute texture coordinates. */
    val end: Vector2i = Vector2i(start).add(texture.width, texture.height)

    /** UV of the start corner on the atlas texture. */
    val uvStart: Vector2f = Vector2f(
        start.x.toFloat() / atlas.sheet.width.toFloat(),
        start.y.toFloat() / atlas.sheet.height.toFloat(),
    )

    /** UV of the end corner on the atlas texture. */
    val uvEnd: Vector2f = Vector2f(
        end.x.toFloat() / atlas.sheet.width.toFloat(),
        end.y.toFloat() / atlas.sheet.height.toFloat(),
    )
}