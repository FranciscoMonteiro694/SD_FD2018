/**
 * Raul Barbosa 2014-11-07
 */
package hey.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;
import hey.model.LoginBean;

public class LoginAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() {
        if(this.username != null && !username.equals("") && this.password !=null && !password.equals("")) {
            this.getLoginBean().setUsername(this.username);
            this.getLoginBean().setPassword(this.password);
            try {
                if(this.getLoginBean().getUserMatchesPassword()){
                    return SUCCESS;
                }
                else{
                    return "failed";
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "failed";

    }

    public void setUsername(String username) {
        this.username = username; // will you sanitize this input? maybe use a prepared statement?
    }

    public void setPassword(String password) {
        this.password = password; // what about this input?
    }

    public LoginBean getLoginBean() {
        if(!session.containsKey("LoginBean"))
            this.setLoginBean(new LoginBean());
        return (LoginBean) session.get("LoginBean");
    }

    public void setLoginBean(LoginBean LoginBean) {
        this.session.put("LoginBean", LoginBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
