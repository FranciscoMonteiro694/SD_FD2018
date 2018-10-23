import java.util.ArrayList;

public class Album {
    private String nome;
    private ArrayList<Musica> musicas;
    private Data data_lancamento;
    private ArrayList<Critica> criticas;
    private double pontuacao_med;
    private String descricao;
    private String autor;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    Album(String nome){
        this.nome=nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public double getPontuacao_med() {
        return pontuacao_med;
    }

    public void setPontuacao_med(double pontuacao_med) {
        this.pontuacao_med = pontuacao_med;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    Album(String nome, Data data_lancamento, String autor){
        this.data_lancamento=data_lancamento;
        this.nome=nome;
        this.autor=autor;
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
