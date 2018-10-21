public class Critica {
    private String justificao;//ate 300 palavras
    private int avaliacao;//de 0 a 10

    Critica(String justificao,int avaliacao){
        this.justificao=justificao;
        this.avaliacao=avaliacao;
    }

    public String getJustificao() {
        return justificao;
    }

    public void setJustificao(String justificao) {
        this.justificao = justificao;
    }

    public int getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(int avaliacao) {
        this.avaliacao = avaliacao;
    }
}
