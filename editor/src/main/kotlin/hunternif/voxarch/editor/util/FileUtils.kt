package hunternif.voxarch.editor.util

import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog.*
import java.io.FileNotFoundException
import java.nio.file.*
import java.nio.file.spi.FileSystemProvider

fun <T: Any> T.resourcePath(path: String): Path {
    return javaClass.classLoader.getResource(path)?.let {
        Paths.get(it.toURI())
    } ?: throw FileNotFoundException(path)
}

fun <T: Any> T.loadFromResources(path: String): ByteArray =
    Files.readAllBytes(resourcePath(path))

fun newZipFileSystem(path: Path): FileSystem {
    val env = mapOf("create" to "true")
    for (provider in FileSystemProvider.installedProviders()) {
        if ("jar".equals(provider.scheme, ignoreCase = true)) {
            return provider.newFileSystem(path, env)
        }
    }
    throw ProviderNotFoundException("Provider jar not found")
}

//TODO: use a separate thread
fun openFileDialog(fileFilter: String, onPathChosen: (Path) -> Unit) {
    val outPath = MemoryUtil.memAllocPointer(1)
    try {
        if (NFD_OKAY == NFD_OpenDialog(fileFilter, null, outPath)) {
            val pathStr = outPath.getStringUTF8(0)
            val path = Paths.get(pathStr)
            onPathChosen(path)
        }
    } finally {
        MemoryUtil.memFree(outPath)
    }
}

//TODO: use a separate thread
fun saveFileDialog(fileFilter: String, onPathChosen: (Path) -> Unit) {
    val outPath = MemoryUtil.memAllocPointer(1)
    try {
        if (NFD_OKAY == NFD_SaveDialog(fileFilter, null, outPath)) {
            val pathStr = outPath.getStringUTF8(0)
            val path = Paths.get(pathStr)
            onPathChosen(path)
        }
    } finally {
        MemoryUtil.memFree(outPath)
    }
}