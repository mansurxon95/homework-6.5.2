package com.example.a652

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.net.toUri

class MyDB(context: Context): SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION),MyDBservis {


    companion object{
        const val DB_NAME = "YPX"
        const val DB_VERSION = 1



        const val TABLE_USER_NAME = "name"
        const val USER_ID = "ypxid"
        const val  USER_NAME = "sarlavxa"
        const val USER_TEL = "matni"
        const val USER_DAVLAT = "image"
        const val USER_VILOYAT = "yt_id"
        const val USER_PASSWORD = "nol"
        const val USER_IMAGE = "nn"


    }

    override fun onCreate(db: SQLiteDatabase?) {

        val query1 = "create table $TABLE_USER_NAME($USER_ID integer not null primary key autoincrement unique," +
                "$USER_NAME text not null," +
                "$USER_TEL text not null," +
                "$USER_DAVLAT text not null," +
                "$USER_VILOYAT TEXT not null," +
                "$USER_PASSWORD TEXT not null," +
                "$USER_IMAGE TEXT NOT NULL)"



        db?.execSQL(query1)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun addUser(user: User) {
        val writableDatabase = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(USER_NAME,user.lastFirst_name)
        contentValues.put(USER_TEL,user.tel_number)
        contentValues.put(USER_DAVLAT,user.davlat)     // TODO: image Int ?
        contentValues.put(USER_VILOYAT,user.viloyat)
        contentValues.put(USER_PASSWORD,user.password)
        contentValues.put(USER_IMAGE,user.image.toString())
        writableDatabase.insert(TABLE_USER_NAME,null,contentValues)
        writableDatabase.close()
    }

    override fun editUser(user: User): Int {
        val writableDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(USER_NAME,user.lastFirst_name)
        contentValues.put(USER_TEL,user.tel_number)
        contentValues.put(USER_DAVLAT,user.davlat)     // TODO: image Int ?
        contentValues.put(USER_VILOYAT,user.viloyat)
        contentValues.put(USER_PASSWORD,user.password)
        contentValues.put(USER_IMAGE,user.image.toString())
        return writableDatabase.update(TABLE_USER_NAME, contentValues, "${USER_ID}=?", arrayOf(user.id.toString()))

    }



    override fun deletUser(user: User) {
        val writableDatabase = this.writableDatabase
        writableDatabase.delete(TABLE_USER_NAME, "${USER_ID}=?", arrayOf("${user.id}"))
    }

    override fun getallUser(): ArrayList<User> {

        var list2 = ArrayList<User>()
        val query = "SELECT * from $TABLE_USER_NAME"

        val readableDatabase = this.readableDatabase
        val crusor = readableDatabase.rawQuery(query,null)
        if (crusor.moveToFirst()){
            do {
                val id = crusor.getInt(0)
                val last_first_name = crusor.getString(1)
                val tel_num = crusor.getString(2)
                val davlat = crusor.getString(3)
                val viloyat = crusor.getString(4)
                val password = crusor.getString(5)
                val image = crusor.getString(6)


                list2.add(User(id,last_first_name,tel_num,davlat,viloyat,password,image.toUri()))
            } while ( crusor.moveToNext())
        }

        return list2
    }
}