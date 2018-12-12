/**
 * Raul Barbosa 2014-11-07
 */
package hey.model;

import java.util.ArrayList;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import rmiserver.RMIServerInterface;

public class LoginBean {
    private RMIServerInterface server;
    private String username; // username and password supplied by the user
    private String password;

    public LoginBean() {
        try {
            server = (RMIServerInterface) Naming.lookup("server");
        }
        catch(NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
    }

    public boolean getUserMatchesPassword() throws RemoteException{
        return server.login(this.username,this.password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
