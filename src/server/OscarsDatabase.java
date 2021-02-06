package server;

import java.util.ArrayList;

public class OscarsDatabase {

    private ArrayList<Record> records;
    private ArrayList<User> users;
    private static int counter = 0;

    public OscarsDatabase() {
        records = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void removeRecord(User patient, int recNbr) {
        for (int i = 0; i < records.size(); i++) {
            Record rec = records.get(i);
            if(rec.getPatient().equals(patient)) {
                if(rec.newRecordNbr == recNbr) {
                    records.remove(i);
                    System.out.print("Success!!");
                }
            }
        }
        System.out.print("Action failed...");
    }

    public boolean appendToRecord(User patient, int recNbr, String textToAppend) {
        for (Record rec : records) {
            if(rec.getPatient().equals(patient)) {
                if(rec.newRecordNbr == recNbr) {
                    rec.addNote("================" + textToAppend);
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Record> getPatientRecords(User patient) {
        ArrayList<Record> toReturn = new ArrayList<>();
        for (Record rec : records) {
            if (rec.patient.equals(patient)) {
                toReturn.add(rec);
            }
        }
        return toReturn;

    }
    public Record getRecordForPatient(User patient, int recNbr) {
            for (Record rec : records) {
                if (rec.patient.equals(patient) && rec.getId() == recNbr) {
                    return rec;
                }
            }
            return null;
    }

    public void addUser(User user) {
        users.add(user);
        user.setId( ++ counter);
    }

    public User getPatient(String s) {
        for (User user : users) {
            if (user.getID() == Integer.parseInt(s)) {
            	return user;
            }
        }
        return null;
    }
}