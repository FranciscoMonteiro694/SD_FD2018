import java.io.Serializable;
import java.util.ArrayList;

public class Artista implements Serializable {
    private String nome;
    private ArrayList<String> constituintes;
    private ArrayList<String> generos;
    private Data data_nasc;
    private String descricao;

    Artista(String nome,ArrayList<String> constituintes,ArrayList<String> generos){
        this.nome=nome;
        this.constituintes=constituintes;
        this.generos=generos;

    }
}
