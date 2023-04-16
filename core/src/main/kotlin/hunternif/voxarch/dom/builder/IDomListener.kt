package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement

interface IDomListener {
    fun onBeginBuild(element: StyledElement<*>)
    fun onPrepareChildren(parent: StyledElement<*>, children: List<StyledElement<*>>)
    fun onLayoutChildren(parent: StyledElement<*>, children: List<StyledElement<*>>)
    fun onEndBuild(element: StyledElement<*>)
}