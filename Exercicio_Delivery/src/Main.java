import javax.swing.JOptionPane;

// Classe que representa um pedido
class Pedido {
    int id;
    String nomeCliente;
    String itens;
    String status;
    Pilha historicoAcoes;
    boolean isVip;

    public Pedido(int id, String nomeCliente, String itens, boolean isVip) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.itens = itens;
        this.status = "Pendente";
        this.historicoAcoes = new Pilha(100); // Tamanho arbitrário para a pilha
        this.isVip = isVip;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " - " + nomeCliente + " - " + itens + " - Status: " + status + (isVip ? " (VIP)" : "");
    }
}

// Classe principal do sistema
public class Main {
    private static Fila filaEspera = new Fila(100); // Fila de pedidos pendentes
    private static ListaEncadeada pedidosAtivos = new ListaEncadeada(); // Lista de pedidos em preparo
    private static int proximoId = 1; // Contador para gerar IDs únicos

    public static void main(String[] args) {
        int opcao;
        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "SISTEMA DE DELIVERY\n\n" +
                            "1. Adicionar novo pedido\n" +
                            "2. Aceitar pedido (mover para preparo)\n" +
                            "3. Registrar etapa de preparo\n" +
                            "4. Desfazer última etapa\n" +
                            "5. Finalizar preparo\n" +
                            "6. Visualizar pedidos ativos\n" +
                            "7. Consultar histórico de preparo\n" +
                            "8. Reencaminhar pedido finalizado\n" +
                            "9. Cancelar pedido\n" +
                            "0. Sair\n\n" +
                            "Escolha uma opção:"
            ));

            switch (opcao) {
                case 1:
                    adicionarPedido();
                    break;
                case 2:
                    aceitarPedido();
                    break;
                case 3:
                    registrarEtapa();
                    break;
                case 4:
                    desfazerEtapa();
                    break;
                case 5:
                    finalizarPreparo();
                    break;
                case 6:
                    visualizarPedidosAtivos();
                    break;
                case 7:
                    consultarHistorico();
                    break;
                case 8:
                    reencaminharPedido();
                    break;
                case 9:
                    cancelarPedido();
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Sistema encerrado.");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void adicionarPedido() {
        String nomeCliente = JOptionPane.showInputDialog("Nome do cliente:");
        String itens = JOptionPane.showInputDialog("Itens do pedido:");
        int respostaVip = JOptionPane.showConfirmDialog(null, "É um pedido VIP?", "VIP", JOptionPane.YES_NO_OPTION);
        boolean isVip = (respostaVip == JOptionPane.YES_OPTION);

        Pedido novoPedido = new Pedido(proximoId++, nomeCliente, itens, isVip);

        // Se for VIP, vamos simular prioridade na fila (funcionalidade extra)
        if (isVip) {
            // Para simular prioridade, vamos criar uma fila temporária
            Fila tempFila = new Fila(100);
            tempFila.enfileirar(novoPedido.id); // Adiciona o VIP primeiro

            // Transferir todos os pedidos normais para a fila temporária
            while (!filaEspera.vazia()) {
                tempFila.enfileirar(Integer.parseInt(filaEspera.desenfileirar()));
            }

            // Transferir de volta para a fila original
            while (!tempFila.vazia()) {
                filaEspera.enfileirar(Integer.parseInt(tempFila.desenfileirar()));
            }
        } else {
            filaEspera.enfileirar(novoPedido.id);
        }

        JOptionPane.showMessageDialog(null, "Pedido #" + novoPedido.id + " adicionado à fila de espera!");
    }

    private static void aceitarPedido() {
        if (filaEspera.vazia()) {
            JOptionPane.showMessageDialog(null, "Não há pedidos na fila de espera!");
            return;
        }

        int idPedido = Integer.parseInt(filaEspera.desenfileirar());
        Pedido pedido = buscarPedidoPorId(idPedido);

        if (pedido != null) {
            pedido.status = "Em preparo";

            // Criar um nó para a lista encadeada
            IntNoSimples novoNo = new IntNoSimples();
            novoNo.valor = pedido.id;
            novoNo.pedido = pedido; // Assumindo que adicionamos um campo Pedido na classe IntNoSimples

            pedidosAtivos.insereNo_fim(novoNo);
            JOptionPane.showMessageDialog(null, "Pedido #" + pedido.id + " movido para preparo!");
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado!");
        }
    }

    private static void registrarEtapa() {
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            String acao = JOptionPane.showInputDialog("Registrar etapa de preparo:");
            pedido.historicoAcoes.empilhar(acao);
            JOptionPane.showMessageDialog(null, "Ação registrada para o pedido #" + pedido.id);
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }

    private static void desfazerEtapa() {
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            Object acaoDesfeita = pedido.historicoAcoes.desempilhar();
            if (acaoDesfeita.equals("Pilha Vazia!")) {
                JOptionPane.showMessageDialog(null, "Não há ações para desfazer neste pedido!");
            } else {
                JOptionPane.showMessageDialog(null, "Ação desfeita: " + acaoDesfeita);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }

    private static void finalizarPreparo() {
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            pedido.status = "Finalizado";

            // Exibir histórico de ações
            StringBuilder historico = new StringBuilder();
            historico.append("Histórico de preparo do pedido #").append(pedido.id).append(":\n");

            // Para exibir a pilha na ordem correta, precisamos desempilhar e empilhar novamente
            Pilha tempPilha = new Pilha(100);
            Object acao;
            while (!pedido.historicoAcoes.vazia()) {
                acao = pedido.historicoAcoes.desempilhar();
                tempPilha.empilhar(acao);
                historico.append("- ").append(acao).append("\n");
            }

            // Restaurar a pilha original
            while (!tempPilha.vazia()) {
                pedido.historicoAcoes.empilhar(tempPilha.desempilhar());
            }

            JOptionPane.showMessageDialog(null, historico.toString());

            // Remover da lista de pedidos ativos
            pedidosAtivos.excluiNo(pedido.id);
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }

    private static void visualizarPedidosAtivos() {
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        StringBuilder lista = new StringBuilder("Pedidos em preparo:\n\n");
        IntNoSimples tempNo = pedidosAtivos.primeiro;

        while (tempNo != null) {
            Pedido pedido = tempNo.pedido;
            lista.append(pedido.toString()).append("\n");
            tempNo = tempNo.prox;
        }

        JOptionPane.showMessageDialog(null, lista.toString());
    }

    private static void consultarHistorico() {
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            StringBuilder historico = new StringBuilder();
            historico.append("Histórico do pedido #").append(pedido.id).append(":\n");

            // Similar ao método de finalizar preparo
            Pilha tempPilha = new Pilha(100);
            Object acao;
            while (!pedido.historicoAcoes.vazia()) {
                acao = pedido.historicoAcoes.desempilhar();
                tempPilha.empilhar(acao);
                historico.append("- ").append(acao).append("\n");
            }

            // Restaurar a pilha original
            while (!tempPilha.vazia()) {
                pedido.historicoAcoes.empilhar(tempPilha.desempilhar());
            }

            JOptionPane.showMessageDialog(null, historico.toString());
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }

    private static void reencaminharPedido() {
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido a ser reencaminhado:"));

        // Buscar o pedido (poderia ser em uma lista de finalizados, mas para simplificar vamos supor que está em memória)
        Pedido pedido = buscarPedidoPorId(idPedido);

        if (pedido != null) {
            pedido.status = "Pendente";
            pedido.historicoAcoes = new Pilha(100); // Limpar histórico

            filaEspera.enfileirar(pedido.id);
            JOptionPane.showMessageDialog(null, "Pedido #" + pedido.id + " reencaminhado para a fila de espera!");
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado!");
        }
    }

    private static void cancelarPedido() {
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido a ser cancelado:"));

        // Verificar se está na fila de espera
        Fila tempFila = new Fila(100);
        boolean encontrado = false;

        while (!filaEspera.vazia()) {
            int id = Integer.parseInt(filaEspera.desenfileirar());
            if (id == idPedido) {
                encontrado = true;
                JOptionPane.showMessageDialog(null, "Pedido #" + id + " cancelado e removido da fila de espera!");
            } else {
                tempFila.enfileirar(id);
            }
        }

        // Restaurar a fila original
        while (!tempFila.vazia()) {
            filaEspera.enfileirar(Integer.parseInt(tempFila.desenfileirar()));
        }

        // Se não estava na fila, verificar se está em preparo
        if (!encontrado) {
            IntNoSimples tempNo = pedidosAtivos.primeiro;
            IntNoSimples anterior = null;

            while (tempNo != null) {
                if (tempNo.valor == idPedido) {
                    if (anterior == null) {
                        pedidosAtivos.primeiro = tempNo.prox;
                    } else {
                        anterior.prox = tempNo.prox;
                    }

                    if (tempNo == pedidosAtivos.ultimo) {
                        pedidosAtivos.ultimo = anterior;
                    }

                    JOptionPane.showMessageDialog(null, "Pedido #" + idPedido + " cancelado e removido da lista de preparo!");
                    encontrado = true;
                    break;
                }

                anterior = tempNo;
                tempNo = tempNo.prox;
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado na fila de espera ou em preparo!");
        }
    }

    private static Pedido buscarPedidoPorId(int id) {
        // Em um sistema real, buscaríamos em um banco de dados ou estrutura de dados global
        // Para simplificar, vamos simular que sempre encontramos
        return new Pedido(id, "Cliente " + id, "Itens do pedido " + id, false);
    }

    private static Pedido buscarPedidoAtivoPorId(int id) {
        IntNoSimples tempNo = pedidosAtivos.primeiro;

        while (tempNo != null) {
            if (tempNo.valor == id) {
                return tempNo.pedido;
            }
            tempNo = tempNo.prox;
        }

        return null;
    }
}

// Modificação da classe IntNoSimples para incluir referência ao Pedido
class IntNoSimples {
    int valor;
    IntNoSimples prox;
    Pedido pedido; // Referência ao objeto Pedido
}