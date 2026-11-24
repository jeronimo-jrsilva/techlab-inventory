package techlab.model;

public class EquipamentoRede extends AtivoTI {

    // 1. Atributos específicos de Equipamento de Rede
    private int quantidadePortas;
    private String tipoEquipamento; // "Switch", "Roteador", "Firewall"
    private int velocidadeMbps;

    // 2. Construtor completo com ID gerado automaticamente pelo pai
    public EquipamentoRede(String patrimonio, String modelo, String dataAquisicao, String status,
            int quantidadePortas, String tipoEquipamento, int velocidadeMbps) {
        super(patrimonio, modelo, dataAquisicao, status);
        this.quantidadePortas = quantidadePortas;
        this.tipoEquipamento = tipoEquipamento;
        this.velocidadeMbps = velocidadeMbps;
    }

    // 3. Sobrecarga com status padrão "Ativo" e ID automático
    public EquipamentoRede(String patrimonio, String modelo, String dataAquisicao,
            int quantidadePortas, String tipoEquipamento, int velocidadeMbps) {
        super(patrimonio, modelo, dataAquisicao);
        this.quantidadePortas = quantidadePortas;
        this.tipoEquipamento = tipoEquipamento;
        this.velocidadeMbps = velocidadeMbps;
    }

    // 4. Construtores com ID explícito (útil para persistência e compatibilidade)
    public EquipamentoRede(int id, String patrimonio, String modelo, String dataAquisicao, String status,
            int quantidadePortas, String tipoEquipamento, int velocidadeMbps) {
        super(id, patrimonio, modelo, dataAquisicao, status);
        this.quantidadePortas = quantidadePortas;
        this.tipoEquipamento = tipoEquipamento;
        this.velocidadeMbps = velocidadeMbps;
    }

    public EquipamentoRede(int id, String patrimonio, String modelo, String dataAquisicao,
            int quantidadePortas, String tipoEquipamento, int velocidadeMbps) {
        super(id, patrimonio, modelo, dataAquisicao);
        this.quantidadePortas = quantidadePortas;
        this.tipoEquipamento = tipoEquipamento;
        this.velocidadeMbps = velocidadeMbps;
    }

    // 5. Implementação OBRIGATÓRIA do método abstrato (Polimorfismo)
    // Regra de Negócio: Taxa de rack (R$ 120,00) + R$ 8,00 por porta
    @Override
    public double calcularCustoManutencaoMensal() {
        return 120.0 + (this.quantidadePortas * 8.0);
    }

    // 6. Getters e Setters específicos
    public int getQuantidadePortas() {
        return quantidadePortas;
    }

    public void setQuantidadePortas(int quantidadePortas) {
        this.quantidadePortas = quantidadePortas;
    }

    public String getTipoEquipamento() {
        return tipoEquipamento;
    }

    public void setTipoEquipamento(String tipoEquipamento) {
        this.tipoEquipamento = tipoEquipamento;
    }

    public int getVelocidadeMbps() {
        return velocidadeMbps;
    }

    public void setVelocidadeMbps(int velocidadeMbps) {
        this.velocidadeMbps = velocidadeMbps;
    }

    // 7. Sobrescrita do toString()
    @Override
    public String toString() {
        return String.format("[Equipamento de Rede] %s | Tipo: %s | Portas: %d | Velocidade: %d Mbps",
                super.toString(), tipoEquipamento, quantidadePortas, velocidadeMbps);
    }
}
