import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Só a criação das listas mudou.
        List<Ferramenta> campus = List.of(new Biblioteca(), new Secretaria());
        List<Bot> bots = List.of(new AgenteIA("Fabi", campus), new BotBoasVindas("Téo"));

        Scanner teclado = new Scanner(System.in);
        while (true) {
            System.out.print("Você: ");
            String mensagem = teclado.nextLine().toLowerCase();
            if (mensagem.equals("sair")) {
                break;
            }
            // POLIMORFISMO: o for não mudou nada.
            for (Bot bot : bots) {
                System.out.println(bot.nome + ": " + bot.responder(mensagem));
            }
        }
    }
}
