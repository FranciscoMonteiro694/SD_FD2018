import java.util.ArrayList;

public class Grupo extends Artista {
    private ArrayList <String> constituintes;

    Grupo(String nome, Data data_criacao, String descricao, String genero) {
        super(nome, data_criacao, descricao, genero);
    }
    Grupo(String nome, Data data_criacao, String descricao, String genero,ArrayList<String> constituintes ) {
        super(nome, data_criacao, descricao, genero);
        this.constituintes=constituintes;
    }
    public ArrayList<String> getConstituintes() {
        return constituintes;
    }

    public void setConstituintes(ArrayList<String> constituintes) {
        this.constituintes = constituintes;
    }

}
