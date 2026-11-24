package techlab.model;

public abstract class AtivoTI {

    // 1. Contador estático para geração automática de IDs únicos
    private static int proximoIdAtivo = 0;

    // 2. Atributos Privados (Encapsulamento rígido)
    private int id;
    private String patrimonio;     // Código de tombamento (ex: "PAT-2026-001")
    private String modelo;         // Modelo do equipamento (ex: "Dell PowerEdge R740")
    private String dataAquisicao;  // Data de aquisição (ex: "2026-01-15")
    private String status;         // "Ativo", "EmManutencao" ou "Descartado"

    // 3. Construtor Completo com geração automática de ID
    public AtivoTI(String patrimonio, String modelo, String dataAquisicao, String status) {
        this.id = ++proximoIdAtivo;
        this.patrimonio = patrimonio;
        this.modelo = modelo;
        this.dataAquisicao = dataAquisicao;
        setStatus(status); // Chama o setter para usar a validação de status
    }

    // 4. Sobrecarga do Construtor (Status padrão é "Ativo" e ID automático)
    public AtivoTI(String patrimonio, String modelo, String dataAquisicao) {
        this(patrimonio, modelo, dataAquisicao, "Ativo");
    }

    // 5. Construtor com ID explícito (útil para persistência/banco de dados)
    public AtivoTI(int id, String patrimonio, String modelo, String dataAquisicao, String status) {
        this.id = id;
        if (id > proximoIdAtivo) {
            proximoIdAtivo = id;
        }
        this.patrimonio = patrimonio;
        this.modelo = modelo;
        this.dataAquisicao = dataAquisicao;
        setStatus(status);
    }

    // 6. Sobrecarga com ID explícito e status padrão
    public AtivoTI(int id, String patrimonio, String modelo, String dataAquisicao) {
        this(id, patrimonio, modelo, dataAquisicao, "Ativo");
    }

    // 7. Método Abstrato (Polimorfismo Obrigatório)
    // As subclasses (Servidor, EstacaoTrabalho, etc.) SERÃO OBRIGADAS a implementar como calculam seu custo.
    public abstract double calcularCustoManutencaoMensal();

    // 8. Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(String dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && (status.equalsIgnoreCase("Ativo") ||
                               status.equalsIgnoreCase("EmManutencao") ||
                               status.equalsIgnoreCase("Descartado"))) {
            this.status = status;
        } else {
            this.status = "Ativo"; // Valor padrão caso venha nulo ou algo inválido
        }
    }

    // 9. Sobrescrita do toString() para exibição formatada
    @Override
    public String toString() {
        return String.format("ID: %d | Patrimônio: %s | Modelo: %s | Aquisição: %s | Status: %s | Custo Manutenção: R$ %.2f",
                id, patrimonio, modelo, dataAquisicao, status, calcularCustoManutencaoMensal());
    }
}
