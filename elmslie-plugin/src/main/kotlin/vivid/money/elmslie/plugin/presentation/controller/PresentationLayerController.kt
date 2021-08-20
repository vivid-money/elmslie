package vivid.money.elmslie.plugin.presentation.controller

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory
import vivid.money.elmslie.plugin.core.ext.formPackageName
import vivid.money.elmslie.plugin.core.templates.FileTemplateData
import vivid.money.elmslie.plugin.core.templates.ModuleFileFactory
import vivid.money.elmslie.plugin.core.templates.TemplatesConstants
import vivid.money.elmslie.plugin.presentation.model.PresentationLayerModel

class PresentationLayerController(private val project: Project) {

    fun generate(model: PresentationLayerModel, target: VirtualFile) {
        val targetPsiDirectory = target.toPsiDirectory(project) ?: return
        val presentationPsiFolder = targetPsiDirectory.findSubdirectory("presentation")
            ?: targetPsiDirectory.createSubdirectory("presentation")

        val templates = getTemplates(model, presentationPsiFolder)
        val moduleFileFactory = ModuleFileFactory(project)

        val properties = mutableMapOf<String, Any>(
            "presentation_package_name" to presentationPsiFolder.formPackageName(),
            "domain_name" to model.className,
            "split_events" to model.addSplittingEvents
        )

        templates.forEach { templateData ->
            val psiFile = moduleFileFactory.createFromTemplate(templateData, properties)
            templateData.outputFilePsiDirectory?.let { directory ->
                directory.findFile(psiFile.name) ?: directory.add(psiFile)
            }
        }
    }

    private fun getTemplates(
        model: PresentationLayerModel,
        presentationPsiDirectory: PsiDirectory
    ): MutableList<FileTemplateData> {
        val templates = mutableListOf<FileTemplateData>()

        FileTemplateData(
            templateFileName = TemplatesConstants.PRESENTATION_MODELS_CLASS_TEMPLATE,
            outputFileName = "${model.className}${TemplatesConstants.PRESENTATION_MODELS_CLASS_OUTPUT_SUFFIX}",
            outputFileType = KotlinFileType.INSTANCE,
            outputFilePsiDirectory = presentationPsiDirectory
        ).let(templates::add)

        FileTemplateData(
            templateFileName = TemplatesConstants.PRESENTATION_ACTOR_CLASS_TEMPLATE,
            outputFileName = "${model.className}${TemplatesConstants.PRESENTATION_ACTOR_CLASS_OUTPUT_SUFFIX}",
            outputFileType = KotlinFileType.INSTANCE,
            outputFilePsiDirectory = presentationPsiDirectory
        ).let(templates::add)

        FileTemplateData(
            templateFileName = TemplatesConstants.PRESENTATION_REDUCER_CLASS_TEMPLATE,
            outputFileName = "${model.className}${TemplatesConstants.PRESENTATION_REDUCER_CLASS_OUTPUT_SUFFIX}",
            outputFileType = KotlinFileType.INSTANCE,
            outputFilePsiDirectory = presentationPsiDirectory
        ).let(templates::add)

        return templates
    }
}
