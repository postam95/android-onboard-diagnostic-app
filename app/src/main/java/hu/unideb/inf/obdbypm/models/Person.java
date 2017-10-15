package hu.unideb.inf.obdbypm.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Posta Mario on 14/10/2017.
 */

public class Person {

    @DatabaseField(generatedId=true)
    private int id;

    @DatabaseField (columnName = "name")
    private String name;

    @DatabaseField (columnName = "email")
    private String emailAddress;

    @DatabaseField (columnName = "password")
    private String password;

    @ForeignCollectionField
    private ForeignCollection<Car> cars;

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Car> getCars() {
        ArrayList<Car> itemList = new ArrayList<>();
        for (Car item : cars) {
            itemList.add(item);
        }
        return itemList;
    }

    public void setCars(ForeignCollection<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
