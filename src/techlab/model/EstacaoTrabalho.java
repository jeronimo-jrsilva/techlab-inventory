package techlab.model;

public class EstacaoTrabalho extends AtivoTI {

    // 1. Atributos específicos de Estação de Trabalho (Workstation/PC de Usuário)
    private String departamento;
    private String usuarioAlocado;
    private boolean possuiPlacaVideoDedicada;

    // 2. Construtor completo com ID gerado automaticamente pelo pai
    public EstacaoTrabalho(String patrimonio, String modelo, String dataAquisicao, String status,
            String departamento, String usuarioAlocado, boolean possuiPlacaVideoDedicada) {
        super(patrimonio, modelo, dataAquisicao, status);
        this.departamento = departamento;
        this.usuarioAlocado = usuarioAlocado;
        this.possuiPlacaVideoDedicada = possuiPlacaVideoDedicada;
    }

    // 3. Sobrecarga com status padrão "Ativo" e ID automático
    public EstacaoTrabalho(String patrimonio, String modelo, String dataAquisicao,
            String departamento, String usuarioAlocado, boolean possuiPlacaVideoDedicada) {
        super(patrimonio, modelo, dataAquisicao);
        this.departamento = departamento;
        this.usuarioAlocado = usuarioAlocado;
        this.possuiPlacaVideoDedicada = possuiPlacaVideoDedicada;
    }

    // 4. Construtores com ID explícito (útil para persistência e compatibilidade)
    public EstacaoTrabalho(int id, String patrimonio, String modelo, String dataAquisicao, String status,
            String departamento, String usuarioAlocado, boolean possuiPlacaVideoDedicada) {
        super(id, patrimonio, modelo, dataAquisicao, status);
        this.departamento = departamento;
        this.usuarioAlocado = usuarioAlocado;
        this.possuiPlacaVideoDedicada = possuiPlacaVideoDedicada;
    }

    public EstacaoTrabalho(int id, String patrimonio, String modelo, String dataAquisicao,
            String departamento, String usuarioAlocado, boolean possuiPlacaVideoDedicada) {
        super(id, patrimonio, modelo, dataAquisicao);
        this.departamento = departamento;
        this.usuarioAlocado = usuarioAlocado;
        this.possuiPlacaVideoDedicada = possuiPlacaVideoDedicada;
    }

    // 5. Implementação OBRIGATÓRIA do método abstrato (Polimorfismo)
    // Regra de Negócio: Suporte N1 base (R$ 80,00) + R$ 40,00 caso tenha GPU dedicada
    @Override
    public double calcularCustoManutencaoMensal() {
        return 80.0 + (this.possuiPlacaVideoDedicada ? 40.0 : 0.0);
    }

    // 6. Getters e Setters específicos
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getUsuarioAlocado() {
        return usuarioAlocado;
    }

    public void setUsuarioAlocado(String usuarioAlocado) {
        this.usuarioAlocado = usuarioAlocado;
    }

    public boolean isPossuiPlacaVideoDedicada() {
        return possuiPlacaVideoDedicada;
    }

    public void setPossuiPlacaVideoDedicada(boolean possuiPlacaVideoDedicada) {
        this.possuiPlacaVideoDedicada = possuiPlacaVideoDedicada;
    }

    // 7. Sobrescrita do toString()
    @Override
    public String toString() {
        return String.format("[Estação de Trabalho] %s | Depto: %s | Usuário: %s | GPU Dedicada: %s",
                super.toString(), departamento, usuarioAlocado, possuiPlacaVideoDedicada ? "Sim" : "Não");
    }
}
