
//import static HelloClient.String_To_Hash;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import static java.lang.Thread.sleep;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloServer extends UnicastRemoteObject implements Hello_S_I {

    static public ArrayList<Hello_C_I> todos_clientes = new ArrayList<Hello_C_I>();//mudar para hashmap
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    public MulticastSocket socket = null;
    public MulticastSocket socket2 = null;
    public HashMap<Integer, Hello_C_I> queue;//adicionar aqui o username
    public HashMap<String, Hello_C_I> online;//para notificaçoes etc
    public int id;

    public HelloServer() throws RemoteException {
        id = 0;
        try {
            socket2 = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket2.joinGroup(group);
            socket = new MulticastSocket();
        } catch (IOException ex) {
            Logger.getLogger(HelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        queue = new HashMap<Integer, Hello_C_I>();

    }

    public void envia() {

    }

    public void print_on_server(String s) throws RemoteException {
        System.out.println("> " + s);
        //wait();
    }
//para apagar

    public void subscribe(String name, Hello_C_I c) throws RemoteException {
        System.out.println("Subscribing " + name);
        System.out.print("> ");
        todos_clientes.add(c);
    }

    public boolean login(String name, String password, Hello_C_I c) throws RemoteException {
        System.out.println("A dar login " + name);
        String s = "type|login;username|" + name + ";password|" + password + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1";
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("login_try").equals("sucess")) {
            return true;
        }
        return false;
    }

    public String send_recive(String s) {
        int id = this.id;
        send(this.socket, this.MULTICAST_ADDRESS, this.PORT, s);
        this.id++;
        String d = recive(this.socket2);
        d = recive(this.socket2);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        System.out.println(Integer.parseInt(aux.get("ID")));
        while (Integer.parseInt(aux.get("ID")) != id) {
            System.out.println("Discarda");
            d = recive(this.socket2);
            aux = new HashMap<String, String>();
            aux = String_To_Hash(d);
        }
        System.out.println("Encotramos O nosso packet");
        //d = HashToString(aux);
        return d;
    }

    public boolean register(String name, String password, Hello_C_I c) throws RemoteException {
        System.out.println("A registar " + name);
        String s = "type|register;username|" + name + ";password|" + password + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1";
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("regist_try").equals("sucess")) {
            return true;
        }
        return false;
    }

    public boolean is_Editor(String name, Hello_C_I c) throws RemoteException {
        System.out.println("A verificar se e editor " + name);
        String s = "type|request_permission_gerir;username|" + name + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1";
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("acess").equals("granted")) {
            return true;
        }
        return false;
    }

    public boolean insere_Artista(String name, String genero, String descricao, String data, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A inserir artista " + name);
        String s = "type|inserir_artista;username|" + username + ";artista_name|" + name + ";artista_genero|" + genero + ";artista_data|" + data + ";artista_descricao|" + descricao + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1";
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("insere_artista_try").equals("sucess")) {
            return true;
        }
        return false;

    }

    public boolean remove_Artista(String name, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A remover artista " + name);
        String s = "type|remover_artista;username|" + username + ";artista_name|" + name + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1";
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("remove_artista_try").equals("sucess")) {
            return true;
        }
        return false;
    }

    public boolean insere_Album(String username, String nome, String musicas, String descricao, String autor, String data, Hello_C_I c) throws RemoteException {
        System.out.println("A inserir album" + nome);
        String s = "type|inserir_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "album_name|" + nome + ";album_descricao|" + descricao + ";album_data|" + data + ";album_autor|" + autor + ";" + musicas;
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        System.out.println("Antes");
        if (aux.get("insere_album_try").equals("sucess")) {
            return true;
        }
        return false;
    }

    public boolean remove_Album(String name, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A inserir album" + name);
        String s = "type|remover_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "album_name|" + name;
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("remove_album_try ").equals("sucess")) {
            return true;
        }
        return false;
    }

    public boolean insere_Musica(String username, String name, String autor, String compositor, String data, String album, String descricao, Hello_C_I c) throws RemoteException {
        System.out.println("A inserir album" + name);
        String s = "type|inserir_musica;name|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "musica_name|" + name+";musica_compositor|"+compositor+";musica_autor|"+autor+";musica_data|"+data+";musica_album|"+album+";musica_descricao|"+descricao;
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
        if (aux.get("insere_musica_try ").equals("sucess")) {
            return true;
        }
        return false;
    }

    public boolean remove_Musica(String name, String username, Hello_C_I c) throws RemoteException {
        return true;
    }

    public void Pesquisa_Geral(ArrayList<String> Albuns, ArrayList<String> Musicas, ArrayList<String> Artistas, String pesq, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A procurar " + pesq);
        String s = "type|whole_list;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "search_key|" + pesq;
        String d = send_recive(s);
        String_to_Arrays(d,Albuns,Musicas,Artistas);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);
        c.print_on_client(d);
    }

    public void Pesquisa_Musica(String pesq, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A procurar " + pesq);
        String s = "type|get_musica;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "musica_name|" + pesq;
        String d = send_recive(s);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);

    }

    public void Pesquisa_Album(String pesq,ArrayList<String> Musicas, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A procurar " + pesq);
        String s = "type|get_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "album_name|" + pesq;//ou serch key
        String d = send_recive(s);
        String_to_Arrays_Musicas(pesq,Musicas);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);

    }

    public void Pesquisa_Artista(String pesq,ArrayList<String> Albuns, String username, Hello_C_I c) throws RemoteException {
        System.out.println("A procurar " + pesq);
        String s = "type|get_artista;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1;" + "artista_name|" + pesq;
        String d = send_recive(s);
        String_to_Arrays_Albuns(pesq,Albuns);
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        d = HashToString(aux);

    }

    public static String HashToString(HashMap<String, String> s) {
        String r = "";
        s.remove("ID");
        s.remove("request");
        Set set = s.entrySet();
        System.out.println("Cenasssssssss");
        Iterator iterator = set.iterator();
        //chave1|valor1;ID|1
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
            r = r + mentry.getKey() + "|" + mentry.getValue() + ";";
        }
        r = r.substring(0, r.length() - 1);
        return r;
    }

    public static HashMap<String, String> String_To_Hash(String s) {

        HashMap<String, String> r = new HashMap<String, String>();
        String[] parts = s.split(";");
        //chave1|valor1;chave2|valor dois
        String[] parts2;
        for (int i = 0; i < parts.length; i++) {
            parts2 = parts[i].split("\\|");
            r.put(parts2[0], parts2[1]);
        }
        return r;
    }

    public void getQueue() {
        Set set = queue.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }

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
            // new Handler_Response(h.queue);
            //new RMI_Backup();

        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }

    public static void String_to_Arrays(String d,ArrayList<String> Albuns,ArrayList<String> Musicas ,ArrayList<String> Artistas) {
        //album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        if (aux.containsKey("album_count")) {
            for (int i = 0; i < Integer.parseInt(aux.get("album_count")); i++) {
                System.out.println("album_" + i + "_name");
                Albuns.add(aux.get("album_" + i + "_name"));
            }
        }
        if (aux.containsKey("artista_count")) {
            for (int i = 0; i < Integer.parseInt(aux.get("artista_count")); i++) {
                System.out.println("artista_" + i + "_name");
                Artistas.add(aux.get("artista_" + i + "_name"));
            }
        }
        if (aux.containsKey("musica_count")) {
            for (int i = 0; i < Integer.parseInt(aux.get("musica_count")); i++) {
                System.out.println("musica_" + i + "_name");
                Musicas.add(aux.get("musica_" + i + "_name"));
            }
        }
        System.out.println("Albuns");
        System.out.println(Arrays.toString(Albuns.toArray()));
        System.out.println("Musicas");
        System.out.println(Arrays.toString(Musicas.toArray()));
        System.out.println("Artistas");
        System.out.println(Arrays.toString(Artistas.toArray()));

    }
     public static void String_to_Arrays_Albuns(String d,ArrayList<String> Albuns) {
        //album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        if (aux.containsKey("album_count")) {
            for (int i = 0; i < Integer.parseInt(aux.get("album_count")); i++) {
                System.out.println("album_" + i + "_name");
                Albuns.add(aux.get("album_" + i + "_name"));
            }
        }
        
        System.out.println("Albuns");
        System.out.println(Arrays.toString(Albuns.toArray()));
        System.out.println("Musicas");
    }
        public static void String_to_Arrays_Artistas(String d,ArrayList<String> Artistas) {
        //album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        if (aux.containsKey("artista_count")) {
            for (int i = 0; i < Integer.parseInt(aux.get("artista_count")); i++) {
                System.out.println("artista_" + i + "_name");
                Artistas.add(aux.get("artista_" + i + "_name"));
            }
        }
        System.out.println("Artistas");
        System.out.println(Arrays.toString(Artistas.toArray()));

    }
        
        public static void String_to_Arrays_Musicas(String d,ArrayList<String> Musicas) {
        //album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
        HashMap<String, String> aux = new HashMap<String, String>();
        aux = String_To_Hash(d);
        if (aux.containsKey("musica_count")) {
            for (int i = 0; i < Integer.parseInt(aux.get("musica_count")); i++) {
                System.out.println("musica_" + i + "_name");
                Musicas.add(aux.get("musica_" + i + "_name"));
            }
        }
        System.out.println("Musicas");
        System.out.println(Arrays.toString(Musicas.toArray()));

    }

    //ta mal
    public static void send(MulticastSocket socket, String MULTICAST_ADDRESS, int PORT, String s) {
        try {
            byte[] buffer = s.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 4321);//adicionar port
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*finally {
            socket.close();
        }*/

    }

    public static String recive(MulticastSocket socket) {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);

        } catch (IOException ex) {
            Logger.getLogger(HelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
        String message = new String(packet.getData(), 0, packet.getLength());
        HashMap<String, String> aux = String_To_Hash(message);
        System.out.println(message);
        if (aux.containsKey("request")) {
            System.out.println("Javardice");
            return "";
        } else {
            System.out.println("Niceeee");
            return message;
        }

    }
}

