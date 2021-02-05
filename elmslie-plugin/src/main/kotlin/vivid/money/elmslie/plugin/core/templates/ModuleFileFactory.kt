package vivid.money.elmslie.plugin.core.templates

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.StringWriter

class ModuleFileFactory(private val project: Project) {

    private val psiFileFactory by lazy {
        PsiFileFactory.getInstance(project)
    }

    private val freeMarkerConfig by lazy {
        Configuration(Configuration.VERSION_2_3_27).apply {
            setClassForTemplateLoading(javaClass, "/templates")

            defaultEncoding = Charsets.UTF_8.name()
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
        }
    }


    fun createFromTemplate(templateData: FileTemplateData, templateProperties: Map<String, Any>): PsiFile {
        val template = freeMarkerConfig.getTemplate(templateData.templateFileName)

        val text = StringWriter().use { writer ->
            template.process(templateProperties, writer)
            writer.buffer.toString()
        }

        return psiFileFactory.createFileFromText(templateData.outputFileName, templateData.outputFileType, text)
    }
}