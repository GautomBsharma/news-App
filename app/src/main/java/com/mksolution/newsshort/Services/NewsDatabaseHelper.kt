package com.mksolution.newsshort.Services

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mksolution.newsshort.Models.News

class NewsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "newsDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "news"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_IMAGE_URL = "imageUrl"
        private const val COLUMN_UPLOAD_TIME = "uploadTime"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT,
                $COLUMN_IMAGE_URL TEXT,
                $COLUMN_UPLOAD_TIME INTEGER
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert a list of news items into the database
    fun saveNews(newsList: List<News>) {
        val db = writableDatabase
        db.beginTransaction()
        try {

            val limitedNewsList = newsList.take(3)
            // Clear the old data before inserting new data
            db.delete(TABLE_NAME, null, null)

            val contentValues = ContentValues()
            for (news in limitedNewsList) {
                contentValues.put(COLUMN_TITLE, news.newsTitle)
                contentValues.put(COLUMN_CONTENT, news.news)
                contentValues.put(COLUMN_IMAGE_URL, news.newsUrl)
                contentValues.put(COLUMN_UPLOAD_TIME, news.newsTime)
                db.insert(TABLE_NAME, null, contentValues)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Retrieve all news items from the database
    fun getSavedNews(): ArrayList<News> {
        val newsList = ArrayList<News>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TITLE, COLUMN_CONTENT, COLUMN_IMAGE_URL, COLUMN_UPLOAD_TIME),
            null, null, null, null, "$COLUMN_UPLOAD_TIME DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                val uploadTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_TIME))

                newsList.add(
                    News(
                        newsUrl = imageUrl,
                        newsId = "", // Default value or retrieved value if you have it
                        newsTag = "", // Default value or retrieved value if you have it
                        newsTitle = title,
                        news = content,
                        newsTime = uploadTime
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return newsList
    }
}
