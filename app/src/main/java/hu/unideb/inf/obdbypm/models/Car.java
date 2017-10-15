package hu.unideb.inf.obdbypm.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Posta Mario on 14/10/2017.
 */

public class Car {

    @DatabaseField(generatedId=true)
    private int id;

    @DatabaseField (columnName = "name")
    private String name;

    @ForeignCollectionField
    private ForeignCollection<ServiceBookRecord> serviceBookRecords;

    @DatabaseField(foreign=true, foreignAutoRefresh=true)
    private Person person;

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

    public List<ServiceBookRecord> getServiceBookRecords() {
        ArrayList<ServiceBookRecord> itemList = new ArrayList<>();
        for (ServiceBookRecord item : serviceBookRecords) {
            itemList.add(item);
        }
        return itemList;
    }

    public void setServiceBook(ForeignCollection<ServiceBookRecord> serviceBookRecords) {
        this.serviceBookRecords = serviceBookRecords;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
