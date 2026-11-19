# Atividade 1 — Investigue

## 1. O que a Bia responde para "boa noite"?

A Bia responde **"Oi! Tudo bem?"**.

Isso acontece porque o `contains("oi")` não procura a *palavra* "oi", e sim a
*sequência de letras* "oi" em qualquer parte do texto. Em "n**oi**te" existe
essa sequência, então o primeiro `if` do `ChatBot` dá verdadeiro.

```java
if (mensagem.contains("oi")) {   // "boa noite" contém "oi" dentro de "noite"
    return "Oi! Tudo bem?";
}
```

## 2. Quem responde "qual a hora da prova?"

Responde a **Agenda**: "A prova é na sexta, às 19h."

O pedido contém tanto "hora" (Relógio) quanto "prova" (Agenda). O `for` do
`AgenteIA` percorre as ferramentas **na ordem da lista** e para na primeira que
consegue (`return`). Como a lista é:

```java
List.of(new Agenda(), new Relogio())
```

a Agenda é testada primeiro e já responde.

Se trocarmos a ordem para:

```java
List.of(new Relogio(), new Agenda())
```

o **Relógio** passa a responder primeiro, mostrando a hora atual.

---

## Herança, interface e polimorfismo no projeto

**Herança:** `ChatBot extends Bot` e `AgenteIA extends Bot`. As classes filhas
recebem o atributo `nome` da classe-mãe e chamam o construtor dela com
`super(nome)`. Com `@Override`, cada filha reescreve o método `responder` do
seu próprio jeito.

**Interface:** `Ferramenta` é um contrato: diz que toda ferramenta precisa ter
`consegue(...)` e `executar(...)`, mas não diz *como*. As classes `Agenda`,
`Relogio`, `Conversor`, `Piada`, `Elogio`, `Biblioteca` e `Secretaria` usam
`implements Ferramenta` e cumprem esse contrato. Por isso o `AgenteIA` aceita
qualquer uma delas sem precisar ser alterado.

**Polimorfismo:** no `Main`, a linha

```java
System.out.println(bot.nome + ": " + bot.responder(mensagem));
```

é sempre a mesma, mas cada objeto executa a **sua própria versão** de
`responder`: a Bia usa os `if` do `ChatBot`, o Max procura uma ferramenta, o
Juca conta piada, e assim por diante. O mesmo acontece dentro do `AgenteIA`
com `f.consegue(pedido)` e `f.executar(pedido)`.
