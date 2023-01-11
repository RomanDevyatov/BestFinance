package com.romandevyatov.bestfinance

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.romandevyatov.bestfinance.models.ExpenseGroup

class ExpenseGroupDatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASTE_VERSION) {
    companion object {
        private val DATABASTE_VERSION = 1
        private val DATABASE_NAME = "BestFinanceDatabase"
        private val TABLE_INCOME_GROUPS = "ExpenseGroupTable"
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

    fun addExpenseGroup(expenseGroup: ExpenseGroup) : Long {
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, expenseGroup.name)

        val db = this.writableDatabase
        val success = db.insert(TABLE_INCOME_GROUPS, null, contentValues)
        db.close()

        return success
    }

    fun getAllExpenseGroups() : List<ExpenseGroup> {
        val expenseGroupArrayList: ArrayList<ExpenseGroup> = ArrayList<ExpenseGroup>()
        val SELECT_ALL_INCOME_GROUP_QUERY = "SELECT * FROM $TABLE_INCOME_GROUPS"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(SELECT_ALL_INCOME_GROUP_QUERY, null)
        } catch (e: SQLiteException) {
            throw SQLiteException("Error running expense group select all", e)
        }

        var expenseGroupId: Int
        var expenseGroupName: String

        if (cursor.moveToFirst()) {
            do {
                expenseGroupId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                expenseGroupName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val expenseGroup = ExpenseGroup(id = expenseGroupId, name = expenseGroupName)
                expenseGroupArrayList.add(expenseGroup)
            } while (cursor.moveToNext())
        }

        return expenseGroupArrayList
    }

    fun updateExpenseGroup(expenseGroup: ExpenseGroup) : Int {
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, expenseGroup.id)
        contentValues.put(KEY_NAME, expenseGroup.name)

        val db = this.writableDatabase
        val success = db.update(TABLE_INCOME_GROUPS, contentValues,"id="+expenseGroup.id,null)
        db.close()

        return success
    }

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) : Int {
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, expenseGroup.id)

        val db = this.writableDatabase
        val success = db.delete(TABLE_INCOME_GROUPS,"id="+expenseGroup.id,null)
        db.close()

        return success
    }

}