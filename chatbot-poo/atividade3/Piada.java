import java.util.Random;

// Piada implementa a interface Ferramenta: vira uma habilidade do agente.
public class Piada implements Ferramenta {
    String[] piadas = {
        "Por que o Java foi ao médico? Porque estava cheio de exceções!",
        "Por que o for nunca descansa? Porque vive dando voltas!",
        "O que o if disse ao else? Sem você, fico sem saída!"
    };

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("piada");
    }

    @Override
    public String executar(String pedido) {
        return piadas[new Random().nextInt(piadas.length)];
    }
}
