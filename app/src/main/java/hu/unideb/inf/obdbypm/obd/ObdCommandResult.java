package hu.unideb.inf.obdbypm.obd;

/**
 * Created by Posta Mario on 05/11/2017.
 */

public class ObdCommandResult {
    private int id;
    private String value;

    public ObdCommandResult() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
