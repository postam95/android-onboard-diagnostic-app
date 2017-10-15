package hu.unideb.inf.obdbypm.database;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.ServiceBookRecord;

public class DatabaseManager {

    private static DatabaseManager instance;
    private DatabaseHelper helper;

    public static void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    public DatabaseHelper getHelper() {
        return helper;
    }

    /**
     * Get all customer in db
     *
     * @return
     */




    public ArrayList<Car> getAllCars() {
        ArrayList<Car> cars = null;
        try {
            cars = (ArrayList<Car>) getHelper().getCarsDAO().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public void addCar(Car car) {
        try {
            getHelper().getCarsDAO().create(car);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshCar(Car car) {
        try {
            getHelper().getCarsDAO().refresh(car);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCar(Car wishList) {
        try {
            getHelper().getCarsDAO().update(wishList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCar (int carId) {
        try {
            DeleteBuilder<Car, Integer> deleteBuilder = getHelper().getCarsDAO().deleteBuilder();
            deleteBuilder.where().eq("id", carId);
            deleteBuilder.delete();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ServiceBookRecord newServiceBookRecord() {
        ServiceBookRecord serviceBookRecord = new ServiceBookRecord();
        try {
            getHelper().getServiceBookRecordDAO().create(serviceBookRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceBookRecord;
    }

    public ServiceBookRecord newServiceBookRecordAppend(ServiceBookRecord serviceBookRecord) {
        try {
            getHelper().getServiceBookRecordDAO().create(serviceBookRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceBookRecord;
    }

    public void updateServiceBookRecord(ServiceBookRecord item) {
        try {
            getHelper().getServiceBookRecordDAO().update(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ServiceBookRecord> getAllServiceBookRecords() {
        ArrayList<ServiceBookRecord> serviceBookRecordArrayList = null;
        try {
            serviceBookRecordArrayList = (ArrayList<ServiceBookRecord>) getHelper().getServiceBookRecordDAO().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceBookRecordArrayList;
    }

    public void deleteServiceBookRecord (int serviceBookRecordId) {
        try {
            DeleteBuilder<ServiceBookRecord, Integer> deleteBuilder = getHelper().getServiceBookRecordDAO().deleteBuilder();
            deleteBuilder.where().eq("id", serviceBookRecordId);
            deleteBuilder.delete();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}