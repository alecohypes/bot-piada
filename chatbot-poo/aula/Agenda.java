// Agenda cumpre o contrato da interface Ferramenta (implements).
public class Agenda implements Ferramenta {

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("prova");
    }

    @Override
    public String executar(String pedido) {
        return "A prova é na sexta, às 19h.";
    }
}
