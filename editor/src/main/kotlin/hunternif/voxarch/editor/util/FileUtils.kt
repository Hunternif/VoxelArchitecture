package hunternif.voxarch.editor.util

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