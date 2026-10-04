# 📄 Relatório e Documentação: Refatoração de Código Limpo (JIUFMS)

## 🎯 Contexto do Trabalho
Este repositório contém a documentação e o trabalho prático da disciplina de **Manutenção de Software**, focado no aprendizado e na aplicação de conceitos de *Clean Code* (Código Limpo) com base no Capítulo 2 do livro *Fundamentos de Manutenção de Software*.

O objetivo é transformar um código legado e de difícil compreensão em uma aplicação legível, clara e manutenível, permitindo que os desenvolvedores foquem sua energia no entendimento da lógica do programa.

---

## 🛠️ As 6 Premissas de Código Limpo Aplicadas
A refatoração do sistema de apuração do **JIUFMS** (Jogos Interatléticas da UFMS) foi realizada com base em 6 diretrizes fundamentais:

1. **Use Verificadores de Estilo e Formatadores**: Padronização do código segundo as convenções oficiais da linguagem Java.
2. **Escolha Nomes Legíveis**: Identificadores descritivos que tornam o código auto-documentado.
3. **Evite Números Mágicos**: Substituição de valores numéricos soltos por constantes explícitas.
4. **Adote uma Linguagem Ubíqua**: Criação de modelos e entidades de negócio que refletem o vocabulário real do evento (`Atletica`, `TipoInfracao`, `JuizGeral`).
5. **Implemente Funções Coesas e Desacopladas**: Separação estrita entre o cálculo matemático de pontuação e as regras disciplinares de arbitragem.
6. **Separe os Fluxos de Execução**: Uso de *Guard Clauses* (cláusulas de guarda) para tratar desclassificações e infrações graves sem aninhar o código.

---

## 💩 Código Original ("Antes")
O código original em Java apresenta um método procedural único, sem orientação a objetos, com variáveis abreviadas, números sem significado explícito e tratamento de exceção misturado com o fluxo de execução.

```java
// ApuracaoAntiga.java
public class ApuracaoAntiga {
    public static int calcPts(String a, int[] pos, String[] inf) {
        int tot = 0;
        
        for (int p : pos) {
            if (p == 1) {
                tot += 50;
            } else {
                if (p == 2) {
                    tot += 30;
                } else {
                    if (p == 3) {
                        tot += 20;
                    } else {
                        tot += 5;
                    }
                }
            }
        }
        
        for (String i : inf) {
            if (i.equals("briga_quadra") || i.equals("briga_torcida")) {
                tot = tot - 50;
            } else {
                if (i.equals("preconceito")) {
                    System.out.println("Atletica " + a + " eliminada do JIUFMS");
                    return -1;
                }
            }
        }
        
        System.out.println("A atletica " + a + " fez " + tot + " pontos no geral");
        return tot;
    }
}
```

---

## ✨ Código Refatorado ("Limpo")
A versão refatorada adota o paradigma de Orientação a Objetos em Java, aplicando estruturas limpas para garantir facilidade de manutenção.

