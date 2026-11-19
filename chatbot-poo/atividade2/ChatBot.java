// HERANÇA: ChatBot "é um" Bot (extends) e reescreve o responder (@Override).
public class ChatBot extends Bot {

    public ChatBot(String nome) {
        super(nome);
    }

    @Override
    public String responder(String mensagem) {
        if (mensagem.contains("oi")) {
            return "Oi! Tudo bem?";
        }
        if (mensagem.contains("aula")) {
            return "A aula começa às 19h.";
        }
        // Nível Fácil: nova resposta
        if (mensagem.contains("nota")) {
            return "As notas saem no portal do aluno.";
        }
        return "Não entendi. Pode repetir?";
    }
}
