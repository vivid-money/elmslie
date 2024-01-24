package money.vivid.elmslie.android.logger

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri

internal abstract class EmptyContentProvider : ContentProvider() {

    open fun init() {}

    override fun onCreate(): Boolean {
        init()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ) = error("not allowed")

    override fun getType(uri: Uri) = error("not allowed")

    override fun insert(uri: Uri, values: ContentValues?) = error("not allowed")

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = error("not allowed")

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ) = error("not allowed")
}
