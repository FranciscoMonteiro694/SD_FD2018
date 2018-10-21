public class Notificacao {
    private String nota;
    private String destinario;

    Notificacao(String nota, String destinario){
        this.nota=nota;
        this.destinario=destinario;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getDestinario() {
        return destinario;
    }

    public void setDestinario(String destinario) {
        this.destinario = destinario;
    }
}
