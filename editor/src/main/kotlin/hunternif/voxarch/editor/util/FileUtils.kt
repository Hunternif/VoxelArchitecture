package hunternif.voxarch.editor.util

import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog.*
import java.io.FileNotFoundException
import java.net.URI
import java.nio.file.*
import java.nio.file.spi.FileSystemProvider

private lateinit var jarFs: FileSystem

// Thanks to https://stackoverflow.com/a/22605905/1093712
private fun getJarFs(uri: URI): FileSystem {
    if (!::jarFs.isInitialized) {
        // drop the "jar:file:/" in the beginning "jar:" and what comes after "!"
        val uriStr = uri.toString()
        val jarFileUri = uriStr.substring(10, uriStr.indexOf('!'))
        println("Loading jar file: $jarFileUri")
        jarFs = newZipFileSystem(Paths.get(jarFileUri))
    }
    return jarFs
}

private fun getInnerFileUri(uri: URI) =
    uri.toString().run { substring(indexOf('!') + 1) }


fun <T: Any> T.resourcePath(path: String): Path {
    return javaClass.classLoader.getResource(path)?.let {
        val uri = it.toURI()
        if (uri.scheme == "jar") {
            val fs = getJarFs(uri)
            val innerUri = getInnerFileUri(uri)
            println("loading path from jar: $innerUri")
            return fs.getPath(innerUri)
        }
        return Paths.get(uri)
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