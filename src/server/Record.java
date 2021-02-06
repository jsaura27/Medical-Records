package server;

public class Record {

    public User doctor, nurse;
    public User patient;
    public String division;
    public static int counter = 0;
    public int newRecordNbr;
    public String notes;

    public Record(String division, User doctor, User nurse, User patient) {
        this.division = division;
        this.doctor = doctor;
        this.nurse = nurse;
        this.patient = patient;
        counter ++;
        this.newRecordNbr = counter;
        this.notes = "";
    }

    public User getNurse() {
        return nurse;
    }

    public void addNote(String noteToAdd) {
        notes += noteToAdd;
        //db.update(this);
    }

    public String getDivision() {
        return division;
    }

    public User getPatient() {
        return patient;
    }

    public int getNewRecordNbr() {
        return newRecordNbr;
    }

    public User getDoctor() { return doctor; }

    public String getNotes() {
        return "Records for patient " + patient.getID()  + ": " + notes;
    }

	public int getId() {
		return newRecordNbr;
	}
}
