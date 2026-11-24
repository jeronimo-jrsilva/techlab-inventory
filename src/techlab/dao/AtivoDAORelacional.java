package techlab.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import techlab.database.FabricaConexao;
import techlab.interfaces.IAtivoDAO;
import techlab.model.AtivoTI;
import techlab.model.Servidor;
import techlab.model.EquipamentoRede;
import techlab.model.EstacaoTrabalho;
import techlab.exception.EquipamentoNaoEncontradoException;
import techlab.exception.FalhaConexaoBDException;

/**
 * Implementação concreta de acesso a dados (DAO) utilizando JDBC e PreparedStatements.
 * Blindado contra ataques de SQL Injection e com suporte completo a polimorfismo de subclasses.
 */
public class AtivoDAORelacional implements IAtivoDAO {

    private final FabricaConexao fabrica = FabricaConexao.getInstancia();

    @Override
    public void salvar(AtivoTI ativo) throws SQLException {
        String sql = "INSERT INTO ativos (" +
                "tipo, patrimonio, modelo, data_aquisicao, status, " +
                "ip_estatico, so, memoria_ram, quantidade_portas, tipo_rede, velocidade_mbps, " +
                "departamento, usuario, gpu_dedicada) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = fabrica.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Campos universais (AtivoTI)
            stmt.setString(2, ativo.getPatrimonio());
            stmt.setString(3, ativo.getModelo());
            stmt.setString(4, ativo.getDataAquisicao());
            stmt.setString(5, ativo.getStatus());

            // Campos específicos mapeados por polimorfismo
            if (ativo instanceof Servidor) {
                Servidor s = (Servidor) ativo;
                stmt.setString(1, "Servidor");
                stmt.setString(6, s.getIpEstatico());
                stmt.setString(7, s.getSistemaOperacional());
                stmt.setInt(8, s.getMemoriaRam());
                stmt.setNull(9, java.sql.Types.INTEGER);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.INTEGER);
                stmt.setNull(12, java.sql.Types.VARCHAR);
                stmt.setNull(13, java.sql.Types.VARCHAR);
                stmt.setNull(14, java.sql.Types.BOOLEAN);
            } else if (ativo instanceof EquipamentoRede) {
                EquipamentoRede r = (EquipamentoRede) ativo;
                stmt.setString(1, "EquipamentoRede");
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.VARCHAR);
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setInt(9, r.getQuantidadePortas());
                stmt.setString(10, r.getTipoEquipamento());
                stmt.setInt(11, r.getVelocidadeMbps());
                stmt.setNull(12, java.sql.Types.VARCHAR);
                stmt.setNull(13, java.sql.Types.VARCHAR);
                stmt.setNull(14, java.sql.Types.BOOLEAN);
            } else if (ativo instanceof EstacaoTrabalho) {
                EstacaoTrabalho w = (EstacaoTrabalho) ativo;
                stmt.setString(1, "EstacaoTrabalho");
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.VARCHAR);
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setNull(9, java.sql.Types.INTEGER);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.INTEGER);
                stmt.setString(12, w.getDepartamento());
                stmt.setString(13, w.getUsuarioAlocado());
                stmt.setBoolean(14, w.isPossuiPlacaVideoDedicada());
            }

            stmt.executeUpdate();
        } catch (FalhaConexaoBDException e) {
            throw new SQLException("Falha de infraestrutura no banco de dados: " + e.getMessage(), e);
        }
    }

    @Override
    public AtivoTI buscarPorId(int id) throws EquipamentoNaoEncontradoException, SQLException {
        String sql = "SELECT * FROM ativos WHERE id = ?";

        try (Connection conn = fabrica.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaAtivo(rs);
                }
            }
        } catch (FalhaConexaoBDException e) {
            throw new SQLException("Falha ao abrir conexão JDBC: " + e.getMessage(), e);
        }

        throw new EquipamentoNaoEncontradoException(id);
    }

    @Override
    public List<AtivoTI> listarTodos() throws SQLException {
        List<AtivoTI> ativos = new ArrayList<>();
        String sql = "SELECT * FROM ativos ORDER BY id ASC";

        try (Connection conn = fabrica.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ativos.add(mapearResultSetParaAtivo(rs));
            }
        } catch (FalhaConexaoBDException e) {
            throw new SQLException("Falha de conexão com a base de dados: " + e.getMessage(), e);
        }

        return ativos;
    }

    @Override
    public void atualizarStatus(int id, String novoStatus) throws SQLException {
        String sql = "UPDATE ativos SET status = ? WHERE id = ?";

        try (Connection conn = fabrica.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhum equipamento foi atualizado. ID #" + id + " inexistente.");
            }
        } catch (FalhaConexaoBDException e) {
            throw new SQLException("Falha de conexão JDBC: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM ativos WHERE id = ?";

        try (Connection conn = fabrica.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhum ativo foi removido. ID #" + id + " não encontrado.");
            }
        } catch (FalhaConexaoBDException e) {
            throw new SQLException("Falha ao conectar para exclusão: " + e.getMessage(), e);
        }
    }

    /**
     * Mapeador Relacional (ORM manual): reconstrói o objeto especializado correto
     * a partir dos dados retornados da tabela SQLite.
     */
    private AtivoTI mapearResultSetParaAtivo(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String tipo = rs.getString("tipo");
        String patrimonio = rs.getString("patrimonio");
        String modelo = rs.getString("modelo");
        String dataAquisicao = rs.getString("data_aquisicao");
        String status = rs.getString("status");

        if ("Servidor".equalsIgnoreCase(tipo)) {
            String ip = rs.getString("ip_estatico");
            String so = rs.getString("so");
            int ram = rs.getInt("memoria_ram");
            return new Servidor(id, patrimonio, modelo, dataAquisicao, status, ip, so, ram);
        } else if ("EquipamentoRede".equalsIgnoreCase(tipo)) {
            int portas = rs.getInt("quantidade_portas");
            String tipoRede = rs.getString("tipo_rede");
            int velocidade = rs.getInt("velocidade_mbps");
            return new EquipamentoRede(id, patrimonio, modelo, dataAquisicao, status, portas, tipoRede, velocidade);
        } else if ("EstacaoTrabalho".equalsIgnoreCase(tipo)) {
            String depto = rs.getString("departamento");
            String usuario = rs.getString("usuario");
            boolean gpu = rs.getBoolean("gpu_dedicada");
            return new EstacaoTrabalho(id, patrimonio, modelo, dataAquisicao, status, depto, usuario, gpu);
        }

        // Fallback genérico caso o tipo não bata
        return new EstacaoTrabalho(id, patrimonio, modelo, dataAquisicao, status, "Geral", "Indefinido", false);
    }
}
