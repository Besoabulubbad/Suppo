package Model;

public class User {
    private  String PhoneNumber;
    private  String UserID;
    private  String fName;
    private  String lName;
    private  String passCode;

    public User(String PhoneNumber, String UserID, String fName, String lName, String passCode) {
        this.PhoneNumber=PhoneNumber;
        this.UserID = UserID;
        this.fName = fName;
        this.lName = lName;
        this.passCode = passCode;
    }
    public  User(){}
public void setUserPhone(String phoneNumber)
{
    this.PhoneNumber=phoneNumber;
}
public  String  getPhoneNumber()
{
    return PhoneNumber;
}
    public String getUID() { return UserID;}
    public void setUID(String UserID) {
        this.UserID = UserID;
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
        return passCode;
    }
    public void setPasscode(String passcode) {
        this.passCode = passcode;
    }
}
