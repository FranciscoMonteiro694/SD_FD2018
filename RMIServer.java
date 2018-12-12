/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;

//import static HelloClient.String_To_Hash;
import java.net.UnknownHostException;
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
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface	 {

	private String MULTICAST_ADDRESS = "224.0.224.0";
	private int PORT = 4321;
	public MulticastSocket socket = null;
	public MulticastSocket socket2 = null;
	//public int id;
	public ArrayList<String> m_server;//lista de servers ativos
	public Random rn;

	public RMIServer() throws RemoteException {
		// id = 0;
		try {
			socket2 = new MulticastSocket(PORT);  // create socket and bind it
			InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
			socket2.joinGroup(group);
			socket = new MulticastSocket();
			m_server = new ArrayList<>();
			rn = new Random();
			new check_M(this);
			//new check_M(this);
		} catch (IOException ex) {
			Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	//funcao usada para detetar se os servers rmi estao em baixo
	public void ping() throws RemoteException {
		System.out.println("caralhoooo");

	}

	//funcao que da print na consola do server
	public void print_on_server(String s) throws RemoteException {
		System.out.println("> " + s);
	}

	// funcao que quando o cliente muda de rmi e chamada para manter o login
	public void subscribe(String name) throws RemoteException {
	}

	public boolean login(String name, String password) throws RemoteException {
		//metodo chamado pelo cliente para dar login retorna true se o login e feito com sucesso ou falso se nao.
		int id=rn.nextInt(10000);
		String s = "type|login;username|" + name + ";password|" + password + ";ID|" +id+ ";request|true;";//mensagem conforme o nosso protocolo upp
		String d = send_recive(s,id);//funcao que manda e recebe atravez da socket
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);//mudar de string para aux para ser mais facil reconhecer o pares chave valor
		d = HashToString(aux);
		System.out.println("testeeeeee->>>>>>>>>>>>>>"+d);
		if (aux.get("login_try").equals("sucess")) {
			if (aux.containsKey("notification_count")){
				if (Integer.parseInt(aux.get("notification_count")) > 0) {
				}
			}
			return true;
		}
		return false;
	}

	public synchronized String send_recive(String s,int id) {
		//metodo que envia e recebe um datagrama
		System.out.println("entro no send_recive");
		String t = s;
		while (true) {
			int server = rn.nextInt(m_server.size()) + 1;
			//escolher o server aleatorio com base na lista de servers que e feita pela thread check_m
			System.out.println("Numero aleatorio" + server);
			s = t + "mserverid|" + m_server.get(server - 1);
			System.out.println("Server escolhido " + m_server.get(server - 1));
			try {

				send(this.socket, this.MULTICAST_ADDRESS, this.PORT, s);
				//this.id++;
				String d = recive(this.socket2);
				//envia pacote
				this.socket2.setSoTimeout(10000);
				d = recive(this.socket2);
				System.out.println(d);
				//recebe pacote
				HashMap<String, String> aux = new HashMap<String, String>();
				aux = String_To_Hash(d);
				System.out.println("Teste "+d);
				//uso de hash map para ser mais facil a iteraçao
				System.out.println(aux.containsKey("request"));
				while (Integer.parseInt(aux.get("ID")) != id||aux.containsKey("request")) {//enquanto o id nao for igual ao id da mensagem que mandamos descartamos
					System.out.println(aux.get("ID"));
					d = recive(this.socket2);
					aux = new HashMap<String, String>();
					aux = String_To_Hash(d);
					System.out.println(id + " wow " + aux.get("ID"));
				}

				System.out.println("Saiusss");
				System.out.println(d);
				return d;
			} catch (SocketTimeoutException e1) {
				System.out.println("oleeeeee");
				m_server.remove(m_server.get(server - 1));

			} catch (SocketException e1) {
				System.out.println("Socket closed " + e1);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//funcao que tenta registar uma pessoa
	public boolean register(String name, String password) throws RemoteException {
		System.out.println("ole");
		//mensagem para registar clientes
		int id=rn.nextInt(10000);
		String s = "type|register;username|" + name + ";password|" + password + ";ID|" + Integer.toString(id) + ";request|true;";
		String d = send_recive(s,id);//uso da funcao acima descrita
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("regist_try").equals("sucess")) {
			return true;
		}
		return false;
	}

	//funcao para saber se o cliente que a chamou tem ou nao permissoes de editor
	public boolean is_Editor(String name) throws RemoteException {
		//
		int id=rn.nextInt(10000);
		System.out.println("A verificar se e editor " + name);
		String s = "type|request_permission_gerir;username|" + name + ";ID|" + Integer.toString(id) + ";request|true;";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("acess").equals("granted")) {
			return true;
		}
		return false;
	}

	//funcao que insere um artista
	public boolean insere_Artista(String name, String genero, String descricao, String data, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A inserir artista " + name);
		String s = "type|inserir_artista;username|" + username + ";artista_name|" + name + ";artista_genero|" + genero + ";artista_data|" + data + ";artista_descricao|" + descricao + ";ID|" + Integer.toString(id) + ";request|true;";
		String d = send_recive(s,id);
		HashMap<String, String> aux;
		aux = String_To_Hash(d);
		d = HashToString(aux);
		System.out.println("Antes");
		System.out.println("Oleeeee");
		if (aux.get("insere_artista_try").equals("sucess")) {
			return true;
		}
		return false;

	}

	public boolean insere_Grupo(String name,String genero,String descricao,String data,String artistas,String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A inserir grupo " + name);
		String s = "type|inserir_grupo;username|" + username + ";artista_name|" + name + ";artista_genero|" + genero + ";artista_data|" + data + ";artista_descricao|" + descricao + ";ID|" + Integer.toString(id) + ";request|true;"+artistas;
		String d = send_recive(s,id);
		HashMap<String, String> aux;
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("insere_artista_try").equals("sucess")) {
			return true;
		}
		return false;

	}
	public boolean insere_Musico(String name,String genero,String descricao,String data,String data_nascimento,String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A inserir musico " + name);
		String s = "type|inserir_musico;username|" + username + ";artista_name|" + name + ";artista_genero|" + genero + ";artista_data|" + data + ";artista_descricao|" + descricao + ";ID|" + Integer.toString(id) + ";request|true;"+"musico_datanascimento|"+data_nascimento+";";
		String d = send_recive(s,id);
		HashMap<String, String> aux;
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("insere_artista_try").equals("sucess")) {
			return true;
		}
		return false;

	}

	//fucano que remove o artista mudar isto
	public boolean remove_Musico(String name, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A remover artista " + name);
		String s = "type|remover_artista;username|" + username + ";artista_name|" + name + ";ID|" + Integer.toString(id) + ";request|true;";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("remove_artista_try").equals("sucess")) {
			return true;
		}
		return false;
	}
	//fucano que remove o artista
	public boolean altera_Musico(String name,String data,String descricao,String genero, String username) throws RemoteException {
		System.out.println("wtffffff");
		int id=rn.nextInt(10000);
		System.out.println("A remover artista " + name);
		String s = "type|editar_musico;username|" + username + ";musico_nome|" + name + ";ID|" + Integer.toString(id) + ";request|true;"+"musico_data|"+data+";"+"musico_descricao|"+descricao+";"+"musico_genero|"+genero+";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("remove_artista_try").equals("sucess")) {
			return true;
		}
		return false;
	}

	//funcao que insere o album
	public boolean insere_Album(String username, String nome, String descricao, String autor, String data) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A inserir album" + nome);
		String s = "type|inserir_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "album_name|" + nome + ";album_descricao|" + descricao + ";album_data|" + data + ";artista_name|" + autor + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		System.out.println("Antes");
		if (aux.get("insere_album_try").equals("sucess")) {
			return true;
		}
		return false;
	}

	//fucao que remove um album
	public boolean remove_Album(String artista,String name, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A inserir album" + name);
		String s = "type|remover_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "album_name|" + name + ";"+"artista_name|" + artista + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		// d = HashToString(aux);
		if (aux.get("remove_album_try ").equals("sucess")) {
			return true;
		}
		return false;
	}

	public boolean altera_Album(String name,String artista, String username, String nova_des, String data)  throws RemoteException {
		//System.out.println("A inserir album" + name);
		int id=rn.nextInt(10000);
		String s = "type|editar_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "album_name|" + name + ";album_descricao|" + nova_des + ";album_data|" + data + ";"+"artista_name|" + artista + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.containsKey("user_count")) {
			for (int i = 0; i < Integer.parseInt(aux.get("user_count")); i++) {
				//mandar a notificacao para quem ja alterou o album
				Notification(this, aux.get("user_" + i + "_name"), aux.get("notification"));
			}

		}
		return false;
	}

	//funcao que insere musica
	public boolean insere_Musica(String username, String name, String autor, String compositor, String data, String album, String descricao) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println(username);
		System.out.println("A inserir album" + name);
		String s = "type|inserir_musica;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "musica_name|" + name + ";musica_compositor|" + compositor + ";artista_name|" + autor + ";musica_data|" + data + ";album_name|" + album + ";musica_descricao|" + descricao + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("insere_musica_try").equals("sucess")) {
			return true;
		}
		return false;
	}

	public boolean remove_Musica(String artista, String album, String name, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("cenas" + name);
		String s = "type|remover_musica;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "musica_name|" + name + ";"+"artista_name|" + artista + ";"+"album_name|" + album + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("remove_musica_try").equals("sucess")) {
			return true;
		}
		return false;
	}
	public boolean altera_Musica(String artista,String album,String name,String compositor ,String descricao,String username)throws RemoteException{
		int id=rn.nextInt(10000);
		System.out.println("A editar_musica" + name);
		String s = "type|editar_musica;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "musica_name|" + name + ";"+"musica_compositor|" + compositor + ";"+"musica_descricao|" + descricao + ";"+"artista_name|" + artista + ";"+"album_name|" + album + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("editar_musica_try").equals("sucess")) {
			return true;
		}
		return false;
	}

	//pesquisa que devolve todos os albuns artistas ou musicas que contem a string dada
	public String Pesquisa_Geral(ArrayList<String> Albuns, ArrayList<String> Musicas, ArrayList<String> Artistas, String pesq, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A procurar " + pesq);
		String s = "type|pesquisar;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "search_key|" + pesq + ";";
		String d = send_recive(s,id);
		String_to_Arrays(d, Albuns, Musicas, Artistas);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}

	//pequisa a musica e devolve os detalhes da musica
	public String Pesquisa_Musica(String pesq, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		String s = "type|get_musica;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "musica_name|" + pesq + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		// String e = "Nome da musica:" + aux.get("musica_name") + "\n" + "Album:" + aux.get("musica_album") + "\n" + "Compositor:" + aux.get("musica_compositor") + "\n" + "Descricao:" + aux.get("musica_descricao");
		return d;

	}

	public String Pesquisa_Album(String pesq, ArrayList<String> Musicas, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A procurar " + pesq);
		String s = "type|get_album;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "album_name|" + pesq + ";";//ou serch key
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		String e = "Nome do album:" + aux.get("album_name") + "\n" + "Autor do Album:" + aux.get("album_autor") + "\n" + "Data do album:" + aux.get("album_data");
		d = HashToString(aux);
		return d;
	}

	public String Pesquisa_Artista(String pesq, ArrayList<String> Albuns, String username) throws RemoteException {
		int id=rn.nextInt(10000);
		System.out.println("A procurar " + pesq);
		String s = "type|get_artista;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "artista_name|" + pesq + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		String e = "Nome do artista:" + aux.get("artista_name") + "\n" + "Genero do artista:" + aux.get("artista_genero") + "\n" + "Data de nascimento:" + aux.get("artista_data");
		return d;

	}

	public void Make_Editor(String username, String target) throws RemoteException {
		int id=rn.nextInt(10000);
		String s = "type|make_editor;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "editor_name|" + target + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		if (aux.get("msg").equals("Permissoes atualizadas!")) {
			Notification(this, target, "Es editor o mano");//ver isto melhor

		}
	}

	public void Escreve_Critica(String username, String critica, int pont,String artista, String album) throws RemoteException {
		int id=rn.nextInt(10000);
		String s = "type|write_review;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "review_critica|" + critica + ";review_pontuacao|" + pont + ";album_name|" + album + ";"+"artista_name|"+artista+";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
	}
	public String criarPlaylist(String username, String nome_playlist,String tipo) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|make_playlist;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "playlist_name|" + nome_playlist + ";playlist_type|" + tipo + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}

	public String sharePlaylist(String username, String nome_playlist,String target) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|share_playlist;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "playlist_name|" + nome_playlist + ";playlist_target|" +target + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}
	public String getPlaylist(String username, String nome_playlist,String criador) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|get_playlist;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "playlist_name|" + nome_playlist + ";playlist_criador|" +criador+ ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}
	public String adicionaMusicaPlaylist(String username, String nome_playlist,String artista,String album,String musica) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|musica_playlist;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "playlist_name|" + nome_playlist + ";artista_name|" +artista + ";"+"album_name|" +album + ";"+"musica_name|" +musica + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}
	public String criarConcerto(String username, String local_concerto,String nome,String local_data) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|make_concerto;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "concerto_name|" + nome + ";concerto_local|" +local_concerto + ";"+"concerto_data|" +local_data + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}

	public String adicionaArtistaConcerto(String username, String local_concerto,String nome,String local_data,String artista) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|artista_concerto;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "concerto_name|" + nome + ";concerto_local|" +local_concerto + ";"+"concerto_data|" +local_data + ";"+"artista_name|" +artista + ";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}

	public String adicionaMusicaConcerto(String username, String local_concerto,String nome,String local_data,String artista,String album,String musica) throws RemoteException{
		int id=rn.nextInt(10000);
		String s = "type|musica_concerto;username|" + username + ";ID|" + Integer.toString(id) + ";request|true;" + "concerto_name|" + nome + ";concerto_local|" +local_concerto + ";"+"concerto_data|" +local_data + ";"+"artista_name|" +artista + ";"+"album_name|"+album+";"+"musica_name|"+musica+";";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);
		return d;
	}

	public String Upload_Musica(String username, String nome_musica) throws RemoteException {
		int id=rn.nextInt(10000);
		String s = "type|upload_music;username|" + username  +";musica_name|"+nome_musica+";ID|" + Integer.toString(id) + ";request|true;";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);//uma beca estupido
		return d;
	}
	public String Transfer_Musica(String username, String nome_musica) throws RemoteException {
		int id=rn.nextInt(10000);
		String s = "type|transfer_musica;username|" + username  +";musica_name|"+nome_musica+";ID|" + Integer.toString(id) + ";request|true;";
		String d = send_recive(s,id);
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		d = HashToString(aux);//uma beca estupido
		return d;
	}

	public static String HashToString(HashMap<String, String> s) {
		String r = "";
		s.remove("ID");
		s.remove("request");
		Set set = s.entrySet();
		System.out.println("Cenasssssssss");
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
			System.out.print("key is: " + mentry.getKey() + " & Value is: ");
			System.out.println(mentry.getValue());
			r = r + mentry.getKey() + "|" + mentry.getValue() + ";";
		}
		r = r.substring(0, r.length() - 1);
		return r;
	}

	// =======================================================
	public static void main(String args[]) {
		try {

			RMIServerInterface s = new RMIServer();
			LocateRegistry.createRegistry(1099).rebind("server", s);
			System.out.println("Server ready...");


		} catch (Exception re) {
			System.out.println("Exception in HelloImpl.main: " + re);
		}
	}

	public static void Notification(RMIServer s, String username, String notification) {

	}

	public static void String_to_Arrays(String d, ArrayList<String> Albuns, ArrayList<String> Musicas, ArrayList<String> Artistas) {
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

	}

	public static void String_to_Arrays_Albuns(String d, ArrayList<String> Albuns) {
		//album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		if (aux.containsKey("album_count")) {
			for (int i = 0; i < Integer.parseInt(aux.get("album_count")); i++) {
				System.out.println("album_" + i + "_name");
				Albuns.add(aux.get("album_" + i + "_name"));
			}
		}
	}

	public static void String_to_Arrays_Artistas(String d, ArrayList<String> Artistas) {
		//album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		if (aux.containsKey("artista_count")) {
			for (int i = 0; i < Integer.parseInt(aux.get("artista_count")); i++) {
				System.out.println("artista_" + i + "_name");
				Artistas.add(aux.get("artista_" + i + "_name"));
			}
		}

	}

	public static void String_to_Arrays_Musicas(String d, ArrayList<String> Musicas) {
		//album_count|3;item_0_name|Gotta Jazz ;item_1_name|Boogie Woogie;item_1_name|Boogie Woogie
		HashMap<String, String> aux = new HashMap<String, String>();
		aux = String_To_Hash(d);
		if (aux.containsKey("musica_count")) {
			for (int i = 0; i < Integer.parseInt(aux.get("musica_count")); i++) {
				System.out.println("musica_" + i + "_name");
				Musicas.add(aux.get("musica_" + i + "_name"));
			}
		}
	}

	//ta mal
	public static void send(MulticastSocket socket, String MULTICAST_ADDRESS, int PORT, String s) {
		try {
			byte[] buffer = s.getBytes();
			InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);//adicionar port
			socket.send(packet);

		} catch (IOException e) {
			e.printStackTrace();
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

	public static String recive(MulticastSocket socket) throws IOException {
		byte[] buffer = new byte[256];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		socket.receive(packet);

		String message = new String(packet.getData(), 0, packet.getLength());
		HashMap<String, String> aux = String_To_Hash(message);
		System.out.println(message);
		if (aux.containsKey("request")) {
			return message;
		} else {
			return message;
		}

	}
}

