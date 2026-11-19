// Classe-mãe: todo robô tem um nome e sabe responder.
public class Bot {
    String nome;

    public Bot(String nome) {
        this.nome = nome;
    }

    public String responder(String mensagem) {
        return "Ainda não sei responder.";
    }
}
