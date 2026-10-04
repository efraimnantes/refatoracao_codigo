import java.util.List;
import java.util.ArrayList;

// 4. Adotar uma Linguagem Ubíqua
enum TipoInfracao {
    BRIGA,
    PRECONCEITO
}

class Atletica {
    private String nome;
    private List<Integer> posicoesModalidades;
    private List<TipoInfracao> infracoes;

    public Atletica(String nome, List<Integer> posicoes, List<TipoInfracao> infracoes) {
        this.nome = nome;
        this.posicoesModalidades = posicoes;
        this.infracoes = infracoes;
    }

    public String getNome() { return nome; }
    public List<Integer> getPosicoesModalidades() { return posicoesModalidades; }
    public List<TipoInfracao> getInfracoes() { return infracoes; }
}

class CalculadoraJIUFMS {
    // 3. Evitar Números Mágicos (Uso de Constantes)
    private static final int PONTOS_OURO = 50;
    private static final int PONTOS_PRATA = 30;
    private static final int PONTOS_BRONZE = 20;
    private static final int PONTOS_PARTICIPACAO = 5;

    private static final int PENALIDADE_BRIGA = 50;
    private static final int PENALIDADE_PRECONCEITO = 100;

    // 5. Funções Coesas e Desacopladas[cite: 1]
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
        // 6. Separar Fluxos de Execução (Cláusula de Guarda)[cite: 1]
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
        // Exemplo de uso aplicando: 1. Verificadores de Estilo e 2. Nomes Legíveis[cite: 1]
        List<Integer> posicoes = List.of(1, 2, 4); // Ouro, Prata, Participação
        List<TipoInfracao> infracoes = List.of(TipoInfracao.BRIGA);

        Atletica atletica = new Atletica("Engenharia", posicoes, infracoes);
        JuizGeral juiz = new JuizGeral();

        try {
            int pontuacaoFinal = juiz.apurarResultadoFinal(atletica);
            System.out.println("A Atlética " + atletica.getNome() + " encerrou o JIUFMS com " + pontuacaoFinal + " pontos na classificação geral.");
        } catch (IllegalArgumentException erro) {
            System.err.println(erro.getMessage());
        }
    }
}
