package server;

public class ReadAction extends Action {

    private String recID;
    private OscarsDatabase odb;
    private User patient;


    public ReadAction(User patient, String recID, OscarsDatabase odb) {
        this.recID = recID;
        this.patient = patient;
        this.odb = odb;
    }

    @Override
    public String execute(User loggedInUser) {
        User emp = null;
        for(User user : odb.getUsers()) {
            if(user.equalsName(loggedInUser) || user.equals(loggedInUser) || (user.getType().equals("gov") && loggedInUser.getType().equals("gov"))) {
                emp = user;
            }
        }
        Record record = odb.getRecordForPatient(patient, Integer.parseInt(recID));
        if (record == null) {
            log.info("User tried to access record which was not found.");
            return "Record [" + recID + "] not found.";
        }
        switch (emp.getType()) {
            case "nurse":
                //OK if nurse==patient or if nurse is in same division as patient
                if ((emp.equals(record.nurse)) || division.equals(record.division)) {
                    log.info("Nurse " + emp.getID() + " read record " + record.newRecordNbr);
                    return record.getNotes();
                } else {
                    log.info("Nurse " + emp.getID() + "got denied access to record " + record.newRecordNbr);
                }
            case "doctor":
                //OK if doctor==patient or if doctor is in same division as patient
                if ((emp.equals(record.doctor)) || division.equals(record.division)) {
                    log.info("Doctor " + emp.getID() + " read record " + record.newRecordNbr);
                    return record.getNotes();
                } else {
                    log.info("Doctor " + emp.getID() + "got denied access to record " + record.newRecordNbr);
                }
            case "patient":
                //OK if employee==patient
                if ((emp.equals(record.patient))) {
                    log.info("Patient " + emp.getID() + " read record " + record.newRecordNbr);
                    return record.getNotes();
                } else {
                    log.info("Patient " + emp.getID() + "got denied access to record " + record.newRecordNbr);
                }
            case "gov":
                log.info("Government " + emp.getID() + " read record " + record.newRecordNbr);
                return record.getNotes();
            default:
                break;
        }
        return "You do not have permission to read this record.";
    }
}