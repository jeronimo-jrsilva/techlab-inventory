package techlab.model;

public class Servidor extends AtivoTI {

    // 1. Atributos específicos da subclasse Servidor
    private String ipEstatico;
    private String sistemaOperacional;
    private int memoriaRam; // Em GB

    // 2. Construtor completo com ID gerado automaticamente pelo pai
    public Servidor(String patrimonio, String modelo, String dataAquisicao, String status,
            String ipEstatico, String sistemaOperacional, int memoriaRam) {
        super(patrimonio, modelo, dataAquisicao, status);
        this.ipEstatico = ipEstatico;
        this.sistemaOperacional = sistemaOperacional;
        this.memoriaRam = memoriaRam;
    }

    // 3. Sobrecarga com status padrão "Ativo" e ID automático
    public Servidor(String patrimonio, String modelo, String dataAquisicao,
            String ipEstatico, String sistemaOperacional, int memoriaRam) {
        super(patrimonio, modelo, dataAquisicao);
        this.ipEstatico = ipEstatico;
        this.sistemaOperacional = sistemaOperacional;
        this.memoriaRam = memoriaRam;
    }

    // 4. Construtores com ID explícito (útil para persistência e compatibilidade)
    public Servidor(int id, String patrimonio, String modelo, String dataAquisicao, String status,
            String ipEstatico, String sistemaOperacional, int memoriaRam) {
        super(id, patrimonio, modelo, dataAquisicao, status);
        this.ipEstatico = ipEstatico;
        this.sistemaOperacional = sistemaOperacional;
        this.memoriaRam = memoriaRam;
    }

    public Servidor(int id, String patrimonio, String modelo, String dataAquisicao,
            String ipEstatico, String sistemaOperacional, int memoriaRam) {
        super(id, patrimonio, modelo, dataAquisicao);
        this.ipEstatico = ipEstatico;
        this.sistemaOperacional = sistemaOperacional;
        this.memoriaRam = memoriaRam;
    }

    // 5. Implementação OBRIGATÓRIA do método abstrato do pai (Polimorfismo)
    // Regra de Negócio: Custo base de data center (R$ 250,00) + R$ 25,00 por GB de RAM
    @Override
    public double calcularCustoManutencaoMensal() {
        return 250.0 + (this.memoriaRam * 25.0);
    }

    // 6. Getters e Setters específicos
    public String getIpEstatico() {
        return ipEstatico;
    }

    public void setIpEstatico(String ipEstatico) {
        this.ipEstatico = ipEstatico;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public int getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(int memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    // 7. Sobrescrita do toString() reaproveitando o toString() do pai
    @Override
    public String toString() {
        return String.format("[Servidor] %s | IP: %s | SO: %s | RAM: %d GB",
                super.toString(), ipEstatico, sistemaOperacional, memoriaRam);
    }
}
