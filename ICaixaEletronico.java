/**
 * Interface (contrato) para utilizacao da interface grafica.
 * Nesse contrato e definido as operacoes de entrada e saida de dinheiro do caixa eletronico.
 *
 * Os metodos desta interface NAO podem ser alterados.
 */
public interface ICaixaEletronico {

    /**
     * Pega o valor total disponivel no caixa eletronico.
     * @return uma string formatada com o valor total disponivel
     */
    public String pegaValorTotalDisponivel();

    /**
     * Efetua o saque.
     * @param valor a ser sacado
     * @return uma string formatada informando o resultado da operacao
     */
    public String sacar(Integer valor);

    /**
     * Pega um relatorio informando as cedulas e a quantidade disponivel.
     * @return uma string formatada com as cedulas e suas quantidades
     */
    public String pegaRelatorioCedulas();

    /**
     * Efetua a reposicao de cedulas.
     * @param cedula valor da cedula a ser reposta
     * @param quantidade quantidade de cedulas para reposicao
     * @return uma string formatada informando o resultado da operacao
     */
    public String reposicaoCedulas(Integer cedula, Integer quantidade);

    /**
     * Armazena a cota minima de atendimento.
     * Se o saldo cair abaixo deste valor, o caixa para de atender.
     * @param minimo valor minimo para operacao
     * @return uma string formatada informando o resultado da operacao
     */
    public String armazenaCotaMinima(Integer minimo);
}
