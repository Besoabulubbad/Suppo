package Model;

public class User {
    private  String PhoneNumber;
    private String status;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    private  String UserID;
    private  String fName;
    private  String lName;
    private  String passCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User(String PhoneNumber, String UserID, String fName, String lName, String passCode, String status) {
        this.PhoneNumber=PhoneNumber;
        this.UserID = UserID;
        this.fName = fName;
        this.lName = lName;
        this.passCode = passCode;
        this.status=status;
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
