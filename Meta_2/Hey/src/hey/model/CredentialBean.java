package hey.model;

import rmiserver.RMIServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class CredentialBean {
    private RMIServerInterface server;
    private String username; // username and password supplied by the user
    private String password;

    public CredentialBean() {
        try {
            server = (RMIServerInterface) Naming.lookup("server");
        }
        catch(NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
    }

    public RMIServerInterface getServer() {
        return server;
    }


    public boolean getRegister() throws RemoteException {
        return server.register(this.username,this.password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}