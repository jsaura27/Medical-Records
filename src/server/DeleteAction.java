package server;

import java.util.ArrayList;
import java.util.Map;

public class DeleteAction extends Action {
    private String recordID;
    private OscarsDatabase odb;
    private User patient;

    public DeleteAction (User patient, String recordID, OscarsDatabase odb) {
        this.patient = patient;
        this.odb = odb;
        this.recordID = recordID;
    }

    public String execute(User loggedInUser) {
        User emp = null;
        for(User user : odb.getUsers()) {
            if(user.equalsName(loggedInUser) || (user.getType().equals("gov") && loggedInUser.getType().equals("gov"))) {
                emp = user;
            }
        }
        if (emp.getType().equals("gov")) {
            odb.removeRecord(patient,Integer.parseInt(recordID));
            log.info("Record with ID " + recordID + " removed by gov.");
            return "Record with ID " + recordID + " removed.";
        }
        log.warning("User with insufficient rights " + " (" + emp.getID() + ") " +  "tried to remove record with ID " + recordID);
        return "You do not have permission";
    }
}