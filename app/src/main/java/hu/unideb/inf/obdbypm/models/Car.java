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

    @DatabaseField(generatedId=true, columnName = "id")
    private int id;

    @DatabaseField(columnName = "brand")
    private String brand;

    @DatabaseField(columnName = "type")
    private String type;

    @DatabaseField(columnName = "licenseNumber")
    private String licenseNumber;

    @ForeignCollectionField(columnName = "serviceBookRecords")
    private ForeignCollection<ServiceBookRecord> serviceBookRecords;

    @DatabaseField(foreign=true, foreignAutoRefresh=true, columnName = "person")
    private Person person;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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
        return this.brand + " " + this.type;
    }
}
