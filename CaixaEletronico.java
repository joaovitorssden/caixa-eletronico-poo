import java.util.ArrayList;
import java.util.List;

/**
 * Classe CaixaEletronico - implementa a logica de um caixa eletronico.
 *
 * Trabalha com uma matriz 6x2 onde:
 *   coluna 0 = valor da cedula
 *   coluna 1 = quantidade disponivel daquela cedula
 *
 * Regras principais:
 *  - Saque sempre prioriza cedulas de maior valor
 *  - Maximo de 30 cedulas por saque
 *  - Se saldo ficar abaixo da cota minima, caixa para de operar
 *  - Extrato completo exibido ao sair
 *
 * @author Grupo POO
 * @version 1.0
 */
public class CaixaEletronico implements ICaixaEletronico {

    // -------------------------------------------------------
    // Constantes
    // -------------------------------------------------------

    /** Numero maximo de cedulas que podem ser emitidas em um unico saque. */
    private static final int MAX_CEDULAS_POR_SAQUE = 30;

    // -------------------------------------------------------
    // Atributos
    // -------------------------------------------------------

    /**
     * Matriz 6x2 de cedulas.
     * cedulas[i][0] = valor da cedula
     * cedulas[i][1] = quantidade disponivel
     * Ordenada da maior para a menor nota (100, 50, 20, 10, 5, 2).
     */
    private int[][] cedulas;

    /** Cota minima de operacao. Se saldo total < cotaMinima, caixa bloqueia saques. */
    private int cotaMinima;

    /** Historico de todas as transacoes realizadas (para o extrato). */
    private List<String> extrato;

    /** Saldo total no momento de cada transacao, para exibir no extrato. */
    private List<Integer> saldoAposTransacao;

    // -------------------------------------------------------
    // Construtor
    // -------------------------------------------------------

    /**
     * Inicializa o caixa eletronico com quantidades padrao de cedulas
     * conforme especificado no enunciado do projeto.
     */
    public CaixaEletronico() {
        // Inicializa a matriz com valores e quantidades padrao
        cedulas = new int[][] {
            {100, 100},
            { 50, 200},
            { 20, 300},
            { 10, 350},
            {  5, 450},
            {  2, 500}
        };

        cotaMinima = 0; // sem cota minima por padrao
        extrato = new ArrayList<>();
        saldoAposTransacao = new ArrayList<>();
    }

    // -------------------------------------------------------
    // Metodos da interface ICaixaEletronico
    // -------------------------------------------------------

    /**
     * Calcula e retorna o valor total disponivel no caixa.
     * Soma (valor_cedula * quantidade) para todas as linhas da matriz.
     *
     * @return String formatada com o valor total
     */
    @Override
    public String pegaValorTotalDisponivel() {
        int total = calcularSaldoTotal();
        return "Valor total disponivel no caixa: R$ " + total + ",00";
    }

