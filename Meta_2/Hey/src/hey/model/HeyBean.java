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

public class HeyBean {
	private RMIServerInterface server;
	private String username; // username and password supplied by the user
	private String password;

	public HeyBean() {
		try {

			server = (RMIServerInterface) Naming.lookup("server");//vai buscar o rmi que esta a correr em paralelo este rmi tem uma lista de users e tem dois metodos um especiede login e um delvolve todos os users

		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); // what happens *after* we reach this line?
		}
	}

	public ArrayList<String> getAllUsers() throws RemoteException {
		//return server.getAllUsers(); // are you going to throw all exceptions?
        return null;

	}

	public boolean getRegister() throws RemoteException {
		server.ping();
		return server.register(this.username,this.password);

	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
