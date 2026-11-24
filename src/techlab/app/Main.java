package techlab.app;

import java.util.Scanner;
import java.util.List;
import java.sql.SQLException;

import techlab.model.AtivoTI;
import techlab.model.Servidor;
import techlab.model.EquipamentoRede;
import techlab.model.EstacaoTrabalho;
import techlab.model.ChamadoSuporte;
import techlab.dao.AtivoDAORelacional;
import techlab.interfaces.IAtivoDAO;

/**
 * Ponto de entrada interativo do TechLab Inventory & Service Desk.
 * Demonstração prática de:
 *  - Encapsulamento rígido e validação defensiva de entrada (Regex de IP, datas e rejeição de números)
 *  - Herança e Especialização (Servidor, EquipamentoRede, EstacaoTrabalho)
 *  - Polimorfismo dinâmico (calcularCustoManutencaoMensal e toString)
 *  - Associação entre entidades (ChamadoSuporte <-> AtivoTI)
 *  - Interfaces e Contratos (INotificavel, IConexaoBD, IAtivoDAO)
 *  - Padrão DAO e Persistência Relacional com PreparedStatements
 */
public class Main {

    private static final int CAPACIDADE_MAXIMA = 100;

    // Repositório em memória para Ativos
    private static AtivoTI[] inventarioDeAtivos = new AtivoTI[CAPACIDADE_MAXIMA];
    private static int totalAtivos = 0;

    // Repositório em memória para Chamados
    private static ChamadoSuporte[] listaDeChamados = new ChamadoSuporte[CAPACIDADE_MAXIMA];
    private static int totalChamados = 0;

    // DAO Relacional (Persistência)
    private static final IAtivoDAO ativoDAO = new AtivoDAORelacional();

    // =========================================================================
    // MÉTODOS ROBUSTOS DE VALIDAÇÃO DE ENTRADA (SCANNER BLINDADO)
    // =========================================================================

