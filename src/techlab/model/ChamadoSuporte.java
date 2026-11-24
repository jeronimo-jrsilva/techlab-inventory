package techlab.model;

import techlab.interfaces.INotificavel; // Importa a interface que vamos implementar

public class ChamadoSuporte implements INotificavel {

    // 1. Contador estático para gerar IDs automaticamente para Chamados
    private static int proximoIdChamado = 0; // Começa em 0, o primeiro ID será 1

    // 2. Atributos da classe ChamadoSuporte
    private int id; // Agora gerado automaticamente
    private String titulo;
    private String descricao;
    private String prioridade; // "Baixa", "Media", "Alta"
    private String dataAbertura;
    private boolean aberto; // true se o chamado está aberto, false se está fechado
    private AtivoTI ativoRelacionado; // Associação: um chamado está ligado a um AtivoTI

    // 3. Construtor completo (ID não é mais um parâmetro)
    public ChamadoSuporte(String titulo, String descricao, String prioridade,
            String dataAbertura, AtivoTI ativoRelacionado) {
        this.id = ++proximoIdChamado; // Incrementa e atribui o ID antes de qualquer coisa
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.dataAbertura = dataAbertura;
        this.aberto = true; // Todo chamado nasce ABERTO por padrão
        this.ativoRelacionado = ativoRelacionado;
    }

    // 4. Implementação OBRIGATÓRIA do método da interface INotificavel
    @Override
    public void enviarAlerta(String mensagem) {
        System.out.println("\n[ALERTA SERVICE DESK - CHAMADO #" + this.id + "]: " + mensagem);
        // Exibe informações do ativo relacionado no alerta
        if (this.ativoRelacionado != null) {
            System.out.println("-> Equipamento Crítico: " + this.ativoRelacionado.getModelo() +
                    " (Patrimônio: " + this.ativoRelacionado.getPatrimonio() + ")");
        } else {
            System.out.println("-> Ativo relacionado: INDEFINIDO");
        }
        System.out.println("----------------------------------------------");
    }

    // 5. Método para fechar o chamado (exemplo de lógica de negócio)
    public void fecharChamado() {
        this.aberto = false;
        System.out.println("Chamado #" + this.id + " foi fechado.");
    }

    // 6. Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) { // Setter de ID mantido caso seja útil em algum cenário específico
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public boolean isAberto() { // Para booleanos, o getter geralmente começa com "is"
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public AtivoTI getAtivoRelacionado() {
        return ativoRelacionado;
    }

    public void setAtivoRelacionado(AtivoTI ativoRelacionado) {
        this.ativoRelacionado = ativoRelacionado;
    }

    // 7. Sobrescrita do toString() para exibição formatada do chamado
    @Override
    public String toString() {
        String statusChamado = aberto ? "Aberto" : "Fechado";
        String infoAtivo = (ativoRelacionado != null)
                ? ativoRelacionado.getModelo() + " (Pat: " + ativoRelacionado.getPatrimonio() + ")"
                : "N/A";

        return String.format("Chamado #%d | Título: %s | Prioridade: %s | Abertura: %s | Status: %s | Ativo: %s",
                id, titulo, prioridade, dataAbertura, statusChamado, infoAtivo);
    }
}
