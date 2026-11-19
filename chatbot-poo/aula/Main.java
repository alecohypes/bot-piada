import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Ferramenta> ferramentas = List.of(new Agenda(), new Relogio());
        List<Bot> bots = List.of(new ChatBot("Bia"), new AgenteIA("Max", ferramentas));

        Scanner teclado = new Scanner(System.in);
        while (true) {
            System.out.print("Você: ");
            String mensagem = teclado.nextLine().toLowerCase();
            if (mensagem.equals("sair")) {
                break;
            }
            // POLIMORFISMO: a mesma linha chama o responder de cada tipo de Bot.
            for (Bot bot : bots) {
                System.out.println(bot.nome + ": " + bot.responder(mensagem));
            }
        }
    }
}
