package vivid.money.elmslie.plugin.presentation.views

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import org.jdesktop.swingx.VerticalLayout
import vivid.money.elmslie.plugin.presentation.model.PresentationLayerModel
import javax.swing.*

class CreatePresentationLayerDialog(
    private var okButtonClickListener: ((PresentationLayerModel) -> Unit)? = null
) : DialogWrapper(true) {

    private val classNameTextField = JTextField()
    private val addSplittingEventsCheckBox = JCheckBox()

    init {
        super.init()
        title = "Generate store"
    }

    override fun createCenterPanel(): JComponent? {
        val dialogPanel = JPanel(VerticalLayout())

        val nameLabel = JLabel()
        nameLabel.text = "Type class name"
        dialogPanel.add(nameLabel)

        dialogPanel.add(classNameTextField)

        addSplittingEventsCheckBox.text = "Split Events to Ui/Internal events"
        dialogPanel.add(addSplittingEventsCheckBox)

        return dialogPanel
    }

    override fun doOKAction() {
        val model = PresentationLayerModel(
            className = classNameTextField.text,
            addSplittingEvents = addSplittingEventsCheckBox.isSelected
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
