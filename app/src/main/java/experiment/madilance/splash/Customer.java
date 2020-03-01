package experiment.madilance.splash;

public class Customer {

    public String cname,cemail,cpassword,cmobile;
    public Customer()
    {

    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCemail() {
        return cemail;
    }

    public void setCemail(String cemail) {
        this.cemail = cemail;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public String getCmobile() {
        return cmobile;
    }

    public void setCmobile(String cmobile) {
        this.cmobile = cmobile;
    }

    public Customer(String cname, String cemail, String cpassword, String cmobile) {
        this.cname = cname;
        this.cemail = cemail;
        this.cpassword = cpassword;
        this.cmobile = cmobile;
    }
}
