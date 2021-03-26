package vivid.money.elmslie.plugin.presentation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.vfs.VirtualFile
import vivid.money.elmslie.plugin.core.ext.runWriteAction
import vivid.money.elmslie.plugin.presentation.controller.PresentationLayerController
import vivid.money.elmslie.plugin.presentation.views.CreatePresentationLayerDialog

private const val ERROR_SOMETHING_WRONG = "Something goes wrong during generation: "

class PresentationLayerAction : AnAction() {

    private val regex = Regex("(java|kotlin)")

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            val target = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE) as VirtualFile

            val dialog = CreatePresentationLayerDialog { model ->
                project.runWriteAction {
                    try {
                        project.getComponent(PresentationLayerController::class.java).generate(model, target)
                    } catch (error: Exception) {
                        val errorDialog = DialogBuilder()
                        errorDialog.setErrorText(ERROR_SOMETHING_WRONG + error.message)
                        errorDialog.show()
                    }
                }
            }
            dialog.show()
        }
    }

    override fun update(e: AnActionEvent) {
        val selectedFile = e.dataContext.getData(PlatformDataKeys.VIRTUAL_FILE) as VirtualFile

        val isDirectory = selectedFile.isDirectory
        val isFeatureModule = selectedFile.path.contains(regex)
        e.presentation.isVisible = isDirectory && isFeatureModule
        e.presentation.isEnabled = isDirectory && isFeatureModule
    }
}