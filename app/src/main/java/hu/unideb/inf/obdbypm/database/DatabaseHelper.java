package hu.unideb.inf.obdbypm.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.Person;
import hu.unideb.inf.obdbypm.models.ServiceBookRecord;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "myServiceBook.db";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private Dao<Person, Integer> personDAO = null;
    private Dao<Car, Integer> carDAO = null;
    private Dao<ServiceBookRecord, Integer> serviceBookRecordDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Person.class);
            TableUtils.createTable(connectionSource, Car.class);
            TableUtils.createTable(connectionSource, ServiceBookRecord.class);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<>();
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }

    }

    public Dao<Person, Integer> getPersonDAO() {
        if (personDAO == null) {
            try {
                personDAO = getDao(Person.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return personDAO;
    }

    public Dao<Car, Integer> getCarsDAO() {
        if (carDAO == null) {
            try {
                carDAO = getDao(Car.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return carDAO;
    }

    public Dao<ServiceBookRecord, Integer> getServiceBookRecordDAO() {
        if (serviceBookRecordDAO == null) {
            try {
                serviceBookRecordDAO = getDao(ServiceBookRecord.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return serviceBookRecordDAO;
    }

}