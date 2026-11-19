# Chatbot POO: Bia, Max e companhia

Projeto da disciplina de **Programação Orientada a Objetos** (Fametro) que mostra,
com robôs de conversa, os três pilares trabalhados em aula:

- **Herança**: vários tipos de bot "são um" `Bot`;
- **Interface**: o contrato `Ferramenta`, que dá habilidades a um agente;
- **Polimorfismo**: a mesma linha de código faz cada bot responder do seu jeito.

Feito em **Java puro** (JDK 17 ou mais novo), sem Maven, Gradle nem bibliotecas externas.
Também tem uma **interface web** para testar todos os bots em um só lugar.

---

## Estrutura do repositório

```
chatbot-poo/
├── aula/          Projeto base: Bia (ChatBot) e Max (AgenteIA com Agenda e Relógio)
├── atividade2/    Fácil (resposta "nota"), Médio (Conversor km→m), Desafio (BotPiada Juca)
├── atividade3/    Show de Bots: agente Zeca com as habilidades Piada e Elogio
├── atividade4/    Assistente da Fametro: Fabi (Biblioteca, Secretaria) e Téo (boas-vindas)
├── web/
│   ├── Servidor.java   Servidor que roda os bots e entrega a página
│   └── index.html      Interface para conversar com todos os bots
├── gerar_pdf.py               Gera o PDF de entrega (normas ABNT)
└── Atividade_ChatBot_POO.pdf  PDF de entrega já gerado
```

Cada pasta de etapa é **independente** e compila sozinha.

---

## Como o código funciona

### As classes base (iguais em todas as etapas)

| Classe / interface | Papel |
|--------------------|-------|
| `Bot`              | Classe-mãe. Todo robô tem um `nome` e um método `responder(mensagem)`. |
| `ChatBot extends Bot` | Responde com `if`s fixos (`"oi"`, `"aula"`, ...). |
| `Ferramenta` (interface) | Contrato: `consegue(pedido)` diz se a ferramenta sabe tratar o pedido; `executar(pedido)` devolve a resposta. |
| `AgenteIA extends Bot` | Recebe uma lista de `Ferramenta`s e usa a **primeira** que `consegue` responder. |
| `Main`             | Lê o que você digita, coloca em minúsculas e chama `responder` de cada bot da lista. `sair` encerra. |

O coração do polimorfismo está no `Main`:

```java
for (Bot bot : bots) {
    System.out.println(bot.nome + ": " + bot.responder(mensagem));
}
```

A linha é sempre a mesma, mas cada objeto executa **a sua própria versão** de
`responder`. O mesmo acontece dentro do `AgenteIA`:

```java
for (Ferramenta f : ferramentas) {
    if (f.consegue(pedido)) {
        return f.executar(pedido);
    }
}
return "Não tenho ferramenta para isso.";
```

Para criar uma nova habilidade, basta escrever uma classe que `implements Ferramenta`
e colocá-la na lista do `Main`. O `AgenteIA` não precisa mudar nada.

### O que cada etapa acrescenta

| Etapa | Bots | Novidade |
|-------|------|----------|
| `aula` | **Bia** (`ChatBot`), **Max** (`AgenteIA`) | Ferramentas `Agenda` (palavra "prova") e `Relogio` (palavra "hora"). |
| `atividade2` | Bia, Max, **Juca** (`BotPiada`) | **Fácil:** Bia responde "nota". **Médio:** ferramenta `Conversor` (km → metros, aceita `2,5`). **Desafio:** novo bot `BotPiada extends Bot`. |
| `atividade3` | **Zeca** (`AgenteIA`) | Habilidades `Piada` e `Elogio`, com respostas sorteadas (`Random`). |
| `atividade4` | **Fabi** (`AgenteIA`), **Téo** (`BotBoasVindas`) | Ferramentas `Biblioteca` e `Secretaria`; Téo sempre dá boas-vindas à Fametro. |

### Atividade 1: perguntas do "Investigue"

**1. O que a Bia responde para "boa noite"?**
Responde **"Oi! Tudo bem?"**. O `contains("oi")` procura a *sequência de letras*
"oi" em qualquer lugar do texto, e "n**oi**te" contém essa sequência.

**2. Quem responde "qual a hora da prova?"?**
A **Agenda**: "A prova é na sexta, às 19h." O pedido contém "hora" e "prova", mas o
`for` do `AgenteIA` segue a ordem da lista `List.of(new Agenda(), new Relogio())`
e para na primeira ferramenta que consegue. Trocando a ordem para
`List.of(new Relogio(), new Agenda())`, quem responde passa a ser o **Relógio**.

---

## Como testar

### Opção 1: interface web (recomendado)

Testa **todas as etapas em um só lugar**, direto no navegador.

```
cd chatbot-poo
java web/Servidor.java
```

Quando aparecer `Pronto! Abra http://localhost:8080`, abra esse endereço no navegador.

- Na lateral, escolha a etapa (**Aula**, **Atividade 2**, **3** ou **4**) ou
  **Todas as etapas** para mandar a mesma mensagem para os 6 bots de uma vez.
- Clique nas sugestões de mensagem ou digite a sua.
- Para parar o servidor, use `Ctrl+C` no terminal.
- Para usar outra porta: `java web/Servidor.java 9090`.

**Como a interface funciona por dentro:** o `Servidor.java` usa o servidor HTTP que já
vem no JDK (`com.sun.net.httpserver`). Ao iniciar, ele **compila** cada pasta de etapa
(em `chatbot-poo/web/build/`) e **roda o `Main.java` original** de cada uma como um
programa separado. Cada mensagem enviada pela página vai para o `Main` como se fosse
digitada no teclado, e o que ele imprime vira as respostas na tela. Por isso as
respostas são exatamente as do código das atividades, sem nenhum bot reescrito.
Se alguém mandar `sair`, o programa daquela etapa encerra e é reiniciado sozinho.

### Opção 2: terminal (como na aula)

```
cd chatbot-poo/aula
javac -encoding UTF-8 *.java
java Main
```

Troque `aula` por `atividade2`, `atividade3` ou `atividade4` para as outras etapas.
Digite `sair` para encerrar.

> No Windows, se os acentos aparecerem estranhos no terminal, rode antes `chcp 65001`.

Para apagar os `.class` gerados: `del *.class` (Windows) ou `rm *.class` (Linux/Mac).

### Mensagens para testar

| Mensagem | O que esperar |
|----------|---------------|
| `oi` | Bia: "Oi! Tudo bem?" · Téo: boas-vindas |
| `boa noite` | Bia também responde "Oi! Tudo bem?" (veja a Atividade 1) |
| `que horas são?` | Max mostra a hora atual |
| `quando é a prova?` | Max: "A prova é na sexta, às 19h." |
| `qual a hora da prova?` | Max responde com a Agenda (ordem da lista) |
| `nota` | Bia (atividade 2): notas no portal do aluno |
| `converter 5 km` / `converter 2,5 km` | Max (atividade 2): converte para metros |
| `piada` | Juca (atividade 2) e Zeca (atividade 3) |
| `elogio` | Zeca (atividade 3), elogio sorteado |
| `biblioteca` / `secretaria` | Fabi (atividade 4): horários de atendimento |
| `sair` | Encerra o programa |

---

## Gerar o PDF de entrega (opcional)

O `chatbot-poo/gerar_pdf.py` compila e roda cada etapa, captura a conversa e monta o
`Atividade_ChatBot_POO.pdf` nas normas da ABNT. Precisa de Python e da biblioteca
`reportlab`:

```
pip install reportlab
python chatbot-poo/gerar_pdf.py
```
