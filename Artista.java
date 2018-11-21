import java.io.Serializable;
import java.util.ArrayList;

public class Artista implements Serializable {
    private String nome;
    private String genero;
    private Data data_nasc;
    private String descricao;
    private ArrayList<Album> listaAlbuns;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public ArrayList<Album> getListaAlbuns() {
        return listaAlbuns;
    }

    public void setListaAlbuns(ArrayList<Album> listaAlbuns) {
        this.listaAlbuns = listaAlbuns;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Data getData_nasc() {
        return data_nasc;
    }

    public void setData_nasc(Data data_nasc) {
        this.data_nasc = data_nasc;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    Artista(String nome){
        this.nome=nome;
    }

    Artista(String nome,ArrayList<Album> listaAlbuns){
        this.nome=nome;
        this.listaAlbuns=listaAlbuns;
    }

    Artista(String nome, Data data_nasc, String descricao, String genero){
        this.nome=nome;
        this.descricao=descricao;
        this.data_nasc=data_nasc;
        this.genero=genero;
    }
    Artista(String nome, Data data_nasc, String descricao, String genero,ArrayList<Album> listaAlbuns){
        this.nome=nome;
        this.genero=genero;
        this.data_nasc=data_nasc;
        this.genero=genero;
        this.listaAlbuns=listaAlbuns;

    }
}
