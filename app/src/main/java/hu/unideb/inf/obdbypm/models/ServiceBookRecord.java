package hu.unideb.inf.obdbypm.models;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Posta Mario on 14/10/2017.
 */

public class ServiceBookRecord {

    @DatabaseField(generatedId=true)
    private int id;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(foreign=true, foreignAutoRefresh=true)
    private Car car;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
