package Model;

public class User {
    private  String UID;
    private  String fName;
    private  String lName;
    private  String passcode;

    public User(String UID, String fName, String lName, String passcode) {
        this.UID = UID;
        this.fName = fName;
        this.lName = lName;
        this.passcode = passcode;
    }
    public  User(){}

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
}
