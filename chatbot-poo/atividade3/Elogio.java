import java.util.Random;

// Nova habilidade: mesmo modelo da Piada (implements Ferramenta).
public class Elogio implements Ferramenta {
    String[] elogios = {
        "Seu código está mais limpo que tela de login!",
        "Você compila de primeira, que orgulho!",
        "Seus ifs são mais certeiros que GPS!",
        "Você é o @Override que faltava no meu código!"
    };

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("elogio");
    }

    @Override
    public String executar(String pedido) {
        return elogios[new Random().nextInt(elogios.length)];
    }
}
