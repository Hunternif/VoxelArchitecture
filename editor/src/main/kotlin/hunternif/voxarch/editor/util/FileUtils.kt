package hunternif.voxarch.editor.util

import imgui.ImGui
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog.*
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun <T: Any> T.resourcePath(path: String): Path {
    return javaClass.classLoader.getResource(path)?.let {
        Paths.get(it.toURI())
    } ?: throw FileNotFoundException(path)
}

fun <T: Any> T.loadFromResources(path: String): ByteArray =
    Files.readAllBytes(resourcePath(path))

//TODO: use a separate thread
fun openFileDialog(fileFilter: String, onPathChosen: (Path) -> Unit) {
    val outPath = MemoryUtil.memAllocPointer(1)
    try {
        if (NFD_OKAY == NFD_OpenDialog(fileFilter, null, outPath)) {
            val pathStr = outPath.getStringUTF8(0)
            val path = Paths.get(pathStr)
            onPathChosen(path)
        }
    } catch (e: Exception) {
        ImGui.text(e.toString())
    } finally {
        MemoryUtil.memFree(outPath)
    }
}