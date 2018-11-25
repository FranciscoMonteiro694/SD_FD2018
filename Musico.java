import java.util.ArrayList;

public class Musico extends Artista{
    private Data dataNascimento;
    Musico(String nome) {
        super(nome);
    }


    Musico(String nome, Data data_nasc, String descricao, String genero) {

        super(nome, data_nasc, descricao, genero);
    }


    public Musico(String nome, Data data_criacao, String descricao, String genero, Data dataNascimento) {
        super(nome, data_criacao, descricao, genero);
        this.dataNascimento = dataNascimento;
    }

    public Data getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Data dataNascimento) {
        this.dataNascimento = dataNascimento;
    }




}
