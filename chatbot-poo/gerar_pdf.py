"""Gera o PDF de entrega da atividade de POO (Chatbot e Agente de IA em Java).

Uso:  python gerar_pdf.py
Compila e roda cada pasta que tem Main.java, captura a conversa no terminal
e monta o arquivo Atividade_ChatBot_POO.pdf na mesma pasta deste script.
"""

import os
import subprocess
import textwrap
from datetime import date
from xml.sax.saxutils import escape

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (KeepTogether, PageBreak, Paragraph,
                                SimpleDocTemplate, Spacer, XPreformatted)

# ---------------- Dados da capa (edite aqui) ----------------
ALUNO = "[MEU NOME]"
CURSO = "[MEU CURSO]"
DISCIPLINA = "Programação Orientada a Objetos"
INSTITUICAO = "Fametro"
TITULO = "Herança, Interface e Polimorfismo: Chatbot e Agente de IA em Java"

RAIZ = os.path.dirname(os.path.abspath(__file__))
SAIDA_PDF = os.path.join(RAIZ, "Atividade_ChatBot_POO.pdf")

MENSAGENS = {
    "aula": ["oi", "que horas são?", "quando é a prova?", "boa noite", "qual a hora da prova?"],
    "atividade2": ["oi", "qual a minha nota?", "converter 2,5 km", "me conta uma piada"],
    "atividade3": ["me conte uma piada", "me faz um elogio"],
    "atividade4": ["oi", "onde fica a biblioteca?", "horário da secretaria?"],
}
MENSAGENS_PADRAO = ["oi", "que horas são?", "quando é a prova?"]

TITULOS = {
    "aula": "Projeto base",
    "atividade2": "Atividade 2 – Níveis",
    "atividade3": "Atividade 3 – Show de Bots",
    "atividade4": "Atividade 4 – Assistente da Fametro",
}

DESCRICOES = {
    "aula": "Projeto da aula: a Bia (ChatBot) responde com if/contains e o Max (AgenteIA) "
            "usa as ferramentas Agenda e Relogio, que implementam a interface Ferramenta.",
    "atividade2": "Fácil: a Bia passa a responder sobre notas. Médio: a ferramenta Conversor "
                  "transforma km em metros. Desafio: o novo BotPiada (Juca) herda de Bot. "
                  "Bot, Ferramenta e AgenteIA não foram alterados.",
    "atividade3": "O agente Zeca recebe as habilidades Piada e Elogio. No Main só as listas "
                  "mudaram; o AgenteIA é o mesmo da aula.",
    "atividade4": "Assistente da Fametro: a Fabi (AgenteIA) usa as ferramentas Biblioteca e "
                  "Secretaria, e o Téo (BotBoasVindas) dá as boas-vindas a qualquer mensagem.",
}

# Ordem de apresentação dos arquivos em cada pasta (os demais vêm depois, em ordem alfabética)
ORDEM_ARQUIVOS = ["Bot.java", "ChatBot.java", "Ferramenta.java", "Agenda.java", "Relogio.java",
                  "AgenteIA.java"]


# ---------------- Fontes ----------------
def registrar_fontes():
    """Registra fontes TTF com acentos. Retorna (normal, negrito, mono)."""
    win = "C:/Windows/Fonts/"
    if os.path.exists(win + "arial.ttf") and os.path.exists(win + "consola.ttf"):
        pdfmetrics.registerFont(TTFont("Texto", win + "arial.ttf"))
        negrito = win + "arialbd.ttf" if os.path.exists(win + "arialbd.ttf") else win + "arial.ttf"
        pdfmetrics.registerFont(TTFont("Texto-Bold", negrito))
        pdfmetrics.registerFont(TTFont("Mono", win + "consola.ttf"))
    else:
        candidatos = ["/usr/share/fonts/truetype/dejavu/", "/usr/share/fonts/dejavu/",
                      "/Library/Fonts/", os.path.join(RAIZ, "")]
        pasta = next((p for p in candidatos if os.path.exists(p + "DejaVuSans.ttf")), None)
        if pasta is None:
            raise SystemExit("Fonte com acentos não encontrada (arial.ttf ou DejaVuSans.ttf).")
        pdfmetrics.registerFont(TTFont("Texto", pasta + "DejaVuSans.ttf"))
        pdfmetrics.registerFont(TTFont("Texto-Bold", pasta + "DejaVuSans-Bold.ttf"))
        pdfmetrics.registerFont(TTFont("Mono", pasta + "DejaVuSansMono.ttf"))
    pdfmetrics.registerFontFamily("Texto", normal="Texto", bold="Texto-Bold",
                                  italic="Texto", boldItalic="Texto-Bold")


