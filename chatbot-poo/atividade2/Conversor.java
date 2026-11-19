// Nível Médio: nova ferramenta que implementa a interface Ferramenta.
public class Conversor implements Ferramenta {

    @Override
    public boolean consegue(String pedido) {
        return pedido.contains("km");
    }

    @Override
    public String executar(String pedido) {
        String[] partes = pedido.split(" ");
        for (int i = 0; i < partes.length; i++) {
            // troca vírgula por ponto e tira o "km" grudado (ex.: "5km")
            String texto = partes[i].replace(",", ".").replace("km", "");
            try {
                double km = Double.parseDouble(texto);
                double metros = km * 1000;
                return km + " km = " + metros + " metros";
            } catch (NumberFormatException e) {
                // não era número, tenta a próxima palavra
            }
        }
        return "Diga um valor, ex.: converter 5 km";
    }
}
