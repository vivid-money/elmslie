package vivid.money.elmslie.android.logger

import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.net.Uri
import vivid.money.elmslie.core.config.ElmslieConfig

class DefaultInitContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val isDebug = 0 != context!!.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        if (isDebug) ElmslieConfig.defaultDebugLogger() else ElmslieConfig.defaultReleaseLogger()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor = error("not allowed")

    override fun getType(uri: Uri): String = error("not allowed")

    override fun insert(uri: Uri, values: ContentValues?): Uri = error("not allowed")

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = error("not allowed")

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = error("not allowed")
}