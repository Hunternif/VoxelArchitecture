package hunternif.voxarch.editor

import java.nio.file.Path
import java.nio.file.Paths

fun <T: Any> T.resourcePath(path: String): Path =
    Paths.get(javaClass.classLoader.getResource(path)!!.toURI())
