// Desafio: HERANÇA - BotPiada é um Bot e tem seu próprio responder.
public class BotPiada extends Bot {

    public BotPiada(String nome) {
        super(nome);
    }

    @Override
    public String responder(String mensagem) {
        if (mensagem.contains("piada")) {
            return "Por que o computador foi ao dentista? Porque estava com dor de bit!";
        }
        return "Peça uma piada!";
    }
}
