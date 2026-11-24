package techlab.interfaces;

// Uma interface é um "contrato". Qualquer classe que implemente esta interface
// deve obrigatoriamente fornecer uma implementação para todos os seus métodos.
public interface INotificavel {

    // Método abstrato (implicitamente public abstract)
    // Classes que implementarem INotificavel precisarão definir como enviar um
    // alerta.
    void enviarAlerta(String mensagem);
}
