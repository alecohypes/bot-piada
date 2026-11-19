import java.time.LocalTime;

// Relogio também implementa a interface Ferramenta.
public class Relogio implements Ferramenta {

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("hora");
    }

    @Override
    public String executar(String pedido) {
        return "Hora: " + LocalTime.now().withNano(0);
    }
}
