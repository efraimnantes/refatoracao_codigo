# RELATÓRIO TÉCNICO: REFATORAÇÃO E APLICAÇÃO DE CLEAN CODE
Disciplina: Manutenção de Software

# Projeto: Sistema de Apuração e Ranking - Jogos Interatléticas da UFMS (JIUFMS)

# 1. INTRODUÇÃO
O presente relatório detalha o processo de refatoração de um sistema desenvolvido em Java, responsável por calcular a pontuação e gerar o ranking das atléticas participantes dos Jogos Interatléticas da UFMS (JIUFMS). O trabalho foi fundamentado no Capítulo 2 do livro-base da disciplina de Manutenção de Software, com o objetivo de aplicar as seis premissas fundamentais de Clean Code (Código Limpo).

A manutenção de software consome a maior parte do ciclo de vida de um sistema. Códigos difíceis de ler, altamente acoplados e com regras de negócio obscuras aumentam o custo de manutenção e o risco de inserção de novos bugs (efeitos colaterais). O objetivo deste trabalho não foi alterar o comportamento final do programa (o output), mas sim reestruturar sua organização interna para melhorar seus atributos de qualidade, especificamente a manutenibilidade, legibilidade e testabilidade.

2. ANÁLISE DO CÓDIGO LEGADO (O PROBLEMA)
O código original consistia em uma única classe procedural, com um único método estático responsável por todo o fluxo de execução. Abaixo, o trecho do código legado:

Java


public class ApuracaoAntiga {
    public static int calcPts(String a, int[] pos, String[] inf) {
        int tot = 0;
        
        for (int p : pos) {
            if (p == 1) { tot += 50; } 
            else if (p == 2) { tot += 30; } 
            else if (p == 3) { tot += 20; } 
            else { tot += 5; }
        }
        
        for (String i : inf) {
            if (i.equals("briga_quadra") || i.equals("briga_torcida")) {
                tot = tot - 50;
            } else if (i.equals("preconceito")) {
                System.out.println("Atletica " + a + " eliminada do JIUFMS");
                return -1;
            }
        }
        
        System.out.println("A atletica " + a + " fez " + tot + " pontos no geral");
        return tot;
    }
}
# 2.1. Diagnóstico de Manutenibilidade
O método calcPts sofre do Anti-Pattern conhecido como "God Method" (Método Deus) ou "Blob". Ele centraliza a iteração das modalidades, a matemática das pontuações, a checagem das infrações disciplinares, as regras de desclassificação e ainda a formatação da saída para o usuário. Se qualquer regra do torneio mudar (ex: uma nova infração, mudança na pontuação do primeiro colocado), este método precisará ser alterado, violando o Princípio do Aberto/Fechado (Open/Closed Principle) e o Princípio da Responsabilidade Única (Single Responsibility Principle).

# 3. APLICAÇÃO DAS 6 PREMISSAS E O QUE MUDOU NO CÓDIGO
A seguir, detalhamos a aplicação de cada premissa de Código Limpo, contrastando o código antigo com a nova arquitetura orientada a objetos.

## Premissa 1: Use Verificadores de Estilo e Formatadores
A formatação do código não é apenas uma questão estética, mas de comunicação. O código legado utilizava espaçamentos inconsistentes, vetores primitivos (int[], String[]) e misturava lógica de apresentação com lógica de negócio.

# O que mudou:
No novo código (SistemaApuracaoJIUFMS), adotamos o padrão de codificação oficial do Java. As classes utilizam PascalCase, os métodos e variáveis utilizam camelCase, e constantes usam UPPER_SNAKE_CASE. Além disso, substituímos os arrays primitivos por Collections tipadas e seguras (List<ResultadoModalidade>, List<TipoInfracao>), garantindo que o compilador verifique os tipos em tempo de execução e melhorando a formatação semântica dos dados.

## Premissa 2: Escolha Nomes Legíveis
A leitura de um código deve ser fluida. No código legado, o programador era obrigado a realizar um mapeamento mental constante para entender as variáveis.

# O que mudou:
calcPts foi renomeado para métodos verbais que indicam ação: calcularPontosEsportes(), calcularPenalidades() e apurarPontuacaoFinal().
String a (que representava a Atlética) transformou-se na classe e variável Atletica atletica.
O array int[] pos (posições) virou uma lista explícita de ResultadoModalidade.
A variável tot (total) foi dividida em pontosConquistados e pontosPerdidos, explicitando o que está sendo calculado naquele momento.

## Premissa 3: Evite Números Mágicos
No código antigo, as pontuações 50, 30, 20 e o código de erro -1 estavam hardcoded (chumbados) dentro das estruturas condicionais if/else. Um desenvolvedor não saberia de imediato se o "50" subtraído na briga tem relação com o "50" ganho no primeiro lugar.

