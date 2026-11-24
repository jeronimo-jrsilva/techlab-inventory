package techlab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import techlab.interfaces.IConexaoBD;
import techlab.exception.FalhaConexaoBDException;

/**
 * Gerenciador Singleton de conexões JDBC para o TechLab.
 * Utiliza por padrão SQLite em arquivo local ('techlab.db') para portabilidade total.
 */
public class FabricaConexao implements IConexaoBD {

    private static final String DRIVER_SQLITE = "org.sqlite.JDBC";
    private static final String URL_PADRAO = "jdbc:sqlite:techlab.db";
    private static FabricaConexao instancia;

    private FabricaConexao() {
        // Construtor privado (Padrão Singleton)
    }

    public static synchronized FabricaConexao getInstancia() {
        if (instancia == null) {
            instancia = new FabricaConexao();
        }
        return instancia;
    }

    @Override
    public Connection conectar() throws FalhaConexaoBDException {
        try {
            // Tenta carregar o driver JDBC do SQLite
            Class.forName(DRIVER_SQLITE);
            Connection conexao = DriverManager.getConnection(URL_PADRAO);
            inicializarTabelasSeNecessario(conexao);
            return conexao;
        } catch (ClassNotFoundException e) {
            throw new FalhaConexaoBDException(
                "Driver JDBC do SQLite ('sqlite-jdbc.jar') não foi localizado no classpath da aplicação.\n" +
                "Para utilizar a persistência relacional, inclua a biblioteca 'sqlite-jdbc' na execução."
            );
        } catch (SQLException e) {
            throw new FalhaConexaoBDException("Erro ao conectar ao banco SQLite em '" + URL_PADRAO + "': " + e.getMessage());
        }
    }

    @Override
    public void desconectar(Connection conexao) {
        if (conexao != null) {
            try {
                if (!conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                System.err.println("[AVISO] Erro ao fechar conexão JDBC: " + e.getMessage());
            }
        }
    }

    /**
     * Garante a criação DDL automática das tabelas caso o banco 'techlab.db' seja criado do zero.
     */
    private void inicializarTabelasSeNecessario(Connection conn) {
        String sqlAtivos = "CREATE TABLE IF NOT EXISTS ativos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo VARCHAR(30) NOT NULL, " +
                "patrimonio VARCHAR(50) UNIQUE NOT NULL, " +
                "modelo VARCHAR(100) NOT NULL, " +
                "data_aquisicao VARCHAR(20), " +
                "status VARCHAR(20) DEFAULT 'Ativo', " +
                "ip_estatico VARCHAR(45), " +
                "so VARCHAR(50), " +
                "memoria_ram INT, " +
                "quantidade_portas INT, " +
                "tipo_rede VARCHAR(30), " +
                "velocidade_mbps INT, " +
                "departamento VARCHAR(50), " +
                "usuario VARCHAR(100), " +
                "gpu_dedicada BOOLEAN" +
                ");";

        String sqlChamados = "CREATE TABLE IF NOT EXISTS chamados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo VARCHAR(100) NOT NULL, " +
                "descricao TEXT, " +
                "prioridade VARCHAR(20) NOT NULL, " +
                "data_abertura VARCHAR(20), " +
                "aberto BOOLEAN DEFAULT 1, " +
                "ativo_id INTEGER, " +
                "FOREIGN KEY (ativo_id) REFERENCES ativos(id)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlAtivos);
            stmt.execute(sqlChamados);
        } catch (SQLException e) {
            System.err.println("[AVISO] Falha ao verificar/inicializar DDL inicial: " + e.getMessage());
        }
    }
}
