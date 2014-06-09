package com.example.stackoverflowsearch.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by mohit on 6/9/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "querySearch.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<QueryData,Integer> queryDao = null;
    private Dao<QuestionData,Integer> questionDao = null;

    private RuntimeExceptionDao<QueryData,Integer> queryRuntimeDao = null;
    private RuntimeExceptionDao<QuestionData,Integer> questionRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION,R.raw.ormlite_config);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,QueryData.class);
            TableUtils.createTable(connectionSource,QuestionData.class);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource,QueryData.class,true);
            TableUtils.dropTable(connectionSource,QuestionData.class,true);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dao<QueryData,Integer> getQueryDao() throws SQLException{
        if (queryDao==null) {
            queryDao = getDao(QueryData.class);
        }
        return queryDao;
    }

    public Dao<QuestionData,Integer> getQuestionDao() throws SQLException{
        if(questionDao==null) {
            questionDao=getDao(QuestionData.class);
        }
        return questionDao;
    }

    public RuntimeExceptionDao<QueryData,Integer> getQueryRuntimeDao (){
        if(queryRuntimeDao==null) {
            queryRuntimeDao = getRuntimeExceptionDao(QueryData.class);
        }
        return queryRuntimeDao;
    }

    public RuntimeExceptionDao<QuestionData,Integer> getQuestionRuntimeDao() {
        if(questionRuntimeDao == null ) {
            questionRuntimeDao = getRuntimeExceptionDao(QuestionData.class);
        }
        return questionRuntimeDao;
    }
}
