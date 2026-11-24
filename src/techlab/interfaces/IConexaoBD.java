package techlab.interfaces;

import java.sql.Connection;
import techlab.exception.FalhaConexaoBDException;

/**
 * Contrato de infraestrutura para gerenciamento desacoplado de conexões JDBC.
 * Permite alternar facilmente entre SQLite, MySQL ou PostgreSQL sem alterar o código de negócio.
 */
public interface IConexaoBD {

    /**
     * Abre e retorna uma conexão ativa com o banco de dados.
     * @return Connection JDBC aberta
     * @throws FalhaConexaoBDException caso o driver ou a URL sejam inválidos
     */
    Connection conectar() throws FalhaConexaoBDException;

    /**
     * Fecha com segurança a conexão ativa informada.
     * @param conexao Conexão JDBC a ser encerrada
     */
    void desconectar(Connection conexao);
}
