package primes.model;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class CredentialBean {
    private Hello_S_I server;
    private String username; // username and password supplied by the user
    private String password;

    public CredentialBean() {
        try {
            server = (Hello_S_I) Naming.lookup("Primario");
        }
        catch(NotBoundException|MalformedURLException|RemoteException e) {
            e.printStackTrace(); // what happens *after* we reach this line?
        }
    }

//    public ArrayList<String> getAllUsers() throws RemoteException {
//        return server.getAllUsers(); // are you going to throw all exceptions?
//    }
//
//    public boolean getUserMatchesPassword() throws RemoteException {
//        return server.userMatchesPassword(this.username, this.password);
//    }


    public Hello_S_I getServer() {
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
