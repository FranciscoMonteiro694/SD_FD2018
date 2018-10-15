import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class HelloServer extends UnicastRemoteObject implements Hello_S_I {
	static public ArrayList<Hello_C_I> todos_clientes = new ArrayList<Hello_C_I>();

	public HelloServer() throws RemoteException {
		super();
	}

	public void print_on_server(String s) throws RemoteException {
		System.out.println("> " + s);
	}
	public void subscribe(String name, Hello_C_I c) throws RemoteException {
		System.out.println("Subscribing " + name);
		System.out.print("> ");
		todos_clientes.add(c);
	}
	public void register(String name,String username,Hello_C_I c)throws RemoteException{
		//supostamente e para mandar por multicast com udp mas pronto
		c.print_on_client("type | s t a t u s ; logged | on ; msg | Welcome to DropMusic");

	}

	// =======================================================

	public static void main(String args[]) {
		String a;
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		try {
			//User user = new User();
                        LocateRegistry.createRegistry(1099);
			HelloServer h = new HelloServer();
			Naming.rebind("XPTO", h);
			System.out.println("Hello Server ready.");
			while (true) {
				System.out.print("> ");
				a = reader.readLine();
				for (int i=0; i<todos_clientes.size(); i++){
                                    todos_clientes.get(i).print_on_client(a);
				}
                        }
		} catch (Exception re){
			System.out.println("Exception in HelloImpl.main: " + re);
		} 
	}
}