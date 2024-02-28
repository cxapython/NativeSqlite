package com.example.nativesqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //声明一个Context
    private
    Context context;
    //原本的重写方法很多用不到的参数   这里直接删除了，第二个参数为数据库的名字(需要用db结尾)
    // 调用父类的时候传入1个空值就可以了
    //这里的两个参数分别是传入的Content（调用的Activity）   version（版本）
    public MyDatabaseHelper(@Nullable Context context, int version) {
        super(context, "/data/local/tmp/mytestdata.db", null, version);
        //赋值
        this.context=context;
    }

    //创建数据库的方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "create table Book(" +
                "id integer," +
                "name text)";
        //执行SQL语句
        db.execSQL(s);
        Toast.makeText(context, "数据库创建成功", Toast.LENGTH_SHORT).show();
    }
    //升级数据库的   这里多了两个参数
    //这两个参数是版本号，只有新版本号大于旧版本号的时候才能运行这个方法
    //升级的时候记得换一个表明，表明不能相同
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String s = "create table User(" +
                "id integer," +
                "name text)";
        //执行SQL语句
        db.execSQL(s);
        Toast.makeText(context, "数据库升级成功", Toast.LENGTH_SHORT).show();
    }
}