class check_M implements Runnable {

	public RMIServer s;
	public Thread t;
	private int PORT = 4322;
	private String MULTICAST_ADDRESS = "224.0.224.0";

	check_M(RMIServer s) {
		this.s = s;
		t = new Thread(this);
		t.start();
	}

	public void run() {
		MulticastSocket socket = null;
		try {
			socket = new MulticastSocket(PORT);  // create socket and bind it

			InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
			socket.joinGroup(group);
			while (true) {
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String message = new String(packet.getData(), 0, packet.getLength());
				System.out.println("message");
				HashMap<String, String> aux = String_To_Hash(message);
				if (!s.m_server.contains(aux.get("idserver"))) {
					s.m_server.add(aux.get("idserver"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
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
}

class Back_up implements Runnable {

	public Thread t;
	public int excecoes;
	long start;
	RMIServer ser;
	private int PORT = 4321;
	private String MULTICAST_ADDRESS = "224.0.224.0";
	int tes = 0;

	public Back_up(RMIServer h) {
		this.ser = h;
		System.out.println("Criando thread");
		excecoes = 0;
		t = new Thread(this);//nao sei se e bem isto
		this.start = System.currentTimeMillis();
		t.start();

	}

	public void run() {
		//System.out.println("Checker a correr");
		long elapsedTime;
		//System.out.println(start);
		while ((elapsedTime = System.currentTimeMillis() - start) < 25000) {
			// System.out.println(System.currentTimeMillis() - start);
			while (true) {
				//System.out.println(System.currentTimeMillis() - start);
				try {
					sleep(100);
					// System.out.println("<------>");
					RMIServerInterface h = (RMIServerInterface) Naming.lookup("Primario");
					h.ping();
					if (tes == 2) {
						//InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
						//ser.socket2.leaveGroup(group);
						//ser.socket2 = null;
						System.out.println("pricipal voltou");
					}
					tes = 1;
					this.start = System.currentTimeMillis();
					//System.out.println("o primario ligouse outra vez");
					//excecoes=0;
				} catch (Exception e) {
					if (tes == 1) {
						System.out.println("principal down");
						//try {
						tes = 2;
						//System.out.println("ta desligado");
						//System.out.println("A ligarse ao multicast");
						//InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
						//ser.socket2 = new MulticastSocket(PORT);
						//ser.socket2.joinGroup(group);
						// } catch (IOException ex) {
						//  Logger.getLogger(Back_up.class.getName()).log(Level.SEVERE, null, ex);
						//}
					}
					//System.out.println("Mais uma");
					break;
					//} catch (IOException ex) {
					//Logger.getLogger(Back_up.class.getName()).log(Level.SEVERE, null, ex);
					// }

				}
			}
		}
		System.out.println("Saiu");
	}

}
