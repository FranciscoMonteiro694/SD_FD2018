import java.util.ArrayList;

public class Album {
    private String nome;
    private ArrayList<Musica> musicas;
    private Data data_lancamento;
    private ArrayList<Critica> criticas;
    private double pontuacao_med;
    private String descricao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    Album(Data data_lancamento, ArrayList<Critica> criticas){
        this.data_lancamento=data_lancamento;
        this.criticas=criticas;
    }

    public void setCriticas(ArrayList<Critica> criticas) {
        this.criticas = criticas;
    }

    public ArrayList<Musica> getMusicas() {
        return musicas;
    }

    public void setMusicas(ArrayList<Musica> musicas) {
        this.musicas = musicas;
    }

    public Data getData_lancamento() {
        return data_lancamento;
    }

    public void setData_lancamento(Data data_lancamento) {
        this.data_lancamento = data_lancamento;
    }

    public ArrayList<Critica> getCriticas() {
        return criticas;
    }
}
