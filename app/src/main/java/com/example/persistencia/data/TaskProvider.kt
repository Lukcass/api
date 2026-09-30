package com.example.persistencia.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.sqlite.db.SupportSQLiteQueryBuilder

class TaskProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.persistencia.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/tasks")
        private const val TASKS = 1
        private const val TASK_ID = 2
        private const val TABLA = "tasks" // cámbialo por el tableName de tu @Entity

        private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "tasks", TASKS)
            addURI(AUTHORITY, "tasks/#", TASK_ID)
        }
    }

    private val db
        get() = TaskDatabase.getDatabase(context!!).openHelper.writableDatabase

    override fun onCreate() = true

    override fun getType(uri: Uri) = when (matcher.match(uri)) {
        TASKS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.tasks"
        TASK_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.tasks"
        else -> null
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor {
        val (sel, args) = filtroPorUri(uri, selection, selectionArgs)
        val q = SupportSQLiteQueryBuilder.builder(TABLA)
            .columns(projection).selection(sel, args).orderBy(sortOrder).create()
        return db.query(q).also { it.setNotificationUri(context!!.contentResolver, uri) }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (matcher.match(uri) != TASKS) throw IllegalArgumentException("URI no válida: $uri")
        val id = db.insert(TABLA, SQLiteDatabase.CONFLICT_ABORT, values!!)
        if (id == -1L) return null
        context!!.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val (sel, args) = filtroPorUri(uri, selection, selectionArgs)
        val n = db.update(TABLA, SQLiteDatabase.CONFLICT_ABORT, values!!, sel, args)
        if (n > 0) context!!.contentResolver.notifyChange(uri, null)
        return n
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val (sel, args) = filtroPorUri(uri, selection, selectionArgs)
        val n = db.delete(TABLA, sel, args)
        if (n > 0) context!!.contentResolver.notifyChange(uri, null)
        return n
    }

    private fun filtroPorUri(uri: Uri, sel: String?, args: Array<String>?): Pair<String?, Array<String>?> =
        when (matcher.match(uri)) {
            TASKS -> sel to args
            TASK_ID -> "id = ?" + (if (sel.isNullOrEmpty()) "" else " AND ($sel)") to
                    arrayOf(uri.lastPathSegment!!) + (args ?: emptyArray())
            else -> throw IllegalArgumentException("URI no válida: $uri")
        }
}