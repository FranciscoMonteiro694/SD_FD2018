package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import hey.model.CredentialBean;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;


    @Override
    public String execute() {
        //Tenho que chamar o RMI Server
        if(this.username != null && !username.equals("") && this.password !=null && !password.equals("")) {
            this.getCredentialBean().setUsername(this.username);
            this.getCredentialBean().setPassword(this.password);
            try {
                if(this.getCredentialBean().getRegister()){
                    return SUCCESS;
                }
                else{
                    return "failed";
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }

    public void setPassword(String password) {
        this.password = password; // what about this input?
    }

    public CredentialBean getCredentialBean() {
        if(!session.containsKey("CredentialBean"))
            this.setCredentialBean(new CredentialBean());
        return (CredentialBean) session.get("CredentialBean");
    }

    public void setCredentialBean(CredentialBean CredentialBean) {
        this.session.put("CredentialBean", CredentialBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }


}