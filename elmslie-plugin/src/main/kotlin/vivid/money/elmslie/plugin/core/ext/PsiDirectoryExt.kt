package vivid.money.elmslie.plugin.core.ext

import com.intellij.psi.PsiDirectory

private const val DOT = "."

fun PsiDirectory.formPackageName(): String {
    val result = StringBuilder(this.name)
    var parent: PsiDirectory = this.parent ?: return result.toString()

    while (parent.name != "java" && parent.name != "kotlin") {
        result.insert(0, parent.name)
        result.insert(parent.name.length, DOT)

        parent = parent.parent ?: return result.toString()
    }

    return result.toString()
}