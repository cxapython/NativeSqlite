package com.example.nativesqlite

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nativesqlite.MessageStorage.database
import com.example.nativesqlite.SQLite.SQLiteCursor
import com.example.nativesqlite.SQLite.SQLiteDatabase
import com.example.nativesqlite.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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