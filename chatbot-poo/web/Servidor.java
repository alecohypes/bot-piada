import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Servidor web para testar todos os bots em um só lugar.
 *
 * Ele NÃO reescreve os bots: compila cada pasta (aula, atividade2, ...) e roda
 * o Main original de cada uma como um processo separado, mandando as mensagens
 * pelo teclado (stdin) e lendo as respostas da tela (stdout).
 *
 * Como rodar (dentro da pasta chatbot-poo):
 *     java web/Servidor.java
 * Depois abra http://localhost:8080
 */
public class Servidor {

    static final String[] ETAPAS = {"aula", "atividade2", "atividade3", "atividade4"};
    static final String PROMPT = "Você: ";

    static Path raiz;
    static final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        raiz = acharRaiz();
        Path build = raiz.resolve("web").resolve("build");

        for (String etapa : ETAPAS) {
            compilar(raiz.resolve(etapa), build.resolve(etapa));
            sessoes.put(etapa, new Sessao(build.resolve(etapa)));
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext("/", Servidor::paginaInicial);
        server.createContext("/api/chat", Servidor::chat);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> sessoes.values().forEach(Sessao::encerrar)));
        System.out.println("Pronto! Abra http://localhost:" + porta + " no navegador (Ctrl+C para parar).");
    }

    /** Aceita rodar tanto de chatbot-poo/ quanto de chatbot-poo/web/. */
    static Path acharRaiz() {
        Path atual = Paths.get("").toAbsolutePath();
        for (Path p : new Path[]{atual, atual.getParent()}) {
            if (p != null && Files.isDirectory(p.resolve("aula")) && Files.isDirectory(p.resolve("web"))) {
                return p;
            }
        }
        throw new IllegalStateException("Rode este comando dentro da pasta chatbot-poo.");
    }

    static void compilar(Path origem, Path destino) throws IOException {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        if (javac == null) {
            throw new IllegalStateException("Compilador não encontrado: use um JDK (não apenas JRE).");
        }
        Files.createDirectories(destino);
        List<String> argsJavac = new ArrayList<>(List.of("-encoding", "UTF-8", "-d", destino.toString()));
        try (Stream<Path> arquivos = Files.list(origem)) {
            arquivos.filter(f -> f.toString().endsWith(".java")).forEach(f -> argsJavac.add(f.toString()));
        }
        int codigo = javac.run(null, null, null, argsJavac.toArray(new String[0]));
        if (codigo != 0) {
            throw new IllegalStateException("Erro ao compilar a pasta " + origem.getFileName());
        }
        System.out.println("Compilado: " + origem.getFileName());
    }

    // ---------------------------------------------------------------- HTTP

    static void paginaInicial(HttpExchange ex) throws IOException {
        if (!ex.getRequestURI().getPath().equals("/")) {
            enviar(ex, 404, "text/plain", "Não encontrado");
            return;
        }
        byte[] html = Files.readAllBytes(raiz.resolve("web").resolve("index.html"));
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, html.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(html);
        }
    }

    /** POST /api/chat?etapa=aula  — corpo: a mensagem em texto puro. */
    static void chat(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equals("POST")) {
            enviar(ex, 405, "text/plain", "Use POST");
            return;
        }
        String query = Optional.ofNullable(ex.getRequestURI().getQuery()).orElse("");
        String etapa = query.startsWith("etapa=") ? query.substring(6) : "";
        Sessao sessao = sessoes.get(etapa);
        if (sessao == null) {
            enviar(ex, 400, "application/json", "{\"erro\":\"Etapa inválida\"}");
            return;
        }
        String mensagem = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)
                .replace("\r", " ").replace("\n", " ").trim();

        Resultado r = sessao.perguntar(mensagem);
        StringBuilder json = new StringBuilder("{\"respostas\":[");
        for (int i = 0; i < r.respostas.size(); i++) {
            String[] par = r.respostas.get(i);
            if (i > 0) json.append(',');
            json.append("{\"bot\":").append(str(par[0])).append(",\"texto\":").append(str(par[1])).append('}');
        }
        json.append("],\"aviso\":").append(r.aviso == null ? "null" : str(r.aviso)).append('}');
        enviar(ex, 200, "application/json", json.toString());
    }

    static void enviar(HttpExchange ex, int status, String tipo, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", tipo + "; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    static String str(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    // ------------------------------------------------------ Processo do Main

    record Resultado(List<String[]> respostas, String aviso) {}

    /** Um Main.java rodando de verdade, conversando por stdin/stdout. */
    static class Sessao {
        final Path classes;
        Process processo;
        Writer entrada;
        Reader saida;
        final ExecutorService leitor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        Sessao(Path classes) throws IOException {
            this.classes = classes;
            iniciar();
        }

        void iniciar() throws IOException {
            String java = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
            processo = new ProcessBuilder(java,
                    "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstdin.encoding=UTF-8",
                    "-cp", classes.toString(), "Main")
                    .redirectErrorStream(true)
                    .start();
            entrada = new OutputStreamWriter(processo.getOutputStream(), StandardCharsets.UTF_8);
            saida = new InputStreamReader(processo.getInputStream(), StandardCharsets.UTF_8);
            try {
                lerAtePrompt(); // descarta o primeiro "Você: "
            } catch (Exception e) {
                throw new IOException("O Main não iniciou: " + e.getMessage(), e);
            }
        }

        /** Lê a saída até o Main pedir a próxima mensagem ("Você: ") ou terminar. */
        String lerAtePrompt() throws Exception {
            return leitor.submit(() -> {
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = saida.read()) != -1) {
                    sb.append((char) c);
                    if (sb.toString().endsWith(PROMPT)) {
                        return sb.substring(0, sb.length() - PROMPT.length());
                    }
                }
                return sb.append("\u0000FIM").toString(); // o programa terminou (ex.: "sair")
            }).get(5, TimeUnit.SECONDS);
        }

        synchronized Resultado perguntar(String mensagem) {
            try {
                if (!processo.isAlive()) iniciar();
                entrada.write(mensagem + "\n");
                entrada.flush();
                String texto = lerAtePrompt();

                String aviso = null;
                if (texto.endsWith("\u0000FIM")) {
                    texto = texto.substring(0, texto.length() - 4);
                    aviso = "O programa encerrou (\"sair\"). Ele foi reiniciado automaticamente.";
                    iniciar();
                }
                List<String[]> respostas = new ArrayList<>();
                for (String linha : texto.split("\\R")) {
                    if (linha.isBlank()) continue;
                    int i = linha.indexOf(": ");
                    respostas.add(i > 0
                            ? new String[]{linha.substring(0, i), linha.substring(i + 2)}
                            : new String[]{"Sistema", linha});
                }
                return new Resultado(respostas, aviso);
            } catch (TimeoutException e) {
                reiniciarAposErro();
                return new Resultado(List.of(), "O programa demorou demais para responder e foi reiniciado.");
            } catch (Exception e) {
                reiniciarAposErro();
                return new Resultado(List.of(), "Erro ao falar com o programa: " + e.getMessage());
            }
        }

        void reiniciarAposErro() {
            encerrar();
            try {
                iniciar();
            } catch (IOException ignored) {
                // tenta de novo na próxima mensagem
            }
        }

        void encerrar() {
            if (processo != null) processo.destroyForcibly();
        }
    }
}
