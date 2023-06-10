package hunternif.voxarch.editor.render

import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.opengl.GL32.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.math.max

/**
 * Stitches textures together row by row.
 */
class TextureAtlas(
    val width: Int,
    val height: Int,
    /** To prevent texture bleeding. */
    val padding: Int = 0,
    val sheet: Texture = Texture(Paths.get("sheet"))
) {
    private var mainFboID: Int = 0

    private val _entries = linkedSetOf<AtlasEntry>()
    val entries: Collection<AtlasEntry> get() = _entries

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
        _entries.add(entry)

        copyTexture(entry)

        cursor.x += entry.sizePadded.x
        rowHeight = max(rowHeight, entry.sizePadded.y)
        return entry
    }

    /** Shifts cursor to accommodate a given new texture. */
    private fun updateCursor(newTexture: Texture) {
        if (cursor.x + newTexture.width + padding * 2 > width) {
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
        if (cursor.y + newTexture.height + padding * 2 > height) {
            throw ArrayIndexOutOfBoundsException(
                "Texture ${newTexture.filepath} doesn't fit into atlas."
            )
        }
    }

    /** Renders the entry's texture onto the main sheet texture at [cursor],
     * adding an extra layer of [padding] to prevent bleeding. */
    private fun copyTexture(entry: AtlasEntry) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mainFboID)

        glEnable(GL_TEXTURE_2D)
        glDisable(GL_LIGHTING)
        glDisable(GL_BLEND)
        glDisable(GL_DEPTH_TEST)
        glColor4f(1f, 1f, 1f, 1f)

        entry.texture.bind()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glViewport(0, 0, sheet.width, sheet.height)
        glMatrixMode(GL_PROJECTION)
        glPushMatrix()
        glLoadIdentity()
        glOrtho(0.0, sheet.width.toDouble(), 0.0, sheet.height.toDouble(), 0.0, 100.0)

        // UVs on the source texture including padding:
        val uvPadding = Vector2f(padding.toFloat(), padding.toFloat())
            .mul(1f / entry.texture.width, 1f / entry.texture.height)
        val uvStartPadded = Vector2f(0f, 0f).sub(uvPadding)
        val uvEndPadded = Vector2f(1f, 1f).add(uvPadding)

        glBegin(GL_QUADS)
        glTexCoord2f(uvEndPadded.x, uvStartPadded.y)
        glVertex3i(entry.endPadded.x, entry.startPadded.y, 0)
        glTexCoord2f(uvEndPadded.x, uvEndPadded.y)
        glVertex3i(entry.endPadded.x, entry.endPadded.y, 0)
        glTexCoord2f(uvStartPadded.x, uvEndPadded.y)
        glVertex3i(entry.startPadded.x, entry.endPadded.y, 0)
        glTexCoord2f(uvStartPadded.x, uvStartPadded.y)
        glVertex3i(entry.startPadded.x, entry.startPadded.y, 0)
        glEnd()

        glPopMatrix()

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    companion object {
        /** Loads a file that contains a texture of an atlas with a rectangular grid */
        fun loadFromFile(
            path: Path,
            tileWidth: Int, tileHeight: Int,
        ): TextureAtlas {
            val sheet = Texture(path)
            if (!sheet.isLoaded) sheet.load()
            val atlas = TextureAtlas(sheet.width, sheet.height, 0, sheet)

            val fakeTileTexture = Texture(Paths.get("tile"))
            fakeTileTexture.generate(tileWidth, tileHeight)

            // Add entries from the grid:
            val totalTiles = sheet.width * sheet.height / tileWidth / tileHeight
            for (i in 0 until totalTiles) {
                atlas.run {
                    updateCursor(fakeTileTexture)

                    val entry = AtlasEntry(this, fakeTileTexture, cursor)
                    _entries.add(entry)

                    cursor.x += entry.sizePadded.x
                    rowHeight = max(rowHeight, entry.sizePadded.y)
                }
            }

            return atlas
        }
    }
}

class AtlasEntry(
    /** The combined atlas texture. */
    atlas: TextureAtlas,
    /** The original texture file. */
    val texture: Texture,
    cursor: Vector2i,
) {
    /** Start corner on atlas including padding. */
    val startPadded = Vector2i(cursor)

    /** Start corner on atlas. */
    val start = Vector2i(startPadded).add(atlas.padding, atlas.padding)

    /** End corner on atlas. */
    val end = Vector2i(start).add(texture.width, texture.height)

    /** End corner on atlas including padding. */
    val endPadded = Vector2i(end).add(atlas.padding, atlas.padding)

    val sizePadded = Vector2i(endPadded).sub(startPadded)

    /** UV of the start corner on atlas. */
    val uvStart: Vector2f = Vector2f(start).mul(
        1f / atlas.sheet.width.toFloat(),
        1f / atlas.sheet.height.toFloat(),
    )

    /** UV of the end corner on atlas. */
    val uvEnd: Vector2f = Vector2f(end).mul(
        1f / atlas.sheet.width.toFloat(),
        1f / atlas.sheet.height.toFloat(),
    )
}