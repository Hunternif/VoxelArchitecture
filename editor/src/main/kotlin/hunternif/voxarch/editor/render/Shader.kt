package hunternif.voxarch.editor.render

import org.joml.*
import org.lwjgl.opengl.GL32.*
import org.lwjgl.system.MemoryStack
import java.nio.file.Files
import java.nio.file.Path

abstract class Shader {
    @PublishedApi
    internal var shaderProgramID = 0

    protected var isInitialized = false

    abstract fun init()

    fun init(
        vertex: Path,
        fragment: Path,
        action: Shader.() -> Unit = {}
    ) {
        if (isInitialized) return
        shaderProgramID = loadShaderProgram(vertex, fragment)
        use(action)
        isInitialized = true
    }

    inline fun render(viewProj: Matrix4f, crossinline action: () -> Unit) {
        use {
            startFrame()
            uploadMat4f("uViewProj", viewProj)
            action()
        }
    }

    /** Called before a frame */
    @PublishedApi
    internal open fun startFrame() {}

    val inUse get() = shaderInUse == this

    /** Tells OpenGL to use this shader while performing [action] */
    inline fun use(crossinline action: Shader.() -> Unit) {
        val prevShaderInUse = shaderInUse
        var shouldReset = false
        if (shaderInUse != this) {
            glUseProgram(shaderProgramID)
            shaderInUse = this
            shouldReset = true
        }
        this.action()
        if (inUse && shouldReset) {
            glUseProgram(prevShaderInUse?.shaderProgramID ?: 0)
            shaderInUse = null
        }
    }

    fun uploadMat4f(varName: String, mat4: Matrix4f) = MemoryStack.stackPush().use { stack ->
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        val matBuffer = stack.mallocFloat(16)
        mat4[matBuffer]
        glUniformMatrix4fv(varLocation, false, matBuffer)
    }

    fun uploadMat3f(varName: String, mat3: Matrix3f) = MemoryStack.stackPush().use { stack ->
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        val matBuffer = stack.mallocFloat(9)
        mat3[matBuffer]
        glUniformMatrix3fv(varLocation, false, matBuffer)
    }

    fun uploadVec4f(varName: String, vec: Vector4f) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w)
    }

    fun uploadVec3f(varName: String, vec: Vector3f) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform3f(varLocation, vec.x, vec.y, vec.z)
    }

    fun uploadVec2f(varName: String, vec: Vector2f) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform2f(varLocation, vec.x, vec.y)
    }

    fun uploadFloat(varName: String, value: Float) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform1f(varLocation, value)
    }

    fun uploadTexture(varName: String, slot: Int) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform1i(varLocation, slot)
    }

    fun uploadBool(varName: String, bool: Boolean) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform1i(varLocation, if (bool) 1 else 0)
    }

    fun uploadInt(varName: String, value: Int) {
        val varLocation = glGetUniformLocation(shaderProgramID, varName)
        glUniform1i(varLocation, value)
    }

    companion object {
        var shaderInUse: Shader? = null
    }
}

/** Returns shader program id */
private fun loadShaderProgram(vertex: Path, fragment: Path): Int {
    val vertexID = loadShader(vertex, GL_VERTEX_SHADER)
    val fragmentID = loadShader(fragment, GL_FRAGMENT_SHADER)

    // Link shaders and check for errors
    val shaderProgramID = glCreateProgram()
    glAttachShader(shaderProgramID, vertexID)
    glAttachShader(shaderProgramID, fragmentID)
    glLinkProgram(shaderProgramID)

    // Check for linking errors
    val success = glGetProgrami(shaderProgramID, GL_LINK_STATUS)
    if (success == GL_FALSE) {
        val len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH)
        throw RuntimeException(
            "Shader linking failed: '${vertex}', '${fragment}'\n\t" +
                glGetShaderInfoLog(shaderProgramID, len)
        )
    }
    return shaderProgramID
}

private fun loadShader(file: Path, type: Int): Int {
    // Read shader source from file
    val source = String(Files.readAllBytes(file))

    // First load and compile the shader
    val shaderId = glCreateShader(type)

    // Pass the shader source to the GPU
    glShaderSource(shaderId, source)
    glCompileShader(shaderId)

    // Check for errors in compilation
    val success = glGetShaderi(shaderId, GL_COMPILE_STATUS)
    if (success == GL_FALSE) {
        val len = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH)
        throw RuntimeException(
            "Shader compilation failed: '$file'\n\t" +
                glGetShaderInfoLog(shaderId, len)
        )
    }
    return shaderId
}