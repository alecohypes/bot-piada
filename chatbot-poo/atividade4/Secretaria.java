// Outra ferramenta do campus: implements Ferramenta.
public class Secretaria implements Ferramenta {

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("secretaria");
    }

    @Override
    public String executar(String pedido) {
        return "A secretaria atende das 8h às 20h, no térreo.";
    }
}
