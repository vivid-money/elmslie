package vivid.money.elmslie.plugin.presentation.views

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import org.jdesktop.swingx.VerticalLayout
import vivid.money.elmslie.plugin.presentation.model.PresentationLayerModel
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class CreatePresentationLayerDialog(
    private var okButtonClickListener: ((PresentationLayerModel) -> Unit)? = null
) : DialogWrapper(true) {

    private val classNameTextField = JTextField()

    init {
        super.init()
        title = "Create presentation layer"
    }

    override fun createCenterPanel(): JComponent? {
        val dialogPanel = JPanel(VerticalLayout())

        val nameLabel = JLabel()
        nameLabel.text = "Type class name"
        dialogPanel.add(nameLabel)

        dialogPanel.add(classNameTextField)

        return dialogPanel
    }

    override fun doOKAction() {
        val model = PresentationLayerModel(
            className = classNameTextField.text
        )
        okButtonClickListener?.invoke(model)
        super.doOKAction()
    }

    override fun doValidate(): ValidationInfo? =
        ValidationInfo("Field shouldn't be empty")
            .takeIf { classNameTextField.text.isNullOrBlank() }

    override fun dispose() {
        okButtonClickListener = null
        super.dispose()
    }
}