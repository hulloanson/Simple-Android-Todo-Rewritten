package com.ycl.androidsimpletodorewrite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by ycl on 12/5/15.
 */
public class DbAdapter {
    public static final String dbTaskName = "name";
    public static final String dbTaskId = "id";

    private static final String dbName = "tasks.db";
    private static final String tableName = "tasks";
    private static final int dbVersion = 1;

    private DbHelper tasksDbHelper;
    private SQLiteDatabase tasksDb;

    private static final String createTableString = "CREATE TABLE " + tableName + " (" + dbTaskId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + dbTaskName + " TEXT NOT NULL);";

    private final Context mainContext;

    public DbAdapter(Context context){
        this.mainContext = context;
    }

    public DbAdapter open() throws SQLException {
        tasksDbHelper = new DbHelper(mainContext);
        tasksDb = tasksDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        tasksDbHelper.close();
    }

    private static class DbHelper extends SQLiteOpenHelper{

        DbHelper(Context context){
            super(context, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(createTableString);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
    }

    public long writeTask(String taskString) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(dbTaskName, taskString);

        return tasksDb.insert(tableName, null, initialValues);
    }

    public Cursor getAll() {
        return tasksDb.query(tableName, null, null, null, null, null,null);
    }

    public long deleteTask(long taskId) {
        return tasksDb.delete(tableName, dbTaskId, new String[] {String.valueOf(taskId)});
    }
}
