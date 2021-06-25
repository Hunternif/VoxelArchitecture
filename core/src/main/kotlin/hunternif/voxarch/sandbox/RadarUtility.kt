package hunternif.voxarch.sandbox

import hunternif.voxarch.world.*
import java.awt.Color
import java.awt.image.BufferedImage

private const val groundColor = 0xccffcc
private const val slopeColor = 0xe58066
private const val topColor = 0xeecc66
private const val perimeterGroundColor = 0x667fe5
private const val perimeterSlopeColor = 0x925fa5

fun HeightMap.getColor(height: Int, baseColor: Int): Int {
    if (maxHeight <= minHeight) return 0x000000
    val color = Color(baseColor)
    val light = 0xff * (height - minHeight) / (maxHeight - minHeight)
    val r = color.red * light / 0xff
    val g = color.green * light / 0xff
    val b = color.blue * light / 0xff
    return (r shl 16) + (g shl 8) + b
}

fun HeightMap.image(): BufferedImage {
    val mountains = detectMountains()
    minHeight -= 10 // to see the ocean color clearly

    val image = BufferedImage(width, length, BufferedImage.TYPE_INT_RGB)
    for (p in this) {
        val baseColor = when {
            mountains.any { p in it.perimeter } -> when {
                mountains.any { p in it.slope } -> perimeterSlopeColor
                else -> perimeterGroundColor
            }
            mountains.any { p in it.slope } -> slopeColor
            mountains.any { p in it.top } -> topColor
            else -> groundColor
        }
        val color = getColor(at(p), baseColor)
        image.setRGB(p.x, p.y, color)
    }
    return image
}