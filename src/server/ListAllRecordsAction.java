package server;

import java.util.*;

public class ListAllRecordsAction extends Action {
    private User patient;
    private OscarsDatabase odb;


    public ListAllRecordsAction(User patient, OscarsDatabase odb) {
        this.patient = patient;
        this.odb = odb;
    }

    public String execute(User loggedInUser) {
        User emp = null;
        for(User user : odb.getUsers()) {
            if(user.equalsName(loggedInUser) || user.equals(loggedInUser) || (user.getType().equals("gov") && loggedInUser.getType().equals("gov"))) {
                emp = user;
            }
        }

        ArrayList<Record> records = odb.getPatientRecords(patient);
        StringBuilder sb = new StringBuilder();
        sb.append("Patient with ID " + patient.getID() + " has:");

        for (Record rec : records) {
            if (rec.patient.equals(patient.getID()) || rec.doctor.equals(emp) || rec.nurse.equals(emp) || rec.division.equals(emp.getDivision())|| (emp.getType().equals("gov") && loggedInUser.getType().equals("gov"))) {
                sb.append(rec.getId()).append(", ");
                log.info("User " + " (" + emp.getID() + ") " + "listed records for patient " + patient.getID());
            } else {
                log.warning("User with insufficient rights " + " (" + emp.getID() + ") " + "tried to list records for patient " + patient.getID());
                return "You do not have permission";
            }
        }
        return sb.toString();
    }
}