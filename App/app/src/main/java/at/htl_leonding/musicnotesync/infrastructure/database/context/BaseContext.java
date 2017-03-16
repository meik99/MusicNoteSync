package at.htl_leonding.musicnotesync.infrastructure.database.context;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import at.htl_leonding.musicnotesync.infrastructure.database.DBHelper;

/**
 * Created by michael on 3/16/17.
 */

abstract class BaseContext<T> {
    DBHelper dbHelper;
    SQLiteDatabase readableDatabase;
    SQLiteDatabase writeableDatabase;
    Context context;

    BaseContext(Context context){
        this.context = context;
        dbHelper = new DBHelper(context);
        readableDatabase = dbHelper.getReadableDatabase();
        writeableDatabase = dbHelper.getWritableDatabase();
    }

    public abstract List<T> findAll();
    public abstract T findById(long id);
    public abstract T create(T entity);
    public abstract T update(T entity);
    public abstract T delete(T entity);

}
