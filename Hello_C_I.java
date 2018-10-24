import java.rmi.*;

public interface Hello_C_I extends Remote{
	public void print_on_client(String s) throws java.rmi.RemoteException;
        //public void menu1() throws java.rmi.RemoteException;
        //public void menu2() throws java.rmi.RemoteException;
}