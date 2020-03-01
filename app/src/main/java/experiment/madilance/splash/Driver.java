package experiment.madilance.splash;

public class Driver {

    public String dname,demail,dpassword,dmobile,dlicence;
    public Driver()
    {

    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDemail() {
        return demail;
    }

    public void setDemail(String demail) {
        this.demail = demail;
    }

    public String getDpassword() {
        return dpassword;
    }

    public void setDpassword(String dpassword) {
        this.dpassword = dpassword;
    }

    public String getDmobile() {
        return dmobile;
    }

    public void setDmobile(String dmobile) {
        this.dmobile = dmobile;
    }

    public String getDlicence() {
        return dlicence;
    }

    public void setDlicence(String dlicence) {
        this.dlicence = dlicence;
    }

    public Driver(String dname, String demail, String dpassword, String dmobile, String dlicence) {
        this.dname = dname;
        this.demail = demail;
        this.dpassword = dpassword;
        this.dmobile = dmobile;
        this.dlicence = dlicence;
    }
}