    /**
     * Lê uma string não vazia.
     */
    public static String lerTextoObrigatorio(Scanner leitor, String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = leitor.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("  [ERRO] Este campo não pode ficar vazio. Tente novamente.");
        }
    }

    /**
     * Lê texto estritamente alfabético (impede números e caracteres especiais onde só deve haver letras).
     * Aceita letras (com acentos), espaços e hifens.
     */
    public static String lerApenasLetras(Scanner leitor, String prompt) {
        while (true) {
            String texto = lerTextoObrigatorio(leitor, prompt);
            if (texto.matches("^[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-]+$")) {
                return texto;
            }
            System.out.println("  [ERRO] Entrada inválida! Digite apenas letras (números e caracteres especiais são proibidos).");
        }
    }

    /**
     * Valida e lê endereço IP no formato IPv4 padrão (ex: 192.168.1.10).
     */
    public static String lerIpValido(Scanner leitor, String prompt) {
        String regexIpv4 = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        while (true) {
            String ip = lerTextoObrigatorio(leitor, prompt);
            if (ip.matches(regexIpv4)) {
                return ip;
            }
            System.out.println("  [ERRO] Formato de IP inválido! Use o padrão IPv4 correto (ex: 192.168.1.10).");
        }
    }

    /**
     * Valida e lê datas estritamente no formato ISO AAAA-MM-DD.
     */
    public static String lerDataIsoValida(Scanner leitor, String prompt) {
        String regexData = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
        while (true) {
            String data = lerTextoObrigatorio(leitor, prompt);
            if (data.matches(regexData)) {
                return data;
            }
            System.out.println("  [ERRO] Data inválida! Digite no formato AAAA-MM-DD (ex: 2026-03-15).");
        }
    }

    /**
     * Lê um número inteiro dentro de uma faixa [min, max] sem estourar InputMismatchException.
     */
    public static int lerInteiroNaFaixa(Scanner leitor, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String linha = leitor.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("  [ERRO] Por favor, digite um valor entre %d e %d.\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [ERRO] Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    /**
     * Lê um número inteiro estritamente positivo (> 0).
     */
    public static int lerInteiroPositivo(Scanner leitor, String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = leitor.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor > 0) {
                    return valor;
                }
                System.out.println("  [ERRO] O valor deve ser maior do que zero.");
            } catch (NumberFormatException e) {
                System.out.println("  [ERRO] Entrada inválida! Digite um número inteiro válido.");
            }
        }
    }

    /**
     * Lê resposta booleana de forma flexível e segura (S/N, Sim/Não, 1/0, True/False).
     */
    public static boolean lerBooleanoSimNao(Scanner leitor, String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = leitor.nextLine().trim().toLowerCase();
            if (entrada.equals("s") || entrada.equals("sim") || entrada.equals("true") || entrada.equals("1")) {
                return true;
            }
            if (entrada.equals("n") || entrada.equals("nao") || entrada.equals("não") || entrada.equals("false") || entrada.equals("0")) {
                return false;
            }
            System.out.println("  [ERRO] Opção inválida! Digite 'S' para Sim ou 'N' para Não.");
        }
    }

    /**
     * Lê o status do ativo garantindo os valores aceitos pela regra de negócio.
     */
    public static String lerStatusAtivo(Scanner leitor) {
        System.out.println("  Selecione o Status:");
        System.out.println("    1 - Ativo");
        System.out.println("    2 - Em Manutenção");
        System.out.println("    3 - Descartado");
        int opcao = lerInteiroNaFaixa(leitor, "  Escolha (1-3) [padrão=1]: ", 1, 3);
        switch (opcao) {
            case 2: return "EmManutencao";
            case 3: return "Descartado";
            default: return "Ativo";
        }
    }

    /**
     * Lê a prioridade de um chamado de suporte.
     */
    public static String lerPrioridadeChamado(Scanner leitor) {
        System.out.println("  Selecione a Prioridade do Chamado:");
        System.out.println("    1 - Baixa");
        System.out.println("    2 - Média");
        System.out.println("    3 - Alta");
        int opcao = lerInteiroNaFaixa(leitor, "  Escolha (1-3): ", 1, 3);
        switch (opcao) {
            case 1: return "Baixa";
            case 3: return "Alta";
            default: return "Media";
        }
    }

    // =========================================================================
    // OPERAÇÕES DO INVENTÁRIO & SERVICE DESK
    // =========================================================================

    public static void cadastrarNovoAtivo(Scanner leitor) {
        if (totalAtivos >= CAPACIDADE_MAXIMA) {
            System.out.println("\n[ALERTA] Inventário cheio! Capacidade máxima de " + CAPACIDADE_MAXIMA + " ativos atingida.");
            return;
        }

        System.out.println("\n==============================================");
        System.out.println("         CADASTRO DE NOVO ATIVO DE TI         ");
        System.out.println("==============================================");
        System.out.println("  1 - Servidor");
        System.out.println("  2 - Equipamento de Rede");
        System.out.println("  3 - Estação de Trabalho");
        int tipo = lerInteiroNaFaixa(leitor, "Selecione o tipo de ativo (1-3): ", 1, 3);

        System.out.println("\n--- Dados Gerais do Ativo ---");
        String patrimonio = lerTextoObrigatorio(leitor, "Código de Patrimônio (ex: PAT-2026-001): ");
        String modelo = lerTextoObrigatorio(leitor, "Modelo do Equipamento (ex: Dell PowerEdge R740): ");
        String dataAquisicao = lerDataIsoValida(leitor, "Data de Aquisição (AAAA-MM-DD): ");
        String status = lerStatusAtivo(leitor);

        AtivoTI novoAtivo = null;

        switch (tipo) {
            case 1: // Servidor
                System.out.println("\n--- Especificações de Servidor ---");
                String ipEstatico = lerIpValido(leitor, "Endereço IP Estático (ex: 192.168.1.10): ");
                String so = lerTextoObrigatorio(leitor, "Sistema Operacional: ");
                int ram = lerInteiroPositivo(leitor, "Memória RAM instalada (em GB): ");
                novoAtivo = new Servidor(patrimonio, modelo, dataAquisicao, status, ipEstatico, so, ram);
                break;

            case 2: // Equipamento de Rede
                System.out.println("\n--- Especificações de Rede ---");
                int portas = lerInteiroPositivo(leitor, "Quantidade de Portas Físicas: ");
                String tipoRede = lerApenasLetras(leitor, "Tipo de Equipamento (Switch / Roteador / Firewall): ");
                int velocidade = lerInteiroPositivo(leitor, "Velocidade da Porta (em Mbps, ex: 1000): ");
                novoAtivo = new EquipamentoRede(patrimonio, modelo, dataAquisicao, status, portas, tipoRede, velocidade);
                break;

            case 3: // Estação de Trabalho
                System.out.println("\n--- Especificações de Estação de Trabalho ---");
                String departamento = lerApenasLetras(leitor, "Departamento (apenas letras, ex: Financeiro): ");
                String usuario = lerApenasLetras(leitor, "Usuário Alocado (apenas letras, ex: Maria Silva): ");
                boolean gpuDedicada = lerBooleanoSimNao(leitor, "Possui Placa de Vídeo Dedicada? (S/N): ");
                novoAtivo = new EstacaoTrabalho(patrimonio, modelo, dataAquisicao, status, departamento, usuario, gpuDedicada);
                break;
        }

        if (novoAtivo != null) {
            inventarioDeAtivos[totalAtivos] = novoAtivo;
            totalAtivos++;
            System.out.println("\n[SUCESSO] Ativo cadastrado na memória com ID #" + novoAtivo.getId() + " (" + novoAtivo.getModelo() + ").");

            // Pergunta se deseja persistir no banco de dados via DAO
            boolean persistirNoBD = lerBooleanoSimNao(leitor, "Deseja salvar este ativo também no Banco de Dados (DAO)? (S/N): ");
            if (persistirNoBD) {
                try {
                    ativoDAO.salvar(novoAtivo);
                    System.out.println("  [DAO] Ativo gravado com sucesso na tabela 'ativos' do SQLite!");
                } catch (SQLException e) {
                    System.out.println("  [AVISO DAO] Não foi possível persistir no banco: " + e.getMessage());
                }
            }
        }
    }

    public static void listarAtivos() {
        System.out.println("\n==========================================================================================");
        System.out.println("                    INVENTÁRIO DE ATIVOS — DEMONSTRAÇÃO DE POLIMORFISMO                    ");
        System.out.println("==========================================================================================");

        if (totalAtivos == 0) {
            System.out.println("Nenhum ativo cadastrado na memória até o momento.");
            return;
        }

        double custoTotalMensal = 0.0;
        for (int i = 0; i < totalAtivos; i++) {
            AtivoTI ativo = inventarioDeAtivos[i];
            double custoMensal = ativo.calcularCustoManutencaoMensal();
            custoTotalMensal += custoMensal;

            System.out.printf("[%02d] %s\n", (i + 1), ativo.toString());
            System.out.printf("     └─> Regra Polimórfica de Custo Mensal: R$ %.2f\n", custoMensal);
            System.out.println("------------------------------------------------------------------------------------------");
        }
        System.out.printf("TOTAL DE ATIVOS: %d | CUSTO DE MANUTENÇÃO GLOBAL ESTIMADO: R$ %.2f / mês\n", totalAtivos, custoTotalMensal);
    }

    public static void listarAtivosDoBanco() {
        System.out.println("\n==========================================================================================");
        System.out.println("               CONSULTA DE ATIVOS NO BANCO DE DADOS (VIA DAO RELACIONAL)                   ");
        System.out.println("==========================================================================================");
        try {
            List<AtivoTI> ativosBD = ativoDAO.listarTodos();
            if (ativosBD.isEmpty()) {
                System.out.println("Nenhum ativo registrado na tabela 'ativos' do banco SQLite.");
                return;
            }
            for (AtivoTI a : ativosBD) {
                System.out.printf("• %s | Custo: R$ %.2f\n", a.toString(), a.calcularCustoManutencaoMensal());
            }
            System.out.printf("\nTotal de registros persistidos no SQLite: %d\n", ativosBD.size());
        } catch (SQLException e) {
            System.out.println("[AVISO DAO] Falha ao consultar o banco de dados: " + e.getMessage());
        }
    }

    public static AtivoTI buscarAtivoPorId(int idBuscado) {
        for (int i = 0; i < totalAtivos; i++) {
            if (inventarioDeAtivos[i].getId() == idBuscado) {
                return inventarioDeAtivos[i];
            }
        }
        return null;
    }

    public static void abrirChamadoSuporte(Scanner leitor) {
        if (totalAtivos == 0) {
            System.out.println("\n[AVISO] Não há ativos cadastrados. Cadastre ao menos um ativo antes de abrir chamados.");
            return;
        }

        if (totalChamados >= CAPACIDADE_MAXIMA) {
            System.out.println("\n[ALERTA] Capacidade máxima de chamados atingida (" + CAPACIDADE_MAXIMA + ").");
            return;
        }

        System.out.println("\n==============================================");
        System.out.println("           ABRIR CHAMADO DE SUPORTE           ");
        System.out.println("==============================================");

        System.out.println("Ativos disponíveis para vínculo:");
        for (int i = 0; i < totalAtivos; i++) {
            System.out.printf("  ID #%d -> %s (Patrimônio: %s)\n",
                    inventarioDeAtivos[i].getId(),
                    inventarioDeAtivos[i].getModelo(),
                    inventarioDeAtivos[i].getPatrimonio());
        }

        int idEscolhido = lerInteiroPositivo(leitor, "\nInforme o ID do ativo relacionado: ");
        AtivoTI ativoRelacionado = buscarAtivoPorId(idEscolhido);

        if (ativoRelacionado == null) {
            System.out.println("  [ERRO] Ativo com ID #" + idEscolhido + " não foi encontrado no inventário!");
            return;
        }

        String titulo = lerTextoObrigatorio(leitor, "Título do Chamado: ");
        String descricao = lerTextoObrigatorio(leitor, "Descrição detalhada do problema: ");
        String prioridade = lerPrioridadeChamado(leitor);
        String dataAbertura = lerDataIsoValida(leitor, "Data de Abertura (AAAA-MM-DD): ");

        ChamadoSuporte novoChamado = new ChamadoSuporte(titulo, descricao, prioridade, dataAbertura, ativoRelacionado);

        // Disparo de notificação via interface INotificavel
        novoChamado.enviarAlerta("Novo chamado de suporte registrado na fila de atendimento!");

        listaDeChamados[totalChamados] = novoChamado;
        totalChamados++;

        System.out.println("[SUCESSO] Chamado #" + novoChamado.getId() + " aberto com sucesso!");
    }

    public static void fecharChamadoSuporte(Scanner leitor) {
        if (totalChamados == 0) {
            System.out.println("\n[AVISO] Nenhum chamado registrado no momento.");
            return;
        }

        System.out.println("\n==============================================");
        System.out.println("            FECHAR CHAMADO DE SUPORTE         ");
        System.out.println("==============================================");

        int idBuscado = lerInteiroPositivo(leitor, "Informe o ID do chamado a ser fechado: ");
        for (int i = 0; i < totalChamados; i++) {
            if (listaDeChamados[i].getId() == idBuscado) {
                if (!listaDeChamados[i].isAberto()) {
                    System.out.println("  [INFO] O chamado #" + idBuscado + " já se encontra fechado.");
                    return;
                }
                listaDeChamados[i].fecharChamado();
                return;
            }
        }
        System.out.println("  [ERRO] Chamado #" + idBuscado + " não foi localizado.");
    }

    public static void listarChamados() {
        System.out.println("\n==========================================================================================");
        System.out.println("                         SERVICE DESK — HISTÓRICO DE CHAMADOS                             ");
        System.out.println("==========================================================================================");

        if (totalChamados == 0) {
            System.out.println("Nenhum chamado registrado até o momento.");
            return;
        }

        for (int i = 0; i < totalChamados; i++) {
            System.out.println(listaDeChamados[i]);
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    /**
     * Carrega massa de testes instantânea para apresentação rápida para o professor.
     */
    public static void carregarMassaDeDemonstracao() {
        if (totalAtivos > 0) {
            System.out.println("\n[INFO] O inventário já contém ativos cadastrados. Demonstração mantida sem duplicar.");
            return;
        }

        // 1. Servidor
        Servidor s1 = new Servidor("PAT-2026-SRV01", "Dell PowerEdge R740", "2026-02-15", "Ativo",
                "192.168.1.10", "Ubuntu Server 24.04 LTS", 64);
        inventarioDeAtivos[totalAtivos++] = s1;

        // 2. Equipamento de Rede
        EquipamentoRede r1 = new EquipamentoRede("PAT-2026-SW01", "Cisco Catalyst 2960X", "2026-03-01", "Ativo",
                48, "Switch", 1000);
        inventarioDeAtivos[totalAtivos++] = r1;

        // 3. Estação de Trabalho
        EstacaoTrabalho w1 = new EstacaoTrabalho("PAT-2026-WS01", "Lenovo ThinkStation P360", "2026-04-10", "Ativo",
                "Desenvolvimento", "Jeronimo Silva", true);
        inventarioDeAtivos[totalAtivos++] = w1;

        // Chamados associados
        ChamadoSuporte c1 = new ChamadoSuporte("Queda de Link Primário", "Switch perdeu conectividade no uplink de fibra.", "Alta", "2026-11-20", r1);
        c1.enviarAlerta("Urgência: Falha de conectividade detectada no Switch principal!");
        listaDeChamados[totalChamados++] = c1;

        ChamadoSuporte c2 = new ChamadoSuporte("Upgrade de Driver GPU", "Atualizar drivers CUDA/ROCm para modelagem de IA.", "Baixa", "2026-11-22", w1);
        listaDeChamados[totalChamados++] = c2;

        System.out.println("\n[SUCESSO] 3 Ativos especializados e 2 Chamados foram carregados com sucesso!");
    }

    // =========================================================================
    // LOOP PRINCIPAL (MENU INTERATIVO)
    // =========================================================================

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        boolean executando = true;

        System.out.println("==========================================================");
        System.out.println("   TECHLAB INVENTORY & SERVICE DESK — SENAC CEARÁ 2026.2  ");
        System.out.println("   Aluno: Jeronimo Silva | Prof.: Me. Tibério Soares       ");
        System.out.println("==========================================================");

        while (executando) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("  1 - Cadastrar Novo Ativo de TI");
            System.out.println("  2 - Listar Inventário (Demonstração de Polimorfismo)");
            System.out.println("  3 - Abrir Chamado de Suporte (Demonstração INotificavel)");
            System.out.println("  4 - Fechar Chamado de Suporte");
            System.out.println("  5 - Listar Chamados de Suporte");
            System.out.println("  6 - Carregar Dados de Demonstração (Carga Rápida)");
            System.out.println("  7 - Consultar Banco de Dados SQLite (Demonstração DAO)");
            System.out.println("  0 - Sair do Sistema");

            int opcao = lerInteiroNaFaixa(leitor, "Escolha uma opção (0-7): ", 0, 7);

            switch (opcao) {
                case 1:
                    cadastrarNovoAtivo(leitor);
                    break;
                case 2:
                    listarAtivos();
                    break;
                case 3:
                    abrirChamadoSuporte(leitor);
                    break;
                case 4:
                    fecharChamadoSuporte(leitor);
                    break;
                case 5:
                    listarChamados();
                    break;
                case 6:
                    carregarMassaDeDemonstracao();
                    break;
                case 7:
                    listarAtivosDoBanco();
                    break;
                case 0:
                    System.out.println("\nEncerrando o TechLab Inventory. Até logo!");
                    executando = false;
                    break;
            }
        }

        leitor.close();
    }
}
