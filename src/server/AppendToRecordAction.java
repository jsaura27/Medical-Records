package server;


public class AppendToRecordAction extends Action {
    private String recordID;
    private String text;
    private OscarsDatabase odb;
    private User patient;

    public AppendToRecordAction(User patient, String recordID, String text, OscarsDatabase odb) {
        this.patient = patient;
        this.odb = odb;
        this.recordID = recordID;
        this.text = text;
    }

    public String execute(User loggedInUser) {
        User emp = null;
        for (User user : odb.getUsers()) {
            if (user.equalsName(loggedInUser)) {
                emp = user;
            }
        }
        Record record = odb.getRecordForPatient(patient, Integer.parseInt(recordID));
        boolean ok = false;
        try {
            if (emp.getType().equals("nurse")) {
                if (emp.equalsName(record.nurse) || division.equals(record.division)) {
                    ok = true;
                }
            } else if (emp.getType().equals("doctor")) {
                if (emp.equalsName(record.getDoctor()) || division.equals(record.getDivision())) {
                    ok = true;
                }
            }
            if (ok) {
                odb.appendToRecord(record.getPatient(), Integer.parseInt(recordID), text);
                log.info(String.format(String.format("%s@%s appended record %s", subject, division, record.newRecordNbr)));
            } else if (record != null) {
                log.info(String.format("Access denied for %s on %s", subject, record.newRecordNbr));
            }
        } catch (NullPointerException e) {
            log.warning("There is no record with ID: " + recordID);
        }
        return (ok) ? "Record successfully changed" : "Permission denied, opsiee";
    }
}