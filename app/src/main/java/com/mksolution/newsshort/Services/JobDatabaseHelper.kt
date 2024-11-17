package com.mksolution.newsshort.Services

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mksolution.newsshort.Models.Job

class JobDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "jobDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "jobs"
        private const val COLUMN_ID = "id"
        private const val COLUMN_JOB_ID = "jobId"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_PROVIDER = "provider"
        private const val COLUMN_PROVIDER_LINK = "providerLink"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_EXPERIENCE = "experience"
        private const val COLUMN_SALARY = "salary"
        private const val COLUMN_DEADLINE = "deadline"
        private const val COLUMN_SLOAT = "slot"
        private const val COLUMN_ABOUT = "about"
        private const val COLUMN_IMAGE_URL = "imageUrl"
        private const val COLUMN_UPLOAD_TIME = "uploadTime"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_JOB_ID TEXT,
                $COLUMN_TITLE TEXT,
                $COLUMN_PROVIDER TEXT,
                $COLUMN_PROVIDER_LINK TEXT,
                $COLUMN_LOCATION TEXT,
                $COLUMN_EXPERIENCE TEXT,
                $COLUMN_SALARY TEXT,
                $COLUMN_DEADLINE TEXT,
                $COLUMN_SLOAT TEXT,
                $COLUMN_ABOUT TEXT,
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

    // Insert a list of job items into the database
    fun saveJobs(jobList: List<Job>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val limitedJobList = jobList.take(3) // Limit to 3 jobs
            db.delete(TABLE_NAME, null, null) // Clear old data

            val contentValues = ContentValues()
            for (job in limitedJobList) {
                contentValues.put(COLUMN_JOB_ID, job.jobId)
                contentValues.put(COLUMN_TITLE, job.jobTitle)
                contentValues.put(COLUMN_PROVIDER, job.jobProvider)
                contentValues.put(COLUMN_PROVIDER_LINK, job.jobProviderLink)
                contentValues.put(COLUMN_LOCATION, job.jobLocation)
                contentValues.put(COLUMN_EXPERIENCE, job.jobExperiance)
                contentValues.put(COLUMN_SALARY, job.jobSalary)
                contentValues.put(COLUMN_DEADLINE, job.jobDeadline)
                contentValues.put(COLUMN_SLOAT, job.jobSloat)
                contentValues.put(COLUMN_ABOUT, job.jobAbout)
                contentValues.put(COLUMN_IMAGE_URL, job.jobUrl)
                contentValues.put(COLUMN_UPLOAD_TIME, job.newsTime)
                db.insert(TABLE_NAME, null, contentValues)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Retrieve all job items from the database
    fun getSavedJobs(): ArrayList<Job> {
        val jobList = ArrayList<Job>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_JOB_ID, COLUMN_TITLE, COLUMN_PROVIDER, COLUMN_PROVIDER_LINK, COLUMN_LOCATION,
                COLUMN_EXPERIENCE, COLUMN_SALARY, COLUMN_DEADLINE, COLUMN_SLOAT,
                COLUMN_ABOUT, COLUMN_IMAGE_URL, COLUMN_UPLOAD_TIME
            ),
            null, null, null, null, "$COLUMN_UPLOAD_TIME DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val jobId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JOB_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val provider = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVIDER))
                val providerLink = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVIDER_LINK))
                val location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
                val experience = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPERIENCE))
                val salary = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALARY))
                val deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
                val slot = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SLOAT))
                val about = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ABOUT))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                val uploadTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_TIME))

                jobList.add(
                    Job(
                        jobUrl = imageUrl,
                        jobId = jobId,
                        jobDeadline = deadline,
                        jobSloat = slot,
                        jobProvider = provider,
                        jobProviderLink = providerLink,
                        jobLocation = location,
                        jobExperiance = experience,
                        jobTitle = title,
                        jobAbout = about,
                        jobSalary = salary,
                        newsTime = uploadTime
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return jobList
    }
}

