# Chatbot POO — Bia e Max

Atividade de Programação Orientada a Objetos sobre **herança**, **interface** e
**polimorfismo**. Java puro (JDK 17+), sem Maven, Gradle nem bibliotecas externas.

## Pastas

| Pasta         | Conteúdo                                                        |
|---------------|-----------------------------------------------------------------|
| `aula/`       | Projeto base: Bia (ChatBot) e Max (AgenteIA com Agenda e Relógio) |
| `atividade2/` | Fácil (resposta "nota"), Médio (Conversor km→m), Desafio (BotPiada Juca) |
| `atividade3/` | Show de Bots: agente Zeca com as habilidades Piada e Elogio      |
| `atividade4/` | Assistente da Fametro: Fabi (Biblioteca, Secretaria) e Téo (boas-vindas) |
| `RESPOSTAS.md`| Respostas da Atividade 1 (Investigue)                            |

Cada pasta é independente e compila sozinha.

## Como rodar

Entre na pasta desejada, compile e execute:

```
cd aula
javac -encoding UTF-8 *.java
java Main
```

Troque `aula` por `atividade2`, `atividade3` ou `atividade4` para as outras etapas.
Digite `sair` para encerrar.

> No Windows, se os acentos aparecerem estranhos no terminal, rode antes `chcp 65001`.

## Testar todos os bots no navegador

A pasta `web/` tem uma interface para conversar com todos os bots em um só lugar.
O servidor compila cada pasta e roda o `Main.java` **original** dela, então as
respostas são exatamente as do código das atividades.

```
cd chatbot-poo
java web/Servidor.java
```

Abra **http://localhost:8080** no navegador. Escolha uma etapa na lateral
(ou "Todas as etapas" para mandar a mesma mensagem para todos os bots) e use as
sugestões de mensagem. Para usar outra porta: `java web/Servidor.java 9090`.

## Mensagens para testar

`oi`, `boa noite`, `que horas são?`, `quando é a prova?`, `qual a hora da prova?`,
`nota`, `converter 5 km`, `converter 2,5 km`, `piada`, `elogio`, `biblioteca`,
`secretaria`, `sair`

## Limpar

Para apagar os arquivos compilados:

```
del *.class      (Windows)
rm *.class       (Linux/Mac)
```
