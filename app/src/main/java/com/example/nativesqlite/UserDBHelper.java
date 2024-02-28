package com.example.nativesqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "UserDBHelper";
    //声明数据库帮助器的实例
    public static UserDBHelper userDBHelper = null;
    //声明数据库的实例
    private SQLiteDatabase db = null;
    //声明数据库的名称
    public static final String DB_NAME = "user.db";
    //声明表的名称
    public static final String TABLE_NAME = "user_info";
    //声明数据库的版本号
    public static int DB_VERSION = 1;

    public UserDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public UserDBHelper(@Nullable Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    //利用单例模式获取数据库帮助器的实例
    public static UserDBHelper getInstance(Context context, int version) {
        if (userDBHelper == null && version > 0) {
            userDBHelper = new UserDBHelper(context, version);
        } else if (userDBHelper == null) {
            userDBHelper = new UserDBHelper(context);
        }
        return userDBHelper;
    }

    //打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (db == null || !db.isOpen()) {
            db = userDBHelper.getWritableDatabase();
        }
        return db;
    }

	//getWritableDatabase()与getReadableDatabase() 这两个方法都可以获取到数据库的连接
	//正常情况下没有区别，当手机存储空间不够了
	//getReadableDatabase()就不能进行插入操作了，执行插入没有效果
	//getWritableDatabase()：也不能进行插入操作，如果执行插入数据的操作，则会抛异常。对于现在来说不会出现这种情况，用哪种方式都可以

    //打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (db == null || !db.isOpen()) {
            db = userDBHelper.getReadableDatabase();
        }
        return db;
    }

    //关闭数据库的读连接
    public void closeLink() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //如果存在user_info表，则删除该表
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        db.execSQL(drop_sql);
        //创建user_info表
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name VARCHAR NOT NULL,"
                + "age INTEGER NOT NULL,"
                + "height INTEGER NOT NULL,"
                + "weight DECIMAL(10,2) NOT NULL,"
                + "married INTEGER NOT NULL,"
                + "update_time VARCHAR NOT NULL"
                + ");";
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //根据指定条件删除表记录
    public int delete(String condition) {
        // 执行删除记录动作，该语句返回删除记录的数目
        //参数一：表名
        //参数二：whereClause where子句
        //参数三：whereArgs 您可以在 where 子句中包含 ?s，
        // 它将被 whereArgs 中的值替换。这些值将绑定为字符串。
        return db.delete(TABLE_NAME, condition, null);
    }

    //删除该表所有记录
    public int deleteAll() {
        // 执行删除记录动作，该语句返回删除记录的数目
        return db.delete(TABLE_NAME, "1=1", null);
    }

    // 往该表添加一条记录
    public long insert(UserInfo userInfo) {
        List<UserInfo> infoList = new ArrayList<>();
        infoList.add(userInfo);
        return insert(infoList);
    }

    // 往该表添加多条记录
    public long insert(List<UserInfo> infoList) {
        long result = -1;
        for (int i = 0; i < infoList.size(); i++) {
            UserInfo userInfo = infoList.get(i);
            List<UserInfo> tempList = new ArrayList<>();
            // 如果存在同名记录，则更新记录
            // 注意条件语句的等号后面要用单引号括起来
            if (userInfo.name != null && userInfo.name.length() > 0) {
                String condition = String.format("name='%s'", userInfo.name);
                tempList = query(condition);
                if (tempList.size() > 0) {
                    update(userInfo, condition);
                    result = tempList.get(0).rowid;
                    continue;
                }
            }
            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("name", userInfo.name);
            cv.put("age", userInfo.age);
            cv.put("height", userInfo.height);
            cv.put("weight", userInfo.weight);
            cv.put("married", userInfo.married);
            cv.put("update_time", userInfo.update_time);
            // 执行插入记录动作，该语句返回插入记录的行号
            //参数二：参数未设置为NULL,参数提供可空列名称的名称，以便在 cv 为空的情况下显式插入 NULL。
            //参数三：values 此映射包含行的初始列值。键应该是列名，值应该是列值
            result = db.insert(TABLE_NAME, "", cv);
            // 添加成功则返回行号，添加失败则返回-1
            if (result == -1) {
                return result;
            }
        }
        return result;
    }

    //根据条件更新指定的表记录
    public int update(UserInfo userInfo, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("name", userInfo.name);
        cv.put("age", userInfo.age);
        cv.put("height", userInfo.height);
        cv.put("weight", userInfo.weight);
        cv.put("married", userInfo.married);
        cv.put("update_time", userInfo.update_time);
        //执行更新记录动作，该语句返回更新的记录数量
        //参数二：values 从列名到新列值的映射
        //参数三：whereClause 更新时要应用的可选 WHERE 子句
        //参数四：whereArgs 您可以在 where 子句中包含 ?s，
        //它将被 whereArgs 中的值替换。这些值将绑定为字符串。
        return db.update(TABLE_NAME, cv, condition, null);
    }

    public int update(UserInfo userInfo) {
        // 执行更新记录动作，该语句返回更新的记录数量
        return update(userInfo, "rowid=" + userInfo.rowid);
    }

    @SuppressLint("Range")
    public List<UserInfo> query(String condition) {
        String sql = String.format("select * from user_info", TABLE_NAME, condition);
        List<UserInfo> infoList = new ArrayList<>();
        // 执行记录查询动作，该语句返回结果集的游标
        //参数一:SQL查询
        //参数二:selectionArgs
        //您可以在查询的 where 子句中包含 ?s，它将被 selectionArgs 中的值替换。这些值将绑定为字符串。
        Cursor cursor = db.rawQuery(sql, null);
        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            //Xxx getXxx(columnIndex):根据字段下标得到对应的值
            //int getColumnIndex():根据字段名得到对应的下标
            //cursor.getLong()：以 long 形式返回所请求列的值。
            //getColumnIndex() 获取给定列名的从零开始的列索引,如果列名不存在返回-1
            userInfo.name = cursor.getString(cursor.getColumnIndex("name"));
            userInfo.age = cursor.getInt(cursor.getColumnIndex("age"));
            userInfo.height = cursor.getLong(cursor.getColumnIndex("height"));
            userInfo.weight = cursor.getFloat(cursor.getColumnIndex("weight"));
            //SQLite没有布尔型，用0表示false，用1表示true
            userInfo.married = (cursor.getInt(cursor.getColumnIndex("married")) == 0) ? false : true;
            userInfo.update_time = cursor.getString(cursor.getColumnIndex("update_time"));
            infoList.add(userInfo);
        }
        //查询完毕，关闭数据库游标
        cursor.close();
        return infoList;
    }
}
