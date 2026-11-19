import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Médio: Conversor entra na lista sem mudar o AgenteIA.
        List<Ferramenta> ferramentas = List.of(new Agenda(), new Relogio(), new Conversor());
        // Desafio: BotPiada entra na lista de bots.
        List<Bot> bots = List.of(new ChatBot("Bia"), new AgenteIA("Max", ferramentas), new BotPiada("Juca"));

        Scanner teclado = new Scanner(System.in);
        while (true) {
            System.out.print("Você: ");
            String mensagem = teclado.nextLine().toLowerCase();
            if (mensagem.equals("sair")) {
                break;
            }
            // POLIMORFISMO: o for continua igual, cada bot responde do seu jeito.
            for (Bot bot : bots) {
                System.out.println(bot.nome + ": " + bot.responder(mensagem));
            }
        }
    }
}
