package com.romandevyatov.bestfinance

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.romandevyatov.bestfinance.models.IncomeGroup

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASTE_VERSION) {
    companion object {
        private val DATABASTE_VERSION = 1
        private val DATABASE_NAME = "BestFinanceDatabase"
        private val TABLE_INCOME_GROUPS = "IncomeGroupTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_INCOME_GROUP_TABLE_QUERY = ("CREATE TABLE " + TABLE_INCOME_GROUPS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT" + ")")
        db?.execSQL(CREATE_INCOME_GROUP_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME_GROUPS)
        onCreate(db)
    }

    fun addIncomeGroup(incomeGroup: IncomeGroup) : Long {
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, incomeGroup.name)

        val db = this.writableDatabase
        val success = db.insert(TABLE_INCOME_GROUPS, null, contentValues)
        db.close()

        return success
    }

    fun getAllIncomeGroups() : List<IncomeGroup> {
        val incomeGroupArrayList: ArrayList<IncomeGroup> = ArrayList<IncomeGroup>()
        val SELECT_ALL_INCOME_GROUP_QUERY = "SELECT * FROM $TABLE_INCOME_GROUPS"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(SELECT_ALL_INCOME_GROUP_QUERY, null)
        } catch (e: SQLiteException) {
            throw SQLiteException("Error running income group select all", e)
        }

        var incomeGroupId: Int
        var incomeGroupName: String

        if (cursor.moveToFirst()) {
            do {
                incomeGroupId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                incomeGroupName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val incomeGroup = IncomeGroup(id = incomeGroupId, name = incomeGroupName)
                incomeGroupArrayList.add(incomeGroup)
            } while (cursor.moveToNext())
        }

        return incomeGroupArrayList
    }

    fun updateIncomeGroup(incomeGroup: IncomeGroup) : Int {
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, incomeGroup.id)
        contentValues.put(KEY_NAME, incomeGroup.name)

        val db = this.writableDatabase
        val success = db.update(TABLE_INCOME_GROUPS, contentValues,"id="+incomeGroup.id,null)
        db.close()

        return success
    }

    fun deleteIncomeGroup(incomeGroup: IncomeGroup) : Int {
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, incomeGroup.id)

        val db = this.writableDatabase
        val success = db.delete(TABLE_INCOME_GROUPS,"id="+incomeGroup.id,null)
        db.close()

        return success
    }

}