import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Premissa 4: Linguagem Ubíqua
enum TipoInfracao {
    BRIGA,
    PRECONCEITO
}

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

    public String getNome() { return nome; }
}

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

class CalculadoraJIUFMS {
    private static final int PENALIDADE_BRIGA = 50;

    // Premissa 5: Funções Coesas e Desacopladas
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

class ServicoRanking {
    private CalculadoraJIUFMS calculadora = new CalculadoraJIUFMS();

    public int apurarPontuacaoFinal(Atletica atletica) {
        // Premissa 6: Separar Fluxos de Execução (Guard Clause)
        if (atletica.getInfracoes().contains(TipoInfracao.PRECONCEITO)) {
            return 0; // Eliminada do torneio
        }

        int pontosConquistados = calculadora.calcularPontosEsportes(atletica.getResultados());
        int pontosPerdidos = calculadora.calcularPenalidades(atletica.getInfracoes());

        return Math.max(0, pontosConquistados - pontosPerdidos);
    }

    public void exibirRankingGeral(List<Atletica> atleticas) {
        System.out.println("\n=== 🏆 RANKING GERAL JIUFMS ===");
        
        atleticas.stream()
            .sorted(Comparator.comparingInt(this::apurarPontuacaoFinal).reversed())
            .forEach(atletica -> {
                boolean eliminada = atletica.getInfracoes().contains(TipoInfracao.PRECONCEITO);
                int pontos = apurarPontuacaoFinal(atletica);
                String status = eliminada ? "[DESCLASSIFICADA]" : pontos + " pontos";
                System.out.println("Atlética " + atletica.getNome() + ": " + status);
            });
    }
}

public class SistemaApuracaoJIUFMS {
    public static void main(String[] args) {
        Esporte futsal = new Esporte("Futsal", 1.0);
        Esporte atletismo = new Esporte("Atletismo", 0.75);

        Atletica engenharia = new Atletica("Engenharia");
        engenharia.adicionarResultado(futsal, 1); // 50 pts
        engenharia.adicionarResultado(atletismo, 2); // 23 pts (30 * 0.75)
        engenharia.registrarInfracao(TipoInfracao.BRIGA); // -50 pts

        Atletica medicina = new Atletica("Medicina");
        medicina.adicionarResultado(futsal, 2); // 30 pts
        medicina.adicionarResultado(atletismo, 1); // 38 pts (50 * 0.75)

        ServicoRanking servicoRanking = new ServicoRanking();
        servicoRanking.exibirRankingGeral(List.of(engenharia, medicina));
    }
}
