package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node

interface IBuildListener {
    fun onBeginBuild(node: Node)
    fun onPrepareChildren(parent: Node, children: Collection<Node>)
}