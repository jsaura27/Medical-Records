package server;

public class User {


    public int id;
    private String pass;
    private String type, division;

    /**
     * @param type
     * @param division
     */
    public User(String type, String division) {
        this.type = type;
        this.division = division;
        pass = "password";
    }

    /**
     * Create user only with DN field from cert
     *
     * @param type
     */
    public User(String type) {
        this.type = type;
    }

    public String getDivision() {
        return division;
    }

    public String getPass(){
        return pass;
    }

    public void setPass(String newPass) {
        pass = newPass;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public int compareTo(User user) {
        return id - user.id;
    }
    public boolean equalsName(User user) {
        return (type.equals(user.getType()) && division.equals(user.getDivision()));
    }

    @Override
    public String toString() {
        return "Type: " + type + " in division: " + division + " with ID: " + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return this.compareTo((User) obj) == 0;
        }
        return false;
    }
}
