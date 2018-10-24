
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;

public class HelloClient extends UnicastRemoteObject implements Hello_C_I {

    public BufferedReader reader;
    public String Nome;//para mudar 
    public String type;
    public Hello_S_I h;

    HelloClient() throws RemoteException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        this.reader = reader;
        try {
            Hello_S_I h = (Hello_S_I) Naming.lookup("XPTO");
            this.h = h;
        } catch (NotBoundException ex) {
            Logger.getLogger(HelloClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(HelloClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void print_on_client(String s) throws RemoteException {
        //por um if(type|erro) manda para o menu2
        System.out.println("> " + s);
    }

    public void chektipe(String s) throws RemoteException {
        //por um if(type|erro) manda para o menu2
        //this.tyoe=s;
        System.out.println("> " + s);
    }

    public boolean menu1() {
        while (true) {
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
                        if (h.register(username, password, this)) {
                            System.out.println("Registo bem sucedio");
                            //this.menu1();
                        } else {
                            System.out.println("Registo mal sucedio");
                            //this.menu1();
                        }

                        //Nome = username;
                        break;
                    case 2:
                        System.out.println("Username");
                        System.out.print(">");
                        username = reader.readLine();
                        System.out.println("Password");
                        System.out.print(">");
                        password = reader.readLine();
                        if (h.login(username, password, this)) {
                            System.out.println("Login bem sucedio");
                            this.Nome = username;
                            //this.menu2();
                            return true;
                        }

                        break;
                    default:
                        System.out.println("Parece que essa opçao nao existe");
                        //this.menu1();
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(HelloClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void menu2() {
        while (true) {
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
                                            h.insere_Artista(y, genero, descricao, data, Nome, this);
                                            /*if (!h.insere_Artista(y, genero, descricao, data, Nome, this)) {
                                            this.menu2();
                                        }*/
                                            break;
                                        case 2:
                                            //alterar
                                            System.out.print("Nome Do Artista:");
                                            y = reader.readLine();
                                            break;
                                        case 3:
                                            //remover
                                            System.out.print("Nome Do Artista:");
                                            y = reader.readLine();
                                            h.remove_Artista(y, Nome, this);
                                            /*if (!h.remove_Artista(y, Nome, this)) {//devia ser um while i guess
                                            this.menu2();
                                        }*/
                                            break;
                                        default:
                                            System.out.println("Parece que essa opçao nao existe");
                                            this.menu2();
                                            break;
                                    }

                                    break;
                                case 2:
                                    //album
                                    System.out.println("1.Inserir");
                                    System.out.println("2.Alterar");
                                    System.out.println("3.Remover");
                                    x = Integer.parseInt(reader.readLine());
                                    switch (x) {
                                        case 1://inserir
                                            System.out.print("Nome Do Album:");
                                            y = reader.readLine();
                                            //ArrayList<String> musicas = new ArrayList<String>(); 
                                            String musicas="";
                                            String f;
                                            int i = 0;
                                            System.out.println("Musicas(sair para para XD) :");
                                            while (!(f = reader.readLine()).equals("sair")) {
                                                musicas = musicas+"item_" + i + "_name|" + f + ";";
                                                i++;

                                            }
                                            musicas = "item_count|" + i + ";" + musicas;
                                            musicas = musicas.substring(0, musicas.length() - 1);
                                            System.out.print("descriçao do Album:");
                                            String descricao = reader.readLine();
                                            System.out.print("Autor do album:");
                                            String autor = reader.readLine();
                                            System.out.print("Data de lançamenro");
                                            String data = reader.readLine();
                                            System.out.println(h.insere_Album(Nome, y, musicas, descricao, autor, data, this));
                                            // if (!h.insere_Album(Nome, y, musicas, descricao, autor, data, this)) {
                                            //this.menu2();
                                            // }
                                            break;
                                        case 2://alterar album
                                            System.out.print("Nome Da M:");
                                            y = reader.readLine();
                                            break;
                                        case 3://eleminar album
                                            System.out.print("Nome Do Album:");
                                            y = reader.readLine();
                                            h.remove_Album(y, Nome, this);
                                            //if (!h.remove_Album(y, Nome, this)) {//devia ser um while i guess
                                            // this.menu2();
                                            //}
                                            break;
                                        default:
                                            System.out.println("Parece que essa opçao nao existe");
                                            this.menu2();
                                            break;
                                    }
                                    break;
                                case 3:
                                    //musica
                                    System.out.println("1.Inserir");
                                    System.out.println("2.Alterar");
                                    System.out.println("3.Remover");
                                    x = Integer.parseInt(reader.readLine());
                                    switch (x) {
                                        case 1://inserir musica
                                            System.out.print("Nome Do Musica:");
                                            y = reader.readLine();
                                            System.out.print("descriçao da Musica:");
                                            String descricao = reader.readLine();
                                            System.out.print("Autor do album:");
                                            String autor = reader.readLine();
                                            System.out.print("Data de lançamenro");
                                            String data = reader.readLine();
                                            System.out.print("Compositor:");
                                            String compositor = reader.readLine();
                                            System.out.print("Album a que pertence:");
                                            String album = reader.readLine();
                                            h.insere_Musica(Nome, y, autor, compositor, data, album, descricao, this);
                                            /*if (!h.insere_Musica(Nome,y,autor,compositor,data,album,descricao,this)) {
                                            this.menu2();
                                        }*/
                                            
                                            break;
                                        case 2://alterar musica
                                            System.out.print("Nome Da M:");
                                            y = reader.readLine();
                                            break;
                                        case 3://eleminar musica
                                            System.out.print("Nome da Musica:");
                                            y = reader.readLine();
                                            h.remove_Musica(y, Nome, this);
                                            /* if (!h.remove_Musica(y, Nome, this)) {//devia ser um while i guess
                                            this.menu2();
                                        }*/
                                            break;
                                        default:
                                            System.out.println("Parece que essa opçao nao existe");
                                            this.menu2();
                                            break;
                                    }
                                    break;

                            }
                            break;
                        } else {
                           

                        }
                    case 2:
                        System.out.print("Nome da pesquisa:");
                        y = reader.readLine();
                        ArrayList<String> Albuns = new ArrayList<String>();
                        ArrayList<String> Musicas = new ArrayList<String>();
                        ArrayList<String> Artistas = new ArrayList<String>();
                        HashMap<String, String> aux = new HashMap<String, String>();
                        h.Pesquisa_Geral(Albuns, Musicas, Artistas, y, Nome, this);
                        System.out.print("1.Musicas");
                        System.out.print("2.Albuns");
                        System.out.print("3.Artistas");
                        x = Integer.parseInt(reader.readLine());
                        switch (x) {
                            case (1)://Musicas
                                System.out.println("Musicas");
                                System.out.println(Arrays.toString(Musicas.toArray()));

                                break;
                            case (2)://Albuns
                                System.out.println("Albuns");
                                System.out.println(Arrays.toString(Albuns.toArray()));
                                x = Integer.parseInt(reader.readLine());
                                h.Pesquisa_Album(Albuns.get(x), Musicas, Nome, this);
                                System.out.println("Musicas");
                                System.out.println(Arrays.toString(Albuns.toArray()));
                                x = Integer.parseInt(reader.readLine());
                                h.Pesquisa_Musica(Musicas.get(x), Nome, this);
                                break;
                            case (3)://Artistas
                                System.out.println("Artistas");
                                System.out.println(Arrays.toString(Artistas.toArray()));
                                x = Integer.parseInt(reader.readLine());
                                //meter um condiçao para o x nao se javardar
                                h.Pesquisa_Artista(Artistas.get(x),Albuns, Nome, this);
                                System.out.println("Albuns");
                                System.out.println(Arrays.toString(Albuns.toArray()));
                                x = Integer.parseInt(reader.readLine());
                                h.Pesquisa_Album(Albuns.get(x), Musicas, Nome, this);
                                System.out.println("Musicas");
                                System.out.println(Arrays.toString(Albuns.toArray()));
                                x = Integer.parseInt(reader.readLine());
                                h.Pesquisa_Musica(Musicas.get(x), Nome, this);
                                break;
                        }
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
                        exit();
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(HelloClient.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            HelloClient c = new HelloClient();
            c.menu1();
            c.menu2();

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
        }

    }

    public static void String_to_Arrays(String d) {
        //album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
        ArrayList<String> Albuns = new ArrayList<String>();
        ArrayList<String> Musicas = new ArrayList<String>();
        ArrayList<String> Artistas = new ArrayList<String>();
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

    public static HashMap<String, String> String_To_Hash(String s) {

        HashMap<String, String> r = new HashMap<String, String>();
        String[] parts = s.split(";");
        //chave1|valor1;chave2|valor dois
        String[] parts2;
        for (int i = 0; i < parts.length; i++) {
            parts2 = parts[i].split("\\|");
            System.out.println(parts2[0] + "  " + parts2[1]);
            r.put(parts2[0], parts2[1]);
        }
        return r;
    }

}