```java
// SistemaApuracaoJIUFMS.java
import java.util.List;

// Premissa 4: Adotar uma Linguagem Ubíqua
enum TipoInfracao {
    BRIGA,
    PRECONCEITO
}

class Atletica {
    private String nome;
    private List<Integer> posicoesModalidades;
    private List<TipoInfracao> infracoes;

    public Atletica(String nome, List<Integer> posicoesModalidades, List<TipoInfracao> infracoes) {
        this.nome = nome;
        this.posicoesModalidades = posicoesModalidades;
        this.infracoes = infracoes;
    }

    public String getNome() { return nome; }
    public List<Integer> getPosicoesModalidades() { return posicoesModalidades; }
    public List<TipoInfracao> getInfracoes() { return infracoes; }
}

class CalculadoraJIUFMS {
    // Premissa 3: Evitar Números Mágicos
    private static final int PONTOS_OURO = 50;
    private static final int PONTOS_PRATA = 30;
    private static final int PONTOS_BRONZE = 20;
    private static final int PONTOS_PARTICIPACAO = 5;

    private static final int PENALIDADE_BRIGA = 50;
    private static final int PENALIDADE_PRECONCEITO = 100;

    // Premissa 5: Implementar Funções Coesas e Desacopladas
    public int calcularPontosModalidades(List<Integer> posicoes) {
        int pontos = 0;
        for (int posicao : posicoes) {
            if (posicao == 1) pontos += PONTOS_OURO;
            else if (posicao == 2) pontos += PONTOS_PRATA;
            else if (posicao == 3) pontos += PONTOS_BRONZE;
            else pontos += PONTOS_PARTICIPACAO;
        }
        return pontos;
    }

    public int calcularPenalidades(List<TipoInfracao> infracoes) {
        int penalidadeTotal = 0;
        for (TipoInfracao infracao : infracoes) {
            if (infracao == TipoInfracao.BRIGA) {
                penalidadeTotal += PENALIDADE_BRIGA;
            } else if (infracao == TipoInfracao.PRECONCEITO) {
                penalidadeTotal += PENALIDADE_PRECONCEITO;
            }
        }
        return penalidadeTotal;
    }
}

class JuizGeral {
    private CalculadoraJIUFMS calculadora;

    public JuizGeral() {
        this.calculadora = new CalculadoraJIUFMS();
    }

    public int apurarResultadoFinal(Atletica atletica) {
        // Premissa 6: Separar Fluxos de Execução (Cláusula de Guarda)
        if (atletica.getInfracoes().contains(TipoInfracao.PRECONCEITO)) {
            throw new IllegalArgumentException("Desclassificação: A Atlética " + atletica.getNome() + " cometeu infração gravíssima (Preconceito).");
        }

        int pontosConquistados = calculadora.calcularPontosModalidades(atletica.getPosicoesModalidades());
        int pontosPerdidos = calculadora.calcularPenalidades(atletica.getInfracoes());

        return pontosConquistados - pontosPerdidos;
    }
}

public class SistemaApuracaoJIUFMS {
    public static void main(String[] args) {
        // Premissa 1 e 2: Verificadores de Estilo e Nomes Legíveis
        List<Integer> posicoes = List.of(1, 2, 4);
        List<TipoInfracao> infracoes = List.of(TipoInfracao.BRIGA);

        Atletica atletica = new Atletica("Engenharia", posicoes, infracoes);
        JuizGeral juiz = new JuizGeral();

        try {
            int pontuacaoFinal = juiz.apurarResultadoFinal(atletica);
            System.out.println("A Atlética " + atletica.getNome() + " encerrou o JIUFMS com " + pontuacaoFinal + " pontos.");
        } catch (IllegalArgumentException erro) {
            System.err.println(erro.getMessage());
        }
    }
}
```

---

## 📊 Relatório Comparativo das 6 Premissas

A tabela abaixo compara o impacto da aplicação de cada premissa no código-fonte original e no código-fonte agora "Limpo".

| Premissa | ❌ Problema no Código Original | ✅ Solução no Código Limpo |
| :--- | :--- | :--- |
| **1. Use Verificadores de Estilo e Formatadores** | Indentação inconsistente e ausência de tipagem explícita com *Generics*. | Código formatado rigorosamente nos padrões Java, aumentando a consistência e clareza. |
| **2. Escolha Nomes Legíveis** | Variáveis e métodos extremamente curtos e vagos (`calcPts`, `a`, `pos`, `inf`, `tot`, `p`). | Uso de identificadores descritivos e auto-documentados (`apurarResultadoFinal`, `posicoesModalidades`, `infracoes`). |
| **3. Evite Números Mágicos** | Valores literais espalhados no meio das validações (`50`, `30`, `20`, `-1`). | Substituição por constantes declaradas explicitamente no topo da classe (`PONTOS_OURO`, `PENALIDADE_BRIGA`). |
| **4. Adote uma Linguagem Ubíqua** | Validação através de *strings* simples e descontextualizadas (`"briga_quadra"`). | Criação de entidades concretas do domínio do torneio (`Atletica`, `TipoInfracao`, `JuizGeral`). |
| **5. Implemente Funções Coesas e Desacopladas** | Função única lidando com matemática, regras do campeonato e mensagens de console simultaneamente. | Separação estrita de responsabilidades: `CalculadoraJIUFMS` soma pontos e `JuizGeral` aplica regras disciplinares. |
| **6. Separe os Fluxos de Execução** | Aninhamento profundo de regras `if/else` e uso de código de erro velado (`-1`). | Aplicação de *Guard Clause* no início do método `apurarResultadoFinal`, lançando uma exceção imediatamente em caso de desclassificação. |
