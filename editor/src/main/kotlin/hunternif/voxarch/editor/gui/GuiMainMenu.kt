package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.centerCamera
import hunternif.voxarch.editor.actions.importVoxFile
import imgui.ImGui
import imgui.flag.ImGuiStyleVar
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog.*
import java.lang.Exception
import java.nio.file.Paths

fun MainGui.mainMenu() {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    if (ImGui.beginMainMenuBar()) {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Import VOX file...")) {
                openFileDialog()
            }
            ImGui.endMenu()
        }
        ImGui.endMainMenuBar()
    }
    ImGui.popStyleVar(1)
}

//TODO: use a separate thread
private fun MainGui.openFileDialog() {
    val outPath = MemoryUtil.memAllocPointer(1)
    try {
        if (NFD_OKAY == NFD_OpenDialog("vox", null, outPath)) {
            val pathStr = outPath.getStringUTF8(0)
            val path = Paths.get(pathStr)
            app.importVoxFile(path)
            app.centerCamera()
        }
    } catch (e: Exception) {
        ImGui.text(e.toString())
    } finally {
        MemoryUtil.memFree(outPath)
    }
}