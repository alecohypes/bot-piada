// Ferramenta do campus (modelo da Agenda): implements Ferramenta.
public class Biblioteca implements Ferramenta {

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("biblioteca");
    }

    @Override
    public String executar(String pedido) {
        return "A biblioteca funciona de segunda a sexta, das 8h às 21h.";
    }
}
