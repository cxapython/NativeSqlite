package com.example.nativesqlite

import MessageElement.Message.parseFrom
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nativesqlite.SQLite.SQLiteCursor
import com.example.nativesqlite.SQLite.SQLiteDatabase
import com.example.nativesqlite.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TABLE_NAME = "user"
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_PERMISSION_CODE
                )
            }
        }
//        val helper = getInstance(this,2)
//        val db= helper.openWriteLink()
//        helper.onCreate(db)
//        val userInfo = UserInfo()
//        userInfo.age=12
//        userInfo.height=123
//        userInfo.name="zhangsan"
//        helper.insert(userInfo)
        // Example of a call to a native method
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val database =
                    SQLiteDatabase("/storage/emulated/0/Download/plaintext_xxx.db")
                val cursor: SQLiteCursor = database.queryFinalized(
                    "SELECT \"40030\" as groupId,\"40033\" as nickId,\"40050\" as time , \"40093\" as nickName, \"40800\" as msgContent\n" +
                            "FROM group_msg_table where time>? order by time;", 0
                )
                if (cursor.next()) {
                    cursor.columnCount
                    val groupId = cursor.stringValue(0)
                    val qqId = cursor.stringValue(1)
                    val time = cursor.intValue(2)
                    val nickName = cursor.stringValue(3)
                    val contentProtobbuf = cursor.byteArrayValue(4)
                    val contentInfo: MessageElement.Message? =
                        parseFrom(contentProtobbuf)
                    val messages = contentInfo?.messagesList
                    var messageText: String = ""
                    if (messages != null) {
                        for (message in messages) {
                            if (message.messageType == 1) {
                                messageText = message.messageText
                                break
                            }
                        }
                    }
                    Log.d("NativeSql", "groupId:${groupId},qqId:${qqId}")
                }
            }
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
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SYSTEM_ALERT_WINDOW
        )
        private const val REQUEST_PERMISSION_CODE = 1
    }
}