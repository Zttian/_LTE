package com.tky.lte;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.tky.lte.gen.DaoMaster;
import com.tky.lte.gen.DaoSession;
import com.tky.lte.util.CrashHandler;
import com.tky.lte.util.Utils;

/**
 * Created by ttz on 2018/3/28.
 */

public class LTEApp extends Application {
    private static LTEApp mInstance;
    private static DaoSession daoSession;


    public static LTEApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Utils.init(this);
        CrashHandler.getInstance().init(this);
        setupDatabase();
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库shop.db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lte.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstance() {
        return daoSession;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
