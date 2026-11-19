// HERANÇA (modelo do ChatBot): BotBoasVindas é um Bot.
public class BotBoasVindas extends Bot {

    public BotBoasVindas(String nome) {
        super(nome);
    }

    @Override
    public String responder(String mensagem) {
        return "Bem-vindo à Fametro!";
    }
}
