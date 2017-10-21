package hu.unideb.inf.obdbypm.models;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Posta Mario on 14/10/2017.
 */

public class ServiceBookRecord {

    @DatabaseField(generatedId=true, columnName = "id")
    private int id;

    @DatabaseField(columnName = "serviceCompany")
    private String serviceCompany;

    @DatabaseField(columnName = "carModel")
    private String carModel;

    @DatabaseField(columnName = "serviceDate")
    private Date serviceDate;

    @DatabaseField(columnName = "currentMileage")
    private int currentMileage;

    @DatabaseField(columnName = "oilChange")
    private boolean oilChange;

    @DatabaseField(columnName = "chassisRepair")
    private boolean chassisRepair;

    @DatabaseField(columnName = "airFilterChange")
    private boolean airFilterChange;

    @DatabaseField(columnName = "oilFilterChange")
    private boolean oilFilterChange;

    @DatabaseField(columnName = "tiresRotate")
    private boolean tiresRotate;

    @DatabaseField(columnName = "brakesAdjust")
    private boolean brakesAdjust;

    @DatabaseField(columnName = "wheelsAlign")
    private boolean wheelsAlign;

    @DatabaseField(columnName = "tiresReplace")
    private boolean tiresReplace;

    @DatabaseField(columnName = "transmissionFluidChange")
    private boolean transmissionFluidChange;

    @DatabaseField(columnName = "engineTuneup")
    private boolean engineTuneup;

    @DatabaseField(columnName = "flushCoolingSystem")
    private boolean flushCoolingSystem;

    @DatabaseField(columnName = "comment")
    private String comment;

    @DatabaseField(columnName = "totalCost")
    private String totalCost;

    @DatabaseField(foreign=true, foreignAutoRefresh=true, columnName = "name")
    private Car car;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceCompany() {
        return serviceCompany;
    }

    public void setServiceCompany(String serviceCompany) {
        this.serviceCompany = serviceCompany;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public int getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(int currentMileage) {
        this.currentMileage = currentMileage;
    }

    public boolean isOilChange() {
        return oilChange;
    }

    public void setOilChange(boolean oilChange) {
        this.oilChange = oilChange;
    }

    public boolean isChassisRepair() {
        return chassisRepair;
    }

    public void setChassisRepair(boolean chassisRepair) {
        this.chassisRepair = chassisRepair;
    }

    public boolean isAirFilterChange() {
        return airFilterChange;
    }

    public void setAirFilterChange(boolean airFilterChange) {
        this.airFilterChange = airFilterChange;
    }

    public boolean isOilFilterChange() {
        return oilFilterChange;
    }

    public void setOilFilterChange(boolean oilFilterChange) {
        this.oilFilterChange = oilFilterChange;
    }

    public boolean isTiresRotate() {
        return tiresRotate;
    }

    public void setTiresRotate(boolean tiresRotate) {
        this.tiresRotate = tiresRotate;
    }

    public boolean isBrakesAdjust() {
        return brakesAdjust;
    }

    public void setBrakesAdjust(boolean brakesAdjust) {
        this.brakesAdjust = brakesAdjust;
    }

    public boolean isWheelsAlign() {
        return wheelsAlign;
    }

    public void setWheelsAlign(boolean wheelsAlign) {
        this.wheelsAlign = wheelsAlign;
    }

    public boolean isTiresReplace() {
        return tiresReplace;
    }

    public void setTiresReplace(boolean tiresReplace) {
        this.tiresReplace = tiresReplace;
    }

    public boolean isTransmissionFluidChange() {
        return transmissionFluidChange;
    }

    public void setTransmissionFluidChange(boolean transmissionFluidChange) {
        this.transmissionFluidChange = transmissionFluidChange;
    }

    public boolean isEngineTuneup() {
        return engineTuneup;
    }

    public void setEngineTuneup(boolean engineTuneup) {
        this.engineTuneup = engineTuneup;
    }

    public boolean isFlushCoolingSystem() {
        return flushCoolingSystem;
    }

    public void setFlushCoolingSystem(boolean flushCoolingSystem) {
        this.flushCoolingSystem = flushCoolingSystem;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
