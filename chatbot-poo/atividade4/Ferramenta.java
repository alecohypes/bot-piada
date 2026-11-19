// INTERFACE: contrato que toda ferramenta precisa cumprir.
public interface Ferramenta {
    boolean consegue(String pedido);
    String executar(String pedido);
}
