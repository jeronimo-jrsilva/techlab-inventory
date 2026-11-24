package techlab.exception;

// Exceção customizada (Checked Exception) para tratar falhas de conexão com o Banco de Dados
public class FalhaConexaoBDException extends Exception {

    // Construtor que recebe apenas a mensagem de erro
    public FalhaConexaoBDException(String mensagem) {
        super(mensagem);
    }

    // Construtor que recebe a mensagem e a causa original (outra exceção, ex:
    // SQLException)
    public FalhaConexaoBDException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