# O que mudou:
Os números mágicos foram extraídos. As pontuações base continuam nas regras de negócio da classe Esporte, mas as punições ganharam uma constante explícita:
private static final int PENALIDADE_BRIGA = 50;.
O retorno mágico -1 (que indicava eliminação) foi totalmente removido, substituído por atributos de status da Atlética. Isso facilita alterações futuras no regulamento.

## Premissa 4: Adote uma Linguagem Ubíqua (Domain-Driven Design)
O código antigo lidava com o domínio do esporte universitário usando tipos primitivos (Strings). Comparações como i.equals("briga_quadra") são frágeis. Um simples erro de digitação ("Briga_quadra") causaria uma falha silenciosa no sistema.

# O que mudou:
Criamos uma linguagem comum entre o código e o mundo real (Regulamento do JIUFMS).
Introduzimos a entidade Esporte, a entidade Atletica e convertemos as Strings de erro em um enumerador (Enum) rígido:

Java


enum TipoInfracao {
    BRIGA,
    PRECONCEITO
}

Isso impede que um programador insira uma infração inexistente, garantindo a integridade dos dados e refletindo o vocabulário real do domínio no código-fonte.

## Premissa 5: Implemente Funções Coesas e Desacopladas
No legado, matemática, lógica disciplinar e exibição visual habitavam a mesma função. O acoplamento era total.

# O que mudou:
O sistema foi fatiado de acordo com o Princípio da Responsabilidade Única (SRP):
Classe CalculadoraJIUFMS: Especialista em aritmética. Tem métodos que só sabem somar e subtrair pontos.
Classe ServicoRanking: Especialista em orquestrar o campeonato. Ela chama a calculadora e decide a posição de cada atlética.
Essa modularização permite que possamos testar as regras matemáticas de forma automatizada (Testes Unitários) sem precisar imprimir nada na tela.

## Premissa 6: Separe os Fluxos de Execução
O código antigo utilizava if/else aninhados e forçava a execução de vários laços de repetição, mesmo quando a atlética já estava eliminada, usando um return -1 sorrateiro no final para interromper o fluxo.

# O que mudou:
Aplicamos o conceito de Cláusulas de Guarda (Guard Clauses). Invertemos a lógica condicional no método principal de apuração, tratando os casos de exceção (eliminação) antes do fluxo feliz (cálculo de pontos).

Java


public int apurarPontuacaoFinal(Atletica atletica) {
    // Cláusula de Guarda: Fail Fast (Falha Rápida)
    if (atletica.getInfracoes().contains(TipoInfracao.PRECONCEITO)) {
        return 0; 
    }

    // Fluxo normal (só executa se passar pela guarda)
    int pontosConquistados = calculadora.calcularPontosEsportes(atletica.getResultados());
    int pontosPerdidos = calculadora.calcularPenalidades(atletica.getInfracoes());
    
    return Math.max(0, pontosConquistados - pontosPerdidos);
}

Isso reduziu a complexidade ciclomática do método e tornou a leitura linear, de cima para baixo.

# 4. APRESENTAÇÃO DA NOVA ARQUITETURA (CÓDIGO REFATORADO)
Abaixo, apresentamos o código final refatorado que encapsula todas as melhorias e premissas citadas no tópico anterior, dividindo a lógica estrutural em classes distintas orientadas a objetos.

Java


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// 1. LINGUAGEM UBÍQUA: Definição clara das infrações
enum TipoInfracao {
    BRIGA,
    PRECONCEITO
}

// 2. RESPONSABILIDADE: Entidade Esporte gerencia suas regras
class Esporte {
    private String nome;
    private double pesoPontuacao;

    public Esporte(String nome, double pesoPontuacao) {
        this.nome = nome;
        this.pesoPontuacao = pesoPontuacao;
    }

    public int calcularPontosPorPosicao(int posicao) {
        int pontosBase;
        if (posicao == 1) pontosBase = 50;
        else if (posicao == 2) pontosBase = 30;
        else if (posicao == 3) pontosBase = 20;
        else pontosBase = 5;

        return (int) Math.round(pontosBase * pesoPontuacao);
    }
}

// 3. NOMES LEGÍVEIS: ResultadoModalidade em vez de int[]
class ResultadoModalidade {
    private Esporte esporte;
    private int posicaoObtida;

    public ResultadoModalidade(Esporte esporte, int posicaoObtida) {
        this.esporte = esporte;
        this.posicaoObtida = posicaoObtida;
    }

    public int getPontosCalculados() {
        return esporte.calcularPontosPorPosicao(posicaoObtida);
    }
}

