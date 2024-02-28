package com.example.nativesqlite;


import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.example.nativesqlite.SQLite.SQLiteCursor;
import com.example.nativesqlite.SQLite.SQLiteDatabase;
import com.example.nativesqlite.SQLite.SQLiteException;
import com.example.nativesqlite.tgnet.NativeByteBuffer;
import com.example.nativesqlite.tgnet.TLRPC;

import java.util.HashMap;
import java.util.Locale;

public class MessageStorage {
    private static final String TAG = "tgJNI";
    public static SQLiteDatabase database = null;
    private String mCurrApkPath = Environment.getExternalStorageDirectory().getPath() + "/";
    private static final String COPY_WX_DATA_DB = "web_cache4.db";
    String copyFilePath = mCurrApkPath + COPY_WX_DATA_DB;
    public static HashMap<Long, String> NickNameMap = null; //tg昵称和id
    public static HashMap<Long, String> channelMap = null; //tg群名和tg的id

//    public void openDatabase() {
//        try {
//            database = SQLiteDatabase.openDatabase(copyFilePath, null, SQLiteDatabase.OPEN_READWRITE);
//            database.rawQuery("PRAGMA secure_delete = ON",null);
//            database.rawQuery("PRAGMA temp_store = MEMORY",null);
//            database.rawQuery("PRAGMA journal_mode = WAL",null);
//            database.rawQuery("PRAGMA journal_size_limit = 10485760",null);
//        } catch (Exception exception) {
//            Log.d("cxaErr",exception.getMessage());
//
//        }
//    }

    public void initNickNameMap() {
        this.NickNameMap = new HashMap<>();
    }

    public void ReadHistoryData() {
        SQLiteCursor cursor = null;
        initNickNameMap();
        try {
            database = new SQLiteDatabase(copyFilePath);
            database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            database.executeFast("PRAGMA journal_size_limit = 10485760").stepThis().dispose();
            int offset = 0;
            try {
                cursor = database.queryFinalized(String.format(Locale.US, "SELECT uid,name FROM users"));
                while (cursor.next()) {
                    long userId = cursor.longValue(0);
                    String name = cursor.stringValue(1);
                    NickNameMap.put(userId, name);
                }

            } catch (Exception e) {

            }
            cursor.dispose();
            cursor = null;


            while (offset < 400) {
                //mid是消息id, uid是频道id，is_channel如果是频道就显示id，不是就是0。
                cursor = database.queryFinalized(String.format(Locale.US, "select m.mid,m.is_channel,m.data,m.date,c.name from messages_v2 as m LEFT JOIN chats as c on m.is_channel=c.uid where m.is_channel!=0 ORDER BY m.mid limit 50 offset %d", offset));
                while (cursor.next()) {
                    long messageId = cursor.longValue(0);
                    long channelId = cursor.longValue(1);
                    NativeByteBuffer data = cursor.byteBufferValue(2);
                    long date = cursor.longValue(3);
                    String channel_name = cursor.stringValue(4);
                    int dataInt = data.readInt32(false);
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, dataInt, false);
                    if (message != null) {
                        String content = message.message;
                        if (TextUtils.isEmpty(content)) {
                            continue;
                        }

                        long user_id = message.from_id.user_id;
                        if (user_id == 0) {
                            continue;
                        }
                        String nameInfo = NickNameMap.get(user_id);
                        String userName = "未知";
                        String[] arr = nameInfo.split(";;;");
                        if (arr.length >= 1) {
                            userName = arr[0];
                        }
                        Log.d("Mydata", "channel_name:"+channel_name+",date:"+String.valueOf(date)+",channelId:" + String.valueOf(channelId) + ",messageId:" + String.valueOf(messageId) + ",userId:" + String.valueOf(user_id) + ",userName:" + userName + ",content:" + content);
                    }
                    data.reuse();
                }
                SystemClock.sleep(2000);
                offset += 50;
                Log.d("Mydata", String.valueOf(offset));
            }


        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            cursor.dispose();
        }

    }
}
