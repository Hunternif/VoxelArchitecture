package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.render.FrameBuffer
import hunternif.voxarch.editor.render.Texture
import hunternif.voxarch.editor.render.msaa.FrameBufferMSAA
import hunternif.voxarch.editor.render.Viewport
import hunternif.voxarch.editor.scene.InputController
import imgui.ImGui
import imgui.ImGuiWindowClass
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImBoolean

class MainGui(val app: EditorApp) : GuiBase() {
    @PublishedApi internal val vp = Viewport(0, 0, 0, 0)
    @PublishedApi internal var mainWindowFbo = FrameBuffer()
    @PublishedApi internal val fpsCounter = FpsCounter()
    @PublishedApi internal val properties = GuiMultiObjectProperties(app, this)
    @PublishedApi internal val nodeTree = GuiNodeTree(app, this)
    @PublishedApi internal val voxelTree = GuiVoxelTree(app, this)
    @PublishedApi internal val history = GuiHistory(app, this)
    @PublishedApi internal val blueprintLibrary = GuiBlueprintLibrary(app, this)
    @PublishedApi internal val blueprintEditor = GuiBlueprintEditor(app, this)
    @PublishedApi internal val styleEditor = GuiStyleEditor(app)
    @PublishedApi internal val log = GuiLog()
    @PublishedApi internal val statusBar = GuiStatusBar(app, log)

    @PublishedApi internal val showStyleEditor = ImBoolean(true)
    @PublishedApi internal val showBlueprintLibrary = ImBoolean(true)
    @PublishedApi internal val showBlueprintEditor = ImBoolean(true)
    @PublishedApi internal val showNodeTree = ImBoolean(true)
    @PublishedApi internal val showVoxelTree = ImBoolean(true)
    @PublishedApi internal val showHistory = ImBoolean(true)
    @PublishedApi internal val showProperties = ImBoolean(true)
    @PublishedApi internal val showLogs = ImBoolean(false)

    @PublishedApi internal val layout = DockLayout(HorizontalSplit(
        rightSize = 250,
        left = VerticalSplit(
            bottomSize = 100,
            top = HorizontalSplit(
                leftSize = 250,
                left = VerticalSplit(
                    bottomRatio = 0.3f,
                    top = Window("Style editor"),
                    bottom = Window("Blueprint library"),
                ),
                right = VerticalSplit(
                    bottomSize = 350,
                    top = WindowGroup(
                        Window("###scene_window"),
                    ),
                    bottom = Window("###blueprint_window")
                ),
            ),
            bottom = Window("Logs"),
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

    /** Called 1 time when the app starts. */
    fun init(
        windowHandle: Long,
        viewport: Viewport,
        inputController: InputController,
        samplesMSAA: Int = 0,
    ) {
        super.init(windowHandle)
        vp.set(viewport)
        if (samplesMSAA > 0) mainWindowFbo = FrameBufferMSAA(samplesMSAA)
        mainWindowFbo.init(viewport)
        blueprintEditor.init()
        styleEditor.init()
        inputController.addListener(styleEditor)
    }

    /** Called when a new AppState is created */
    fun initState() {
        nodeTree.initState()
        voxelTree.initState()
    }

    inline fun render(crossinline renderMainWindow: (Viewport) -> Unit) = runFrame {
//        debugTexture((app as EditorAppImpl).scene.hitTester.voxelsFbo.texture)
        fpsCounter.run()
        mainMenu()
        dockspace(layout, statusBar.height)
        panel("Style editor", showStyleEditor) {
            styleEditor.render()
        }
        panel("Blueprint library", showBlueprintLibrary) {
            blueprintLibrary.render()
        }
        sceneWindow { vp ->
            renderMainWindow(vp)
            if (app.state.DEBUG) overlay("debug_overlay", Corner.TOP_RIGHT,
                padding = 0f) {
//                ImGui.text("isAnyItemActive: ${ImGui.isAnyItemActive()}")
//                ImGui.text("isAnyItemFocused: ${ImGui.isAnyItemFocused()}")
//                ImGui.text("isAnyItemHovered: ${ImGui.isAnyItemHovered()}")
//                ImGui.text("isMainWindowFocused: ${app.state.isMainWindowFocused}")
//                ImGui.text("isMainWindowHovered: ${app.state.isMainWindowHovered}")
//                ImGui.text("isTextEditorActive: ${app.state.isTextEditorActive}")

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
        panel("Node tree", showNodeTree, hasPadding = false) {
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
        panel("History", showHistory, hasPadding = false) {
            history.render()
        }
        panel("Voxel tree", showVoxelTree, hasPadding = false) {
            voxelTree.render()
        }
        panel("Properties", showProperties) {
            properties.render()
        }
        panel(blueprintWindowTitle, showBlueprintEditor, hasPadding = false) {
            blueprintEditor.render()
        }
        panel("Logs", showLogs, hasPadding = false) {
            log.render()
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

    /**
     * @param openFlag if not null, a close 'X' button will appear.
     *                 Clicking it will set the flag to false.
     */
    @PublishedApi
    internal inline fun panel(
        title: String,
        openFlag: ImBoolean? = null,
        hasPadding: Boolean = true,
        crossinline renderWindow: () -> Unit
    ) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
        if (!hasPadding)
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        val flags = 0
        if (openFlag != null) {
            if (openFlag.get()) {
                if (ImGui.begin(title, openFlag, flags)) {
                    renderWindow()
                }
                ImGui.end()
            }
        } else {
            if (ImGui.begin(title, flags)) {
                renderWindow()
            }
            ImGui.end()
        }
        ImGui.popStyleVar(2)
        if (!hasPadding) ImGui.popStyleVar()
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