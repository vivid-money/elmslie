package vivid.money.elmslie.plugin.core.templates

import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiDirectory

data class FileTemplateData(
    val templateFileName: String,
    val outputFileName: String,
    val outputFileType: FileType,
    val outputFilePsiDirectory: PsiDirectory?
)