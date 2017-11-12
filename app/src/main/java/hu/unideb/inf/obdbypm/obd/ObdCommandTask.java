package hu.unideb.inf.obdbypm.obd;

import com.github.pires.obd.commands.ObdCommand;

/**
 * Created by Posta Mario on 05/11/2017.
 */

public class ObdCommandTask {
    private int id;
    private ObdCommand command;

    public ObdCommandTask(int id, ObdCommand command) {
        this.id = id;
        this.command = command;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ObdCommand getCommand() {
        return command;
    }

    public void setCommand(ObdCommand command) {
        this.command = command;
    }
}
