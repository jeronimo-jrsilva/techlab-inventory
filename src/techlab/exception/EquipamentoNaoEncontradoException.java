package techlab.exception;

/**
 * Exceção de negócio lançada quando uma busca por ID ou Patrimônio
 * não localiza o equipamento correspondente no inventário ou banco de dados.
 */
public class EquipamentoNaoEncontradoException extends Exception {

    public EquipamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public EquipamentoNaoEncontradoException(int id) {
        super("Equipamento de tecnologia com ID #" + id + " não foi localizado no sistema.");
    }
}
