import java.util.List;

// HERANÇA: AgenteIA também é um Bot, mas responde usando ferramentas.
public class AgenteIA extends Bot {
    List<Ferramenta> ferramentas;

    public AgenteIA(String nome, List<Ferramenta> ferramentas) {
        super(nome);
        this.ferramentas = ferramentas;
    }

    @Override
    public String responder(String pedido) {
        // POLIMORFISMO: cada ferramenta executa o seu próprio consegue/executar.
        for (Ferramenta f : ferramentas) {
            if (f.consegue(pedido)) {
                return f.executar(pedido);
            }
        }
        return "Não tenho ferramenta para isso.";
    }
}
