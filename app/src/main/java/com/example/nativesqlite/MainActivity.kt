package com.example.nativesqlite

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nativesqlite.SQLite.SQLiteCursor
import com.example.nativesqlite.SQLite.SQLiteDatabase
import com.example.nativesqlite.SQLite.SQLitePreparedStatement
import com.example.nativesqlite.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TABLE_NAME = "user"
    fun createTables(db: SQLiteDatabase) {


        //如果存在user_info表，则删除该表
        val drop_sql = "DROP TABLE IF EXISTS ${TABLE_NAME};"
        db.executeFast(drop_sql).stepThis().dispose();
        //创建user_info表
        //创建user_info表
        val create_sql = ("CREATE TABLE IF NOT EXISTS ${TABLE_NAME} ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name VARCHAR NOT NULL,"
                + "age INTEGER NOT NULL,"
                + "height INTEGER NOT NULL,"
                + "weight DECIMAL(10,2) NOT NULL,"
                + "married INTEGER NOT NULL,"
                + "update_time VARCHAR NOT NULL"
                + ");")
        db.executeFast(create_sql).stepThis().dispose();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val helper = getInstance(this,2)
//        val db= helper.openWriteLink()
//        helper.onCreate(db)
//        val userInfo = UserInfo()
//        userInfo.age=12
//        userInfo.height=123
//        userInfo.name="zhangsan"
//        helper.insert(userInfo)
        // Example of a call to a native method
        val database = SQLiteDatabase("/data/data/com.example.nativesqlite/databases/user.db")
//插入操作示例：待确认
//        val state: SQLitePreparedStatement = database.executeFast("INSERT INTO user VALUES (?, ?, ?, ?,?,?)");
//        state.requery();
//        state.bindInteger(0,3)
//        state.bindString(1,"xiaohong")
//        state.bindInteger(2,30)
//        state.bindLong(3,30L)
//        state.bindDouble(4,30.0)
//        state.step();
//        state.dispose();
        val cursor: SQLiteCursor = database.queryFinalized("SELECT name,age FROM user_info")
        if (cursor.next()) {
            val name = cursor.stringValue(0)
            val age = cursor.intValue(1)
            Log.d("NativeSql", "name:${name},age:${age}")
        }


        binding.sampleText.text = stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'nativesqlite' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'nativesqlite' library on application startup.
        init {
            System.loadLibrary("nativesqlite")
        }
    }
}