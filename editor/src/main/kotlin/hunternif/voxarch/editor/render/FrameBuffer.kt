package hunternif.voxarch.editor.render

import org.lwjgl.opengl.GL32.*

open class FrameBuffer {
    private val vp = Viewport(0, 0, 0, 0)
    var fboID: Int = 0
    var rboID: Int = 0
    val texture = Texture("generated")

    open fun init(viewport: Viewport) {
        setViewport(viewport)
        texture.generate(vp.width, vp.height)

        fboID = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)
        glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            texture.texID,
            0
        )

        rboID = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, rboID)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, vp.width, vp.height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID)

        val fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw AssertionError("Could not create FBO: $fboStatus")
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    open fun setViewport(viewport: Viewport) {
        if (vp.width != viewport.width || vp.height != viewport.height) {
            texture.resize(viewport.width, viewport.height)
            glBindRenderbuffer(GL_RENDERBUFFER, rboID)
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, viewport.width, viewport.height)
        }
        vp.set(viewport)
    }

    @PublishedApi
    internal open fun startFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)
    }

    @PublishedApi
    internal open fun endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    inline fun render(crossinline renderCall: () -> Unit) {
        startFrame()
        renderCall()
        endFrame()
    }
}