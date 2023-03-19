package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.render.msaa.FrameBufferMSAA
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.util.LogMessage
import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags

class MainGui(val app: EditorApp) : GuiBase() {
    @PublishedApi internal val vp = Viewport(0, 0, 0, 0)
    @PublishedApi internal var mainWindowFbo = FrameBuffer()
    @PublishedApi internal val fpsCounter = FpsCounter()
    @PublishedApi internal val nodeProperties = GuiObjectProperties(app, this)
    @PublishedApi internal val nodeTree = GuiNodeTree(app, this)
    @PublishedApi internal val voxelTree = GuiVoxelTree(app, this)
    @PublishedApi internal val history = GuiHistory(app, this)
    @PublishedApi internal val blueprintEditor = GuiBlueprintEditor(app, this)
    @PublishedApi internal val styleEditor = GuiStyleEditor(app)
    @PublishedApi internal val statusBar = GuiStatusBar(app)

    @PublishedApi internal val layout = DockLayout(HorizontalSplit(
        rightSize = 250,
        left = HorizontalSplit(
            leftSize = 250,
            left = Window("Style editor"),
            right = VerticalSplit(
                bottomRatio = 0.5f,
                top = WindowGroup(
                    Window("###scene_window"),
                ),
                bottom = Window("###blueprint_window")
            ),
        ),
        right = VerticalSplit(
            bottomRatio = 0.5f,
            bottom = Window("Properties"),
            top = VerticalSplit(
                bottomRatio = 0.3f,
                top = Window("Node tree"),
                bottom = WindowGroup(
                    Window("Voxel tree"),
                    Window("History"),
                )
            ),
        ),
    ))

    fun init(windowHandle: Long, viewport: Viewport, samplesMSAA: Int = 0) {
        super.init(windowHandle)
        vp.set(viewport)
        if (samplesMSAA > 0) mainWindowFbo = FrameBufferMSAA(samplesMSAA)
        mainWindowFbo.init(viewport)
        blueprintEditor.init()
        styleEditor.init()
    }

    inline fun render(crossinline renderMainWindow: (Viewport) -> Unit) = runFrame {
//        debugTexture((app as EditorAppImpl).scene.hitTester.voxelsFbo.texture)
        logWindow()
        fpsCounter.run()
        mainMenu()
        dockspace(layout, statusBar.height)
        panel("Style editor") {
            styleEditor.render()
        }
        sceneWindow { vp ->
            renderMainWindow(vp)
            if (app.state.DEBUG) overlay("debug_overlay", Corner.TOP_RIGHT,
                padding = 0f) {
                ImGui.text("Tool: ")
                ImGui.sameLine()
                ImGui.text(app.state.currentTool.toolName)

                app.state.overlayText.values.forEach { ImGui.text(it) }

                ImGui.text(fpsCounter.fpsRoundStr)
                ImGui.sameLine()
                ImGui.text(" fps")
            }
            overlay("camera_controls", Corner.BOTTOM_RIGHT,
                innerPadding=0f, bgAlpha=1f) {
                bigIconButton(FontAwesomeIcons.Compress, "Recenter camera") {
                    app.centerCamera()
                }
            }
            overlay("left_toolbar", Corner.TOP_LEFT,
                padding = 0f, bgAlpha=1f) {
                toolButton(Tool.SELECT)
                toolButton(Tool.ADD_NODE)
                toolButton(Tool.MOVE)
                toolButton(Tool.RESIZE)
            }
            overlay("top_toolbar", Corner.TOP_LEFT,
                padding = 0f, bgAlpha=0f, offsetX = 32f) {
                topPanel()
            }
        }
        panel("Node tree", hasPadding = false) {
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0f, 0f)
            childWindow("tree", toolbarHeight) { nodeTree.render() }
            childToolbar("footer") {
                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4f, 4f)
                val width = (ImGui.getContentRegionAvailX() - 2*4 - ImGui.getFrameHeight()) / 2
                accentButton("Build voxels", width = width) {
                    app.buildVoxels()
                }
                ImGui.sameLine()
                accentButton("Generate nodes", width = width) {
                    app.generateNodes()
                }
                ImGui.sameLine()
                iconButton(FontAwesomeIcons.Cog, accent = true, font = fontMediumIcons)
                ImGui.popStyleVar()
            }
            ImGui.popStyleVar()
        }
        panel("History", hasPadding = false) {
            history.render()
        }
        panel("Voxel tree", hasPadding = false) {
            voxelTree.render()
        }
        panel("Properties") {
            nodeProperties.render()
        }
        panel(blueprintWindowTitle, hasPadding = false) {
            blueprintEditor.render()
        }
        statusBar.render()
    }

    @PublishedApi
    internal inline fun sceneWindow(
        crossinline renderWindow: (Viewport) -> Unit,
    ) {
        ImGui.setNextWindowClass(
            ImGuiWindowClass().apply {
                dockNodeFlagsOverrideSet = 0 or
                    ImGuiDockNodeFlags.NoDocking
            }
        )
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        val mainWindowFlags = 0 or
            ImGuiWindowFlags.NoMove or
            ImGuiWindowFlags.NoScrollbar
        if (ImGui.begin(sceneWindowTitle, mainWindowFlags)) {
            val pos = ImGui.getWindowPos()
            val vMin = ImGui.getWindowContentRegionMin()
            val vMax = ImGui.getWindowContentRegionMax()
            vp.set(
                vMin.x + pos.x,
                vMin.y + pos.y,
                vMax.x - vMin.x,
                vMax.y - vMin.y
            )
            mainWindowFbo.setViewport(vp)
            mainWindowFbo.render {
                renderWindow(vp)
                ImGui.image(mainWindowFbo.texture.texID,
                    vMax.x - vMin.x, vMax.y - vMin.y, 0f, 1f, 1f, 0f)
            }
            app.focusMainWindow(ImGui.isWindowFocused())
            app.hoverMainWindow(ImGui.isWindowHovered())
        }
        ImGui.end()
        ImGui.popStyleVar(3)
    }

    @PublishedApi
    internal inline fun panel(
        title: String,
        hasPadding: Boolean = true,
        crossinline renderWindow: () -> Unit
    ) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        if (!hasPadding)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        if (ImGui.begin(title, 0)) {
            renderWindow()
        }
        ImGui.end()
        ImGui.popStyleVar(2)
        if (!hasPadding) ImGui.popStyleVar()
    }

    @PublishedApi
    internal fun logWindow() {
        for (e in app.logs) {
            when (e) {
                is LogMessage.Warning -> {
                    ImGui.textWrapped(e.msg)
                }
                is LogMessage.Error -> {
                    if (ImGui.treeNode(e.msg)) {
                        for (stackFrame in e.ex.stackTrace) {
                            ImGui.text(stackFrame.toString())
                        }
                        ImGui.treePop()
                    }
                }
            }
        }
    }

    @PublishedApi
    internal fun debugTexture(texture: Texture) {
        ImGui.image(
            texture.texID,
            texture.width.toFloat(), texture.height.toFloat(),
            0f, 1f, 1f, 0f)
    }

    @PublishedApi
    internal val sceneWindowTitle: String get() = app.state.run {
        if (lastSavedAction == history.pastItems.lastOrNull()) "Scene###scene_window"
        else "Scene *###scene_window"
    }

    @PublishedApi
    internal val blueprintWindowTitle: String get() = app.state.run {
        selectedBlueprint?.let {
            "Blueprint: ${it.name}###blueprint_window"
        } ?: "Blueprint###blueprint_window"
    }
}