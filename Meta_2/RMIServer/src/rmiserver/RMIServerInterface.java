/**
 * Raul Barbosa 2014-11-07
 */
package rmiserver;
import java.rmi.*;
import java.util.ArrayList;

public interface RMIServerInterface extends Remote {
	public void print_on_server(String s) throws java.rmi.RemoteException;

	public void subscribe(String name) throws RemoteException;

	public boolean register(String name, String password) throws RemoteException;

	public boolean login(String name, String password) throws RemoteException;

	public boolean is_Editor(String name) throws RemoteException;

	// public boolean insere_Artista(String name,String genero,String descricao,String data,String username,Hello_C_I c)throws RemoteException;
	public boolean insere_Musico(String name, String genero, String descricao, String data, String data_nascimento, String username) throws RemoteException;

	public boolean insere_Grupo(String name, String genero, String descricao, String data, String artistas, String username) throws RemoteException;

	public boolean remove_Musico(String name, String username) throws RemoteException;

	public boolean altera_Musico(String name, String data, String descricao,String genero, String username) throws RemoteException;

	public boolean insere_Album(String username, String nome, String descricao, String autor, String data) throws RemoteException;

	public boolean remove_Album(String artista, String name, String username) throws RemoteException;

	public boolean altera_Album(String name,String artista, String username, String nova_des, String data) throws RemoteException;

	public boolean insere_Musica(String username, String name, String autor, String compositor, String data, String album, String descricao) throws RemoteException;

	public boolean remove_Musica(String artista, String album, String name, String username) throws RemoteException;

	public boolean altera_Musica(String artista, String album, String name, String compositor, String descricao, String username) throws RemoteException;

	public String Pesquisa_Geral(ArrayList<String> Albuns, ArrayList<String> Musicas, ArrayList<String> Artistas, String pesq, String username) throws RemoteException;

	public String Pesquisa_Musica(String pesq, String username) throws RemoteException;

	public String Pesquisa_Album(String pesq, ArrayList<String> Musicas, String username) throws RemoteException;

	public String Pesquisa_Artista(String pesq, ArrayList<String> Albuns, String username) throws RemoteException;

	public void Make_Editor(String username, String target) throws RemoteException;

	public void Escreve_Critica(String username, String critica, int pont, String Artista, String album) throws RemoteException;

	public String Upload_Musica(String username, String file) throws RemoteException;

	public void ping() throws RemoteException;

	public String Transfer_Musica(String username, String nome_musica) throws RemoteException;

	public String criarPlaylist(String username, String nome_playlist, String tipo) throws RemoteException;

	public String sharePlaylist(String username, String nome_playlist, String target) throws RemoteException;

	public String getPlaylist(String username, String nome_playlist, String criador) throws RemoteException;

	public String adicionaMusicaPlaylist(String username, String nome_playlist, String artista, String album, String musica) throws RemoteException;

	public String criarConcerto(String username, String local_concerto, String nome, String concerto_data) throws RemoteException;

	public String adicionaArtistaConcerto(String username, String local_concerto, String nome, String data, String artista) throws RemoteException;

	public String adicionaMusicaConcerto(String username, String local_concerto, String nome, String data_concerto, String artista, String album, String musica) throws RemoteException;
	//public void Download_Musica();
}