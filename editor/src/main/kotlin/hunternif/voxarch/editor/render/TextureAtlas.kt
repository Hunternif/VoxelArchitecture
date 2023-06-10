package hunternif.voxarch.editor.render

import org.joml.Vector2f
import org.joml.Vector2i
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
    private val mainFbo = FrameBuffer(sheet)

    private val _entries = linkedSetOf<AtlasEntry>()
    val entries: Collection<AtlasEntry> get() = _entries

    /** Points to the nearest free space, to its top-left corner. */
    private val cursor = Vector2i()

    /** Max height of textures in the current row. */
    private var rowHeight = 0

    fun init() {
        if (!sheet.isLoaded) {
            sheet.generate(width, height)
        }
        mainFbo.init(Viewport(0, 0, width, height))
    }

    /** Adds a new texture to the atlas, if it fits. */
    fun add(texture: Texture): AtlasEntry {
        if (!texture.isLoaded) texture.load()
        updateCursor(texture)

        val entry = AtlasEntry(this, texture, cursor)
        _entries.add(entry)

        // Add an extra layer of padding around the texture to prevent bleeding.
        // UVs on the source texture including padding:
        val uvPadding = Vector2f(padding.toFloat(), padding.toFloat())
            .mul(1f / entry.texture.width, 1f / entry.texture.height)
        copyTexture(
            texture,
            Vector2f(0f, 0f).sub(uvPadding),
            Vector2f(1f, 1f).add(uvPadding),
            mainFbo,
            entry.startPadded,
            entry.endPadded
        )

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

    companion object {
        /** Loads a file that contains a texture of an atlas with a rectangular grid */
        fun loadFromFile(
            path: Path,
            tileWidth: Int, tileHeight: Int,
            padding: Int = 0,
        ): TextureAtlas {
            val sheet = Texture(path)
            if (!sheet.isLoaded) sheet.load()
            val atlas = TextureAtlas(sheet.width, sheet.height, padding, sheet)
            atlas.init()

            val fakeTileTexture = Texture(Paths.get("tile"))
            fakeTileTexture.generate(tileWidth, tileHeight)

            // Add entries from the grid:
            val totalTiles = sheet.width * sheet.height / (tileWidth + padding*2) / (tileHeight + padding*2)
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