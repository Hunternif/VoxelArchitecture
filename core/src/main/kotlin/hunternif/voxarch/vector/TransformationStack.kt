package hunternif.voxarch.vector

import java.util.*
import java.util.ArrayDeque

/**
 * Keeps track of LINEAR transformations as we traverse the Node tree.
 */
class TransformationStack(
    /** Combined transformation from the bottom of the stack. */
    private val current: LinearTransformation = LinearTransformation()
) : ILinearTransformation by current {

    constructor(start: ILinearTransformation) : this(
        LinearTransformation(start.angleY, start.matrix.clone())
    )

    /** Stack of transformations. */
    private val stack: Deque<LinearTransformation> = ArrayDeque()

    /** Pushes the current transformation onto the stack.
     * Similar to OpenGL glPushMatrix(). */
    fun push() {
        stack.push(current.clone())
    }

    /** Pops the last transformation off the stack.
     * Similar to OpenGL glPopMatrix(). */
    fun pop(): LinearTransformation {
        if (stack.isNotEmpty()) {
            val prev = stack.poll()
            current.angleY = prev.angleY
            current.matrix = prev.matrix
        }
        return current
    }
}