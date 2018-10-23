
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloClient extends UnicastRemoteObject implements Hello_C_I {

    public BufferedReader reader;
    public String Nome;//para mudar 

    HelloClient() throws RemoteException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        this.reader = reader;
    }

    public void print_on_client(String s) throws RemoteException {
        //por um if(type|erro) manda para o menu2
        System.out.println("> " + s);
    }

    public void menu1(Hello_S_I h) {
        System.out.println("1.Registar");
        System.out.println("2.Login");
        System.out.print(">");
        try {
            int x = Integer.parseInt(reader.readLine());
            //System.out.println(x);
            switch (x) {
                case 1:
                    System.out.println("Username");
                    System.out.print(">");
                    String username = reader.readLine();
                    System.out.println("Password");
                    System.out.print(">");
                    String password = reader.readLine();
                    if (!h.register(username, password, this)) {
                        this.menu1(h);
                    }
                    Nome = username;
                    this.menu1(h);
                    break;
                case 2:
                    System.out.println("Username");
                    System.out.print(">");
                    username = reader.readLine();
                    System.out.println("Password");
                    System.out.print(">");
                    password = reader.readLine();
                    if (!h.login(username,password, this)) {
                        this.menu1(h);
                    }
                    Nome=username;
                    break;
                default:
                    System.out.println("Parece que essa opçao nao existe");
                    this.menu1(h);
                    break;
            }/*
                if(x==1){
                    System.out.println("Username");
                    System.out.print(">");
                    String username=reader.readLine();
                    System.out.println("Password");
                    System.out.print(">"); 
                    String password=reader.readLine();
                    h.register(username,password, this);   
                }
                else{
                    if(x==2){
                        System.out.println("Menos mal");
                        if(h.login("Duarte","password", this)){
                            System.out.println("Nice");
                        }
                        else{
                            System.out.println("Parece que o login falhou");
                            this.menu1(h);
                        }
                    }
                }
             */ } catch (IOException ex) {
            Logger.getLogger(HelloClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void menu2(Hello_S_I h) {
        System.out.println("1.Gerir artistas, álbuns e músicas.");
        System.out.println("2.Pesquisar músicas.");
        System.out.println("3.Consultar detalhes sobre álbum e sobre artista.");
        System.out.println("4.Escrever crítica a um álbum.");
        System.out.println("5.Dar privilégios de editor a um utilizador.");
        System.out.println("6.Transferência de músicas para o servidor.");
        System.out.println("7.Partilha de ficheiros musicais.");
        System.out.println("8.Transferência de músicas do servidor para os utilizadores.");
        System.out.print(">");
        String y;
        try {
            int x = Integer.parseInt(reader.readLine());
            switch (x) {
                case 1:
                    if (h.is_Editor(Nome, this)) {
                        System.out.println("1.Gerir Artistas");
                        System.out.println("2.Gerir Albums");
                        System.out.println("3.Gerir Musicas");
                        System.out.print(">");
                        x = Integer.parseInt(reader.readLine());
                        switch (x) {
                            case 1:
                                System.out.println("1.Inserir");
                                System.out.println("2.Alterar");
                                System.out.println("3.Remover");
                                x = Integer.parseInt(reader.readLine());
                                switch (x) {
                                    case 1:
                                        System.out.print("Nome Do Artista:");
                                        y = reader.readLine();
                                        System.out.print("Genero do artista:");
                                        String genero = reader.readLine();
                                        System.out.print("Discriçao do artista:");
                                        String descricao = reader.readLine();
                                        System.out.print("Data de nascimento:");
                                        String data = reader.readLine();
                                        if (!h.insere_Artista(y, genero, descricao, data, Nome, this)) {
                                            this.menu2(h);
                                        }
                                        break;
                                    case 2:
                                        System.out.print("Nome Do Artista:");
                                        y = reader.readLine();
                                        break;
                                    case 3:
                                        System.out.print("Nome Do Artista:");
                                        y = reader.readLine();
                                        if (!h.remove_Artista(y, Nome, this)) {//devia ser um while i guess
                                            this.menu2(h);
                                        }
                                        break;
                                    default:
                                        System.out.println("Parece que essa opçao nao existe");
                                        this.menu2(h);
                                        break;
                                }

                                break;
                            case 2:
                                System.out.println("1.Inserir");
                                System.out.println("2.Alterar");
                                System.out.println("3.Remover");
                                x = Integer.parseInt(reader.readLine());
                                switch (x) {
                                    case 1:
                                        System.out.print("Nome Do Album:");
                                        y = reader.readLine();
                                        System.out.print("Genero do artista:");
                                        String genero = reader.readLine();
                                        System.out.print("Discriçao do artista:");
                                        String descricao = reader.readLine();
                                        System.out.print("Data de nascimento:");
                                        String data = reader.readLine();
                                        if (!h.insere_Artista(y, genero, descricao, data, Nome, this)) {
                                            this.menu2(h);
                                        }
                                        break;
                                    case 2:
                                        System.out.print("Nome Do Artista:");
                                        y = reader.readLine();
                                        break;
                                    case 3:
                                        System.out.print("Nome Do Artista:");
                                        y = reader.readLine();
                                        if (!h.remove_Artista(y, Nome, this)) {//devia ser um while i guess
                                            this.menu2(h);
                                        }
                                        break;
                                    default:
                                        System.out.println("Parece que essa opçao nao existe");
                                        this.menu2(h);
                                        break;
                                }
                                break;
                            case 3:
                                System.out.println("1.Inserir");
                                System.out.println("2.Alterar");
                                System.out.println("3.Remover");
                                break;

                        }
                    } else {
                        this.menu2(h);
                    }

                    /*
                    switch (x) {
                        case 1:
                            System.out.println("1.Inserir");
                            System.out.println("2.Alterar");
                            System.out.println("3.Remover");
                            x = Integer.parseInt(reader.readLine());
                            switch (x) {
                                case 1:
                                    break;
                            }
                            System.out.print("Nome Do Artista:");
                            y = reader.readLine();
                            break;
                    }*/
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;

                default:
                    System.out.println("Parece que essa opçao nao existe");
                    this.menu2(h);
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(HelloClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String args[]) {
        String a;
        // usage: java HelloClient username
        //System.getProperties().put("java.security.policy", "policy.all");
        //System.setSecurityManager(new RMISecurityManager());
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try {
            //User user = new User();
            Hello_S_I h = (Hello_S_I) Naming.lookup("XPTO");
            HelloClient c = new HelloClient();
            //h.subscribe(args[0], (Hello_C_I) c);
            //System.out.println("Client sent subscription to server");
            //RMI_Backup t = new RMI_Backup("XPTO");

            //h.register("Duarte","password",c);
            c.menu1(h);
            c.menu2(h);
            // t.t.join();
            /*while (true) {
				System.out.print("> ");
				a = reader.readLine();
				h.print_on_server(a);
			}*/

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }

    }

}
