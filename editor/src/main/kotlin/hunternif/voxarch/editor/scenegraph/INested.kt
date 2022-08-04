package hunternif.voxarch.editor.scenegraph

interface INested<T : INested<T>> {
    var parent: T?
    val children: Collection<T>
    fun addChild(child: T)
    fun removeChild(child: T)
    fun removeAllChildren()
}