# ---------------- Java ----------------
def encontrar_pastas():
    pastas = []
    for nome in os.listdir(RAIZ):
        caminho = os.path.join(RAIZ, nome)
        if os.path.isdir(caminho) and os.path.exists(os.path.join(caminho, "Main.java")):
            pastas.append(nome)
    if os.path.exists(os.path.join(RAIZ, "Main.java")):
        pastas.append(".")
    # "aula" primeiro, depois as demais em ordem alfabética
    pastas.sort(key=lambda n: (n != "aula", n))
    return pastas


def arquivos_java(pasta):
    arquivos = [f for f in os.listdir(pasta) if f.endswith(".java")]
    def chave(nome):
        if nome == "Main.java":
            return (2, 0, nome)
        if nome in ORDEM_ARQUIVOS:
            return (0, ORDEM_ARQUIVOS.index(nome), nome)
        return (1, 0, nome)
    return sorted(arquivos, key=chave)


def compilar_e_rodar(pasta, mensagens):
    arquivos = arquivos_java(pasta)
    comp = subprocess.run(["javac", "-encoding", "UTF-8"] + arquivos, cwd=pasta,
                          capture_output=True)
    if comp.returncode != 0:
        raise SystemExit("Erro ao compilar " + pasta + ":\n" + comp.stderr.decode("utf-8", "replace"))

    entrada = "\n".join(mensagens + ["sair"]) + "\n"
    exe = subprocess.run(["java", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8",
                          "-Dstdin.encoding=UTF-8", "Main"],
                         cwd=pasta, input=entrada.encode("utf-8"), capture_output=True,
                         timeout=30)
    saida = exe.stdout.decode("utf-8", "replace").replace("\r\n", "\n")

    # O programa escreve "Você: " sem quebrar linha; inserimos a mensagem digitada
    partes = saida.split("Você: ")
    conversa = partes[0]
    enviadas = mensagens + ["sair"]
    for i, trecho in enumerate(partes[1:]):
        msg = enviadas[i] if i < len(enviadas) else ""
        conversa += "Você: " + msg + "\n" + trecho
    return conversa.strip("\n")


def apagar_class():
    for pasta, _, arquivos in os.walk(RAIZ):
        for f in arquivos:
            if f.endswith(".class"):
                os.remove(os.path.join(pasta, f))


# ---------------- PDF ----------------
LARGURA_CODIGO = 92  # caracteres por linha antes de quebrar


def quebrar_linhas(texto, largura=LARGURA_CODIGO):
    linhas = []
    for linha in texto.replace("\t", "    ").split("\n"):
        if len(linha) <= largura:
            linhas.append(linha)
        else:
            recuo = len(linha) - len(linha.lstrip())
            partes = textwrap.wrap(linha, width=largura, subsequent_indent=" " * (recuo + 4),
                                   drop_whitespace=False, break_on_hyphens=False)
            linhas.extend(p.rstrip() for p in partes)
    return "\n".join(linhas)


def bloco(texto, estilo):
    return XPreformatted(escape(quebrar_linhas(texto)), estilo)


def criar_estilos():
    s = {}
    s["titulo_capa"] = ParagraphStyle("titulo_capa", fontName="Texto-Bold", fontSize=24,
                                      leading=30, alignment=TA_CENTER, spaceAfter=24)
    s["capa"] = ParagraphStyle("capa", fontName="Texto", fontSize=13, leading=20,
                               alignment=TA_CENTER)
    s["h1"] = ParagraphStyle("h1", fontName="Texto-Bold", fontSize=18, leading=22,
                             spaceBefore=6, spaceAfter=10, textColor=colors.HexColor("#1F3A5F"))
    s["h2"] = ParagraphStyle("h2", fontName="Texto-Bold", fontSize=12, leading=15,
                             spaceBefore=12, spaceAfter=6, textColor=colors.HexColor("#333333"))
    s["corpo"] = ParagraphStyle("corpo", fontName="Texto", fontSize=11, leading=16,
                                alignment=TA_JUSTIFY, spaceAfter=8)
    s["codigo"] = ParagraphStyle("codigo", fontName="Mono", fontSize=8.5, leading=11,
                                 backColor=colors.HexColor("#F2F2F2"),
                                 borderColor=colors.HexColor("#DDDDDD"), borderWidth=0.5,
                                 borderPadding=6, spaceBefore=4, spaceAfter=12)
    s["terminal"] = ParagraphStyle("terminal", parent=s["codigo"],
                                   backColor=colors.HexColor("#1E1E1E"),
                                   borderColor=colors.HexColor("#1E1E1E"),
                                   textColor=colors.HexColor("#E6E6E6"))
    return s


def rodape(canvas, doc):
    canvas.saveState()
    canvas.setFont("Texto", 9)
    canvas.setFillColor(colors.grey)
    canvas.drawCentredString(A4[0] / 2, 1.2 * cm, "Página %d" % doc.page)
    canvas.restoreState()


def montar_pdf(resultados):
    s = criar_estilos()
    meses = ["janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto",
             "setembro", "outubro", "novembro", "dezembro"]
    hoje = date.today()
    data_txt = "%d de %s de %d" % (hoje.day, meses[hoje.month - 1], hoje.year)

    h = []
    # a) Capa
    h.append(Spacer(1, 6 * cm))
    h.append(Paragraph(escape(TITULO), s["titulo_capa"]))
    h.append(Spacer(1, 2 * cm))
    for rotulo, valor in [("Aluno", ALUNO), ("Curso", CURSO), ("Disciplina", DISCIPLINA),
                          ("Instituição", INSTITUICAO)]:
        h.append(Paragraph("<b>%s:</b> %s" % (rotulo, escape(valor)), s["capa"]))
    h.append(Spacer(1, 3 * cm))
    h.append(Paragraph(data_txt, s["capa"]))
    h.append(PageBreak())

    # b) Introdução
    h.append(Paragraph("Introdução", s["h1"]))
    h.append(Paragraph(
        "Este trabalho apresenta dois robôs em Java: a <b>Bia</b>, um chatbot que responde "
        "com regras simples, e o <b>Max</b>, um agente que usa ferramentas. Eles mostram três "
        "conceitos de POO. <b>Herança</b>: as classes ChatBot e AgenteIA usam <i>extends Bot</i>, "
        "chamam o construtor da classe-mãe com <i>super(nome)</i> e reescrevem o método "
        "<i>responder</i> com <i>@Override</i>. <b>Interface</b>: Ferramenta é um contrato com os "
        "métodos <i>consegue</i> e <i>executar</i>; classes como Agenda e Relogio usam "
        "<i>implements Ferramenta</i> e cumprem esse contrato. <b>Polimorfismo</b>: no Main, a "
        "mesma linha <i>bot.responder(mensagem)</i> é executada para todos os robôs, mas cada "
        "objeto responde com a sua própria versão do método.", s["corpo"]))
    h.append(Paragraph(
        "Cada etapa abaixo traz o código completo de todas as classes e uma execução real no "
        "terminal, gerada automaticamente ao compilar e rodar o projeto.", s["corpo"]))
    h.append(PageBreak())

    # c) Uma seção por pasta
    for pasta, conversa in resultados:
        nome = os.path.basename(os.path.abspath(pasta))
        h.append(Paragraph(escape(TITULOS.get(nome, "Projeto – " + nome)), s["h1"]))
        h.append(Paragraph(escape(DESCRICOES.get(nome, "Execução do projeto da pasta " + nome + ".")),
                           s["corpo"]))
        for arquivo in arquivos_java(pasta):
            with open(os.path.join(pasta, arquivo), encoding="utf-8") as f:
                codigo = f.read().rstrip()
            h.append(Paragraph(escape(arquivo), s["h2"]))
            h.append(bloco(codigo, s["codigo"]))
        h.append(KeepTogether([Paragraph("Execução no terminal", s["h2"]),
                               bloco(conversa, s["terminal"])]))
        h.append(PageBreak())

    # d) Atividade 1 – Investigue
    h.append(Paragraph("Atividade 1 – Investigue", s["h1"]))
    h.append(Paragraph("1. O que a Bia responde para \"boa noite\"?", s["h2"]))
    h.append(Paragraph(
        "A Bia responde <b>\"Oi! Tudo bem?\"</b>, porque a palavra \"n<b>oi</b>te\" contém a "
        "sequência \"oi\" e o método <i>contains</i> procura a sequência de letras em qualquer "
        "parte do texto, não a palavra inteira.", s["corpo"]))
    h.append(Paragraph("2. Quem responde \"qual a hora da prova?\"", s["h2"]))
    h.append(Paragraph(
        "Quem responde é a <b>Agenda</b>. O pedido contém \"hora\" e \"prova\", mas a Agenda vem "
        "primeiro no <i>List.of</i> e o <i>for</i> do AgenteIA para na primeira ferramenta que "
        "consegue atender. Se a ordem for trocada para "
        "<i>List.of(new Relogio(), new Agenda())</i>, quem responde é o <b>Relógio</b>.",
        s["corpo"]))

    # e) Conclusão
    h.append(Paragraph("Conclusão", s["h1"]))
    h.append(Paragraph(
        "O projeto mostrou que a herança permite criar novos robôs (ChatBot, AgenteIA, BotPiada, "
        "BotBoasVindas) reaproveitando a classe Bot; que a interface Ferramenta permite "
        "acrescentar habilidades (Conversor, Piada, Elogio, Biblioteca, Secretaria) sem alterar o "
        "AgenteIA; e que o polimorfismo faz o mesmo laço do Main funcionar com qualquer robô. "
        "Em todas as atividades, as novas funções foram adicionadas apenas criando classes e "
        "mudando as listas, sem mexer no código que já funcionava.", s["corpo"]))

    doc = SimpleDocTemplate(SAIDA_PDF, pagesize=A4, leftMargin=2 * cm, rightMargin=2 * cm,
                            topMargin=2 * cm, bottomMargin=2 * cm, title=TITULO, author=ALUNO)
    doc.build(h, onFirstPage=rodape, onLaterPages=rodape)


def main():
    registrar_fontes()
    pastas = encontrar_pastas()
    if not pastas:
        raise SystemExit("Nenhuma pasta com Main.java encontrada.")
    resultados = []
    try:
        for nome in pastas:
            caminho = os.path.join(RAIZ, nome)
            msgs = MENSAGENS.get(os.path.basename(os.path.abspath(caminho)), MENSAGENS_PADRAO)
            print("Compilando e executando:", nome)
            resultados.append((caminho, compilar_e_rodar(caminho, msgs)))
        montar_pdf(resultados)
    finally:
        apagar_class()
    print("PDF gerado em:", SAIDA_PDF)


if __name__ == "__main__":
    main()
