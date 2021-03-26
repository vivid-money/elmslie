package vivid.money.elmslie.plugin.core.ext

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project

fun Project.runWriteAction(action: () -> Unit) {
    ApplicationManager.getApplication().runWriteAction {
        CommandProcessor.getInstance().executeCommand(
            this,
            { action() },
            "",
            null
        )
    }
}