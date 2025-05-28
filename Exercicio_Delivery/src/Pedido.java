
class Pedido { // Classe que representa um pedido no sistema de delivery
    // Atributos do pedido
    int id;                 // Identificador único do pedido
    String nomeCliente;     // Nome do cliente que fez o pedido
    String itens;           // Descrição dos itens do pedido
    String status;          // Status atual do pedido (Pendente, Em preparo, Finalizado)
    Pilha historicoAcoes;   // Pilha para armazenar o histórico de ações/etapas do pedido
    boolean isVip;          // Flag para indicar se é um pedido VIP (com prioridade)

    // Construtor da classe Pedido
    public Pedido(int id, String nomeCliente, String itens, boolean isVip) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.itens = itens;
        this.status = "Pendente";                    // Status inicial sempre é "Pendente"
        this.historicoAcoes = new Pilha(100);   // Inicializa pilha com capacidade 100
        this.isVip = isVip;                          // Define se é VIP ou não
    }

    // Método para representação textual do pedido
    @Override
    public String toString() {
        // Formata os dados do pedido para exibição
        return "Pedido #" + id + " - " + nomeCliente + " - " + itens + " - Status: " + status + (isVip ? " (VIP)" : "");
    }


}