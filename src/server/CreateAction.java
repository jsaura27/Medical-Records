package server;

public class CreateAction extends Action {

    private User nurse, patient;
    private OscarsDatabase odb;

    public CreateAction(User patient, User nurse, OscarsDatabase odb) {
        this.patient = patient;
        this.nurse = nurse;
        this.odb = odb;
    }

    @Override
    public String execute(User loggedInUser) {
        User emp = null;
        String msg = "You are not authorized to perform this action";
        for(User user : odb.getUsers()) {
            if(user.equalsName(loggedInUser) || user.equals(loggedInUser) || (user.getType().equals("gov") && loggedInUser.getType().equals("gov"))) {
             emp = user;
            }
        }
        if(emp.getType().equals("doctor")) {
            Record newRecord = new Record(division, emp, nurse, patient);
            odb.addRecord(newRecord);
            msg = "New record created for patient: " + patient.toString() + " by doctor: " + emp.getID();
            log.info(msg);
        } else {
            log.info(msg);
        }
        return msg;
    }
}
