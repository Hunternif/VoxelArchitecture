package hunternif.voxarch.editor.render.msaa

import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.Viewport
import org.lwjgl.opengl.GL32.*

class FrameBufferMSAA(var samples: Int = 4) : FrameBuffer() {
    @PublishedApi
    internal val vp = Viewport(0, 0, 0, 0)
    var fboMSAAID: Int = 0 // FBO for MSAA, will read from here
    var fboDrawID: Int = 0 // FBO for drawing
    private val textureMSAA = TextureMSAA("generated MSAA", samples)

    override fun init(viewport: Viewport) {
        setViewport(viewport)
        texture.generate(vp.width, vp.height)
        textureMSAA.generate(vp.width, vp.height)

        fboDrawID = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fboDrawID)
        glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            texture.texID,
            0
        )

        fboMSAAID = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fboMSAAID)
        glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D_MULTISAMPLE,
            textureMSAA.texID,
            0
        )

        rboID = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, rboID)
        glRenderbufferStorageMultisample(
            GL_RENDERBUFFER,
            samples,
            GL_DEPTH_COMPONENT32,
            vp.width,
            vp.height
        )
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID)

        val fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw AssertionError("Could not create FBO: $fboStatus")
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun setViewport(viewport: Viewport) {
        if (vp.width != viewport.width || vp.height != viewport.height) {
            texture.resize(viewport.width, viewport.height)
            textureMSAA.resize(viewport.width, viewport.height)
            glBindRenderbuffer(GL_RENDERBUFFER, rboID)
            glRenderbufferStorageMultisample(
                GL_RENDERBUFFER,
                samples,
                GL_DEPTH_COMPONENT32,
                viewport.width,
                viewport.height
            )
        }
        vp.set(viewport)
    }

    override fun render(renderCall: () -> Unit) {
        glBindFramebuffer(GL_FRAMEBUFFER, fboMSAAID)
        renderCall()
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboMSAAID)
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboDrawID)
        glBlitFramebuffer(
            0, 0, vp.width, vp.height,
            0, 0, vp.width, vp.height,
            GL_COLOR_BUFFER_BIT, GL_NEAREST
        )
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0)
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
    }
}