package techlab.interfaces;

import java.sql.SQLException;
import java.util.List;
import techlab.model.AtivoTI;
import techlab.exception.EquipamentoNaoEncontradoException;

/**
 * Contrato de persistência CRUD para a entidade AtivoTI (Padrão DAO - Data Access Object).
 * Isola completamente as operações SQL da camada de apresentação (Main).
 */
public interface IAtivoDAO {

    /**
     * Insere um novo ativo no banco de dados com PreparedStatement.
     */
    void salvar(AtivoTI ativo) throws SQLException;

    /**
     * Busca um ativo específico pela sua chave primária.
     */
    AtivoTI buscarPorId(int id) throws EquipamentoNaoEncontradoException, SQLException;

    /**
     * Retorna a lista completa de ativos cadastrados no banco de dados.
     */
    List<AtivoTI> listarTodos() throws SQLException;

    /**
     * Atualiza o status operacional do equipamento ("Ativo", "EmManutencao", "Descartado").
     */
    void atualizarStatus(int id, String novoStatus) throws SQLException;

    /**
     * Remove um equipamento do banco de dados pelo seu ID.
     */
    void deletar(int id) throws SQLException;
}