/*class Handler_Response implements Runnable {

    public HashMap<Integer, Hello_C_I> queue;
    public MulticastSocket socket;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public Handler_Response(HashMap<Integer, Hello_C_I> queue) {
        this.queue = queue;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            new Thread(this, "Handler_Response").start();
        } catch (Exception e) {
            System.out.println("uppssss");
        }

    }

    public void run() {
        System.out.println("Thread ta a correr e a escuta");
        while (true) {
            try {
                System.out.println("--------------------");
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
                HashMap<String, String> aux;
                aux = String_To_Hash(message);
                if (aux.containsKey("request")) {
                    System.out.println("Javardice");
                } else {
                    int id = Integer.parseInt(aux.get("ID"));
                    System.out.println("Id " + id);
                    synchronized (queue) {
                        System.out.println("Antes");
                        queue.get(id).print_on_client(message);
                        System.out.println("Depois");

                        queue.remove(id);
                        Set set = queue.entrySet();
                        Iterator iterator = set.iterator();
                        //chave1|valor1;ID|1
                        //System.out.println("Ta vazio");
                        System.out.println("Antes do interator");
                        while (iterator.hasNext()) {
                            Map.Entry mentry = (Map.Entry) iterator.next();
                            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
                            System.out.println(mentry.getValue());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static HashMap<String, String> String_To_Hash(String s) {

        HashMap<String, String> r = new HashMap<String, String>();
        String[] parts = s.split(";");
        //chave1|valor1;chave2|valor dois
        String[] parts2;
        for (int i = 0; i < parts.length; i++) {
            parts2 = parts[i].split("\\|");
            r.put(parts2[0], parts2[1]);
        }
        return r;
    }

    public static String HashToString(HashMap<String, String> s) {
        String r = "";
        s.remove("ID");
        s.remove("request");
        Set set = s.entrySet();
        Iterator iterator = set.iterator();
        //chave1|valor1;ID|1
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
            r = r + mentry.getKey() + "|" + mentry.getValue() + ";";
        }
        r = r.substring(0, r.length() - 1);
        return r;
    }

}

class Handler_Request implements Runnable {

    public HashMap<Integer, Hello_C_I> queue;
    public MulticastSocket socket;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public Handler_Request(HashMap<Integer, Hello_C_I> queue) {
        this.queue = queue;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            new Thread(this, "Handler_Response").start();
        } catch (Exception e) {
            System.out.println("uppssss");
        }

    }

    public void run() {
        while (true) {
            if (queue.isEmpty() == true) {
                try {
                    sleep(300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Handler_Request.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Iterator it = queue.entrySet().iterator();
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                it.remove();

            }
        }

    }
 */
