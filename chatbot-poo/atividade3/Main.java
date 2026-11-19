import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Só as listas mudaram: o agente Zeca ganha as habilidades Piada e Elogio.
        List<Ferramenta> habilidades = List.of(new Piada(), new Elogio());
        List<Bot> bots = List.of(new AgenteIA("Zeca", habilidades));

        Scanner teclado = new Scanner(System.in);
        while (true) {
            System.out.print("Você: ");
            String mensagem = teclado.nextLine().toLowerCase();
            if (mensagem.equals("sair")) {
                break;
            }
            // POLIMORFISMO: mesmo for, outros objetos.
            for (Bot bot : bots) {
                System.out.println(bot.nome + ": " + bot.responder(mensagem));
            }
        }
    }
}