    /**
     * Efetua um saque seguindo as regras:
     *  1. Verifica se o caixa esta acima da cota minima
     *  2. Tenta montar o troco usando cedulas da maior para a menor
     *  3. Limita a 30 cedulas no total
     *  4. So desconta as cedulas se o saque for 100% possivel
     *  5. Registra no extrato
     *
     * @param valor valor inteiro a ser sacado
     * @return String formatada com o resultado da operacao
     */
    @Override
    public String sacar(Integer valor) {
        // Verifica se o caixa esta operacional (acima da cota minima)
        if (calcularSaldoTotal() < cotaMinima) {
            return "Caixa Vazio: Chame o Operador";
        }

        // Validacao basica
        if (valor == null || valor <= 0) {
            return "Valor de saque invalido. Informe um valor positivo.";
        }

        // Verifica se o caixa tem saldo suficiente
        if (valor > calcularSaldoTotal()) {
            return "Saque nao realizado por falta de cedulas";
        }

        // Tenta montar o saque (algoritmo greedy - maiores notas primeiro)
        int[] quantidadesUsadas = new int[cedulas.length];
        int restante = valor;
        int totalCedulas = 0;

        for (int i = 0; i < cedulas.length; i++) {
            int valorCedula = cedulas[i][0];
            int disponivel   = cedulas[i][1];

            if (valorCedula <= restante && disponivel > 0) {
                // Quantas cedulas deste valor precisamos?
                int necessarias = restante / valorCedula;
                // Nao pode usar mais do que tem disponivel
                int usar = Math.min(necessarias, disponivel);
                // Nao pode ultrapassar o limite total de 30 cedulas
                usar = Math.min(usar, MAX_CEDULAS_POR_SAQUE - totalCedulas);

                quantidadesUsadas[i] = usar;
                restante      -= usar * valorCedula;
                totalCedulas  += usar;
            }

            // Se ja atingiu o limite de cedulas, para
            if (totalCedulas >= MAX_CEDULAS_POR_SAQUE) {
                break;
            }
        }

        // Se ainda sobrou restante, o saque nao pode ser completado
        if (restante > 0) {
            return "Saque nao realizado por falta de cedulas";
        }

        // Verifica limite de 30 cedulas
        if (totalCedulas > MAX_CEDULAS_POR_SAQUE) {
            return "Saque nao realizado: numero maximo de cedulas (30) excedido";
        }

        // Tudo certo - desconta as cedulas da matriz
        for (int i = 0; i < cedulas.length; i++) {
            cedulas[i][1] -= quantidadesUsadas[i];
        }

        // Monta a mensagem de resposta
        StringBuilder sb = new StringBuilder();
        sb.append("=== SAQUE REALIZADO ===\n");
        sb.append("Valor sacado: R$ ").append(valor).append(",00\n");
        sb.append("Cedulas entregues:\n");

        for (int i = 0; i < cedulas.length; i++) {
            if (quantidadesUsadas[i] > 0) {
                sb.append("  Nota R$ ").append(cedulas[i][0])
                  .append(",00 -> ").append(quantidadesUsadas[i]).append(" cedula(s)\n");
            }
        }

        sb.append("Total de cedulas emitidas: ").append(totalCedulas).append("\n");
        sb.append("Saldo atual: R$ ").append(calcularSaldoTotal()).append(",00");

        // Verifica se apos o saque o caixa ficou abaixo da cota minima
        if (calcularSaldoTotal() < cotaMinima) {
            sb.append("\n\n*** ATENCAO: Caixa Vazio - Chame o Operador ***");
        }

        // Registra no extrato
        String entradaExtrato = "SAQUE: R$ " + valor + ",00 | " + totalCedulas + " cedula(s)";
        extrato.add(entradaExtrato);
        saldoAposTransacao.add(calcularSaldoTotal());

        return sb.toString();
    }

    /**
     * Retorna o relatorio detalhado das cedulas disponiveis no caixa.
     * Exibe valor e quantidade de cada tipo de nota.
     *
     * @return String formatada com o relatorio de cedulas
     */
    @Override
    public String pegaRelatorioCedulas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATORIO DE CEDULAS ===\n");
        sb.append(String.format("%-15s %-15s%n", "Valor (R$)", "Quantidade"));
        sb.append("--------------------------------\n");

        for (int[] linha : cedulas) {
            sb.append(String.format("%-15s %-15s%n",
                "R$ " + linha[0] + ",00",
                linha[1] + " cedula(s)"));
        }

        sb.append("--------------------------------\n");
        sb.append("Total em caixa: R$ ").append(calcularSaldoTotal()).append(",00");