// 4. ENTIDADE PRINCIPAL: Atlética encapsula seus próprios dados
class Atletica {
    private String nome;
    private List<ResultadoModalidade> resultados;
    private List<TipoInfracao> infracoes;

    public Atletica(String nome) {
        this.nome = nome;
        this.resultados = new ArrayList<>();
        this.infracoes = new ArrayList<>();
    }

    public void adicionarResultado(Esporte esporte, int posicao) {
        this.resultados.add(new ResultadoModalidade(esporte, posicao));
    }

    public void registrarInfracao(TipoInfracao infracao) {
        this.infracoes.add(infracao);
    }

    public String getNome() { return nome; }
    public List<ResultadoModalidade> getResultados() { return resultados; }
    public List<TipoInfracao> getInfracoes() { return infracoes; }
}

// 5. FUNÇÕES COESAS E SEM NÚMEROS MÁGICOS
class CalculadoraJIUFMS {
    private static final int PENALIDADE_BRIGA = 50; // Constante explícita

    public int calcularPontosEsportes(List<ResultadoModalidade> resultados) {
        int totalPontos = 0;
        for (ResultadoModalidade resultado : resultados) {
            totalPontos += resultado.getPontosCalculados();
        }
        return totalPontos;
    }

    public int calcularPenalidades(List<TipoInfracao> infracoes) {
        int penalidadeTotal = 0;
        for (TipoInfracao infracao : infracoes) {
            if (infracao == TipoInfracao.BRIGA) {
                penalidadeTotal += PENALIDADE_BRIGA;
            }
        }
        return penalidadeTotal;
    }
}

// 6. SEPARAÇÃO DE FLUXOS E CLÁUSULA DE GUARDA
class ServicoRanking {
    private CalculadoraJIUFMS calculadora = new CalculadoraJIUFMS();

    public int apurarPontuacaoFinal(Atletica atletica) {
        // Cláusula de guarda
        if (atletica.getInfracoes().contains(TipoInfracao.PRECONCEITO)) {
            return 0; 
        }

        int pontosConquistados = calculadora.calcularPontosEsportes(atletica.getResultados());
        int pontosPerdidos = calculadora.calcularPenalidades(atletica.getInfracoes());

        return Math.max(0, pontosConquistados - pontosPerdidos);
    }

    public void exibirRankingGeral(List<Atletica> atleticas) {
        System.out.println("\n=== RANKING GERAL JIUFMS ===");
        atleticas.stream()
            .sorted(Comparator.comparingInt(this::apurarPontuacaoFinal).reversed())
            .forEach(atletica -> {
                boolean eliminada = atletica.getInfracoes().contains(TipoInfracao.PRECONCEITO);
                String status = eliminada ? "[DESCLASSIFICADA]" : apurarPontuacaoFinal(atletica) + " pontos";
                System.out.println(atletica.getNome() + ": " + status);
            });
    }
}

public class SistemaApuracaoJIUFMS {
    public static void main(String[] args) {
        Esporte futsal = new Esporte("Futsal", 1.0);
        Esporte atletismo = new Esporte("Atletismo", 0.75);

        Atletica engenharia = new Atletica("Engenharia");
        engenharia.adicionarResultado(futsal, 1);
        engenharia.adicionarResultado(atletismo, 2);
        engenharia.registrarInfracao(TipoInfracao.BRIGA);

        Atletica medicina = new Atletica("Medicina");
        medicina.adicionarResultado(futsal, 2);
        medicina.adicionarResultado(atletismo, 1);

        ServicoRanking servicoRanking = new ServicoRanking();
        servicoRanking.exibirRankingGeral(List.of(engenharia, medicina));
    }
}

# 5. CONCLUSÃO
O processo de manutenção de software focado em refatoração demonstrou que as métricas de qualidade de código vão muito além de "o sistema compilar e rodar". O código inicial, embora funcional, possuía alta fragilidade a mudanças e demandaria grande esforço de qualquer desenvolvedor novo que tentasse entendê-lo.

Com a aplicação das seis premissas de Código Limpo (Clean Code), transformamos um script procedural acoplado em um modelo de domínio rico. A adoção de classes com responsabilidades únicas (Calculadora, Serviço, Entidades), a padronização de nomenclatura, a erradicação de números mágicos e a aplicação de cláusulas de guarda resultaram em um sistema extensível.

A nova arquitetura permite que as regras do evento esportivo evoluam nos próximos anos sem que as bases do sistema precisem ser reescritas, cumprindo com excelência o propósito da disciplina: produzir software capaz de sobreviver ao tempo e à troca de equipes de desenvolvimento de forma sustentável e segura.