        return sb.toString();
    }

    /**
     * Repoe cedulas de um determinado valor no caixa.
     * Aceita apenas cedulas dos valores validos (2, 5, 10, 20, 50, 100).
     *
     * @param cedula    valor da cedula a repor
     * @param quantidade quantidade de cedulas a adicionar
     * @return String formatada com o resultado da reposicao
     */
    @Override
    public String reposicaoCedulas(Integer cedula, Integer quantidade) {
        // Validacoes de entrada
        if (cedula == null || quantidade == null) {
            return "Erro: cedula e quantidade nao podem ser nulos.";
        }
        if (quantidade <= 0) {
            return "Erro: quantidade deve ser maior que zero.";
        }

        // Busca a linha correspondente ao valor da cedula
        for (int[] linha : cedulas) {
            if (linha[0] == cedula) {
                linha[1] += quantidade;

                String msg = "Reposicao realizada com sucesso!\n"
                        + "Cedula R$ " + cedula + ",00: adicionadas " + quantidade + " unidade(s).\n"
                        + "Quantidade atual de R$ " + cedula + ",00: " + linha[1] + " cedula(s).\n"
                        + "Saldo total: R$ " + calcularSaldoTotal() + ",00";

                // Registra reposicao no extrato
                extrato.add("REPOSICAO: " + quantidade + "x R$ " + cedula + ",00");
                saldoAposTransacao.add(calcularSaldoTotal());

                return msg;
            }
        }

        // Cedula invalida
        return "Erro: cedula de R$ " + cedula + ",00 nao e valida.\n"
             + "Valores aceitos: 2, 5, 10, 20, 50, 100.";
    }

    /**
     * Armazena a cota minima de operacao do caixa.
     * Quando o saldo total cair a este valor ou abaixo, saques sao bloqueados.
     *
     * @param minimo valor minimo de operacao
     * @return String formatada confirmando o armazenamento
     */
    @Override
    public String armazenaCotaMinima(Integer minimo) {
        if (minimo == null || minimo < 0) {
            return "Erro: cota minima deve ser um valor positivo.";
        }

        this.cotaMinima = minimo;
        return "Cota minima definida: R$ " + minimo + ",00\n"
             + "O caixa ira bloquear saques quando o saldo atingir este valor.";
    }

    // -------------------------------------------------------
    // Metodos auxiliares (nao fazem parte da interface)
    // -------------------------------------------------------

    /**
     * Calcula o saldo total disponivel no caixa.
     * Percorre a matriz somando valor * quantidade para cada tipo de cedula.
     *
     * @return soma total em reais
     */
    public int calcularSaldoTotal() {
        int total = 0;
        for (int[] linha : cedulas) {
            total += linha[0] * linha[1]; // valor * quantidade
        }
        return total;
    }

    /**
     * Gera o extrato completo de todas as transacoes realizadas na sessao.
     * Exibido ao clicar no botao Sair da interface grafica.
     *
     * @return String formatada com o extrato completo
     */
    public String gerarExtrato() {
        if (extrato.isEmpty()) {
            return "=== EXTRATO DA SESSAO ===\nNenhuma transacao realizada.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("          EXTRATO DA SESSAO             \n");
        sb.append("========================================\n");

        for (int i = 0; i < extrato.size(); i++) {
            sb.append(String.format("%-3d | %s%n", (i + 1), extrato.get(i)));
            sb.append(String.format("    | Saldo apos: R$ %d,00%n", saldoAposTransacao.get(i)));
            sb.append("    |-------------------------------\n");
        }

        sb.append("========================================\n");
        sb.append("Saldo final: R$ ").append(calcularSaldoTotal()).append(",00\n");
        sb.append("Total de transacoes: ").append(extrato.size());

        return sb.toString();
    }

    /**
     * Retorna a cota minima configurada (util para a GUI).
     *
     * @return valor da cota minima
     */
    public int getCotaMinima() {
        return cotaMinima;
    }

    // -------------------------------------------------------
    // Metodo main - inicializa a interface grafica
    // -------------------------------------------------------

    /**
     * Ponto de entrada da aplicacao.
     * Cria a janela principal e exibe para o usuario.
     *
     * @param args argumentos da linha de comando (nao utilizados)
     */
    public static void main(String[] args) {
        // Instancia a janela da GUI passando a propria classe
        javax.swing.SwingUtilities.invokeLater(() -> {
            GUI janela = new GUI(CaixaEletronico.class);
            janela.setVisible(true);
        });
    }
}
