import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;


// Classe principal do sistema de delivery
public class Main {
    // Estruturas de dados globais
    private static Fila filaEspera = new Fila(100);                     // Fila de pedidos pendentes (capacidade 100)
    private static ListaEncadeada pedidosAtivos = new ListaEncadeada();     // Lista de pedidos em preparo
    private static int proximoId = 1;                                       // Contador para gerar IDs sequenciais
    private static Map<Integer, Pedido> todosPedidos = new HashMap<>();

    // Método principal que inicia o sistema
    public static void main(String[] args) {
        int opcao;  // Variável para armazenar a opção do menu selecionada

        // Loop principal do sistema
        do {
            // Exibe o menu principal e obtém a opção do usuário
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "SISTEMA DE DELIVERY\n" +
                            "-------------------------------------------------\n" +

                            "1. Adicionar novo pedido\n" +
                            "2. Mover para preparo\n" +
                            "3. Registrar etapa de preparo\n" +
                            "4. Desfazer última etapa\n" +
                            "5. Finalizar preparo\n" +
                            "6. Visualizar pedidos ativos\n" +
                            "7. Consultar histórico de preparo\n" +
                            "8. Reencaminhar pedido finalizado\n" +
                            "0. Sair\n" +
                            "-------------------------------------------------\n\n" +
                            "Escolha uma opção:"
            ));

            // Switch para tratar cada opção do menu
            switch (opcao) {
                case 1:
                    adicionarPedido();      // Chama metodo para adicionar novo pedido
                    break;
                case 2:
                    aceitarPedido();        // Chama metodo para mover pedido para preparo
                    break;
                case 3:
                    registrarEtapa();      // Chama metodo para registrar etapa de preparo
                    break;
                case 4:
                    desfazerEtapa();       // Chama metodo para desfazer última etapa
                    break;
                case 5:
                    finalizarPreparo();    // Chama metodo para finalizar preparo do pedido
                    break;
                case 6:
                    visualizarPedidosAtivos(); // Chama metodo para listar pedidos em preparo
                    break;
                case 7:
                    consultarHistorico();  // Chama metodo para consultar histórico de um pedido
                    break;
                case 8:
                    reencaminharPedido();  // Chama metodo para reencaminhar pedido finalizado
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Sistema encerrado."); // Encerra o sistema
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida!"); // Trata opção inválida
            }
        } while (opcao != 0);  // Repete até o usuário escolher sair (opção 0)
    }

    // Metodo para adicionar um novo pedido ao sistema
    private static void adicionarPedido() {
        String nomeCliente = JOptionPane.showInputDialog("Nome do cliente:");
        String itens = JOptionPane.showInputDialog("Itens do pedido:");
        int respostaVip = JOptionPane.showConfirmDialog(null, "É um pedido VIP?", "VIP", JOptionPane.YES_NO_OPTION);
        boolean isVip = (respostaVip == JOptionPane.YES_OPTION);

        Pedido novoPedido = new Pedido(proximoId, nomeCliente, itens, isVip);
        todosPedidos.put(proximoId, novoPedido); // Armazena o pedido no mapa
        proximoId++;

        if (isVip) {
            Fila tempFila = new Fila(100);
            tempFila.enfileirar(novoPedido.id);
            while (!filaEspera.vazia()) {
                tempFila.enfileirar(Integer.parseInt(filaEspera.desenfileirar()));
            }
            while (!tempFila.vazia()) {
                filaEspera.enfileirar(Integer.parseInt(tempFila.desenfileirar()));
            }
        } else {
            filaEspera.enfileirar(novoPedido.id);
        }

        JOptionPane.showMessageDialog(null, "Pedido #" + novoPedido.id + " adicionado à fila de espera!");
    }
    // Metodo para aceitar um pedido da fila de espera e mover para preparo
    private static void aceitarPedido() {
        if (filaEspera.vazia()) {
            JOptionPane.showMessageDialog(null, "Não há pedidos na fila de espera!");
            return;
        }

        int idPedido = Integer.parseInt(filaEspera.desenfileirar());
        Pedido pedido = buscarPedidoPorId(idPedido);

        if (pedido != null) {
            pedido.status = "Em preparo";

            IntNoSimples novoNo = new IntNoSimples();
            novoNo.valor = pedido.id;
            novoNo.pedido = pedido;  // Associa o objeto Pedido ao nó

            pedidosAtivos.insereNo_fim(novoNo);
            JOptionPane.showMessageDialog(null, "Pedido #" + pedido.id + " movido para preparo!");
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado!");
        }
    }
    // Metodo para registrar uma nova etapa no preparo de um pedido
    private static void registrarEtapa() {
        // Verifica se há pedidos em preparo
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        // Solicita o ID do pedido ao usuário
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            // Solicita a descrição da etapa/acao
            String acao = JOptionPane.showInputDialog("Registrar etapa de preparo:");

            // Armazena a ação no histórico (pilha) do pedido
            pedido.historicoAcoes.empilhar(acao);

            // Confirma a operação
            JOptionPane.showMessageDialog(null, "Ação registrada para o pedido #" + pedido.id);
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }
    // metodo para desfazer a última etapa registrada de um pedido
    private static void desfazerEtapa() {
        // Verifica se há pedidos em preparo
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        // Solicita o ID do pedido ao usuário
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            // Remove a última ação do histórico (topo da pilha)
            Object acaoDesfeita = pedido.historicoAcoes.desempilhar();

            // Verifica se havia ações para desfazer
            if (acaoDesfeita.equals("Pilha Vazia!")) {
                JOptionPane.showMessageDialog(null, "Não há ações para desfazer neste pedido!");
            } else {
                // Informa ao usuário qual ação foi desfeita
                JOptionPane.showMessageDialog(null, "Ação desfeita: " + acaoDesfeita);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }
    // Metodo para finalizar o preparo de um pedido
    private static void finalizarPreparo() {
        // Verifica se há pedidos em preparo
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        // Solicita o ID do pedido ao usuário
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            // Atualiza o status do pedido
            pedido.status = "Finalizado";

            // Prepara para exibir o histórico de ações
            StringBuilder historico = new StringBuilder();
            historico.append("Histórico de preparo do pedido #").append(pedido.id).append(":\n");

            // Cria uma pilha temporária para inverter a ordem de exibição
            Pilha tempPilha = new Pilha(100);
            Object acao;

            // Transfere todas as ações para a pilha temporária (inverte a ordem)
            while (!pedido.historicoAcoes.vazia()) {
                acao = pedido.historicoAcoes.desempilhar();
                tempPilha.empilhar(acao);
                historico.append("- ").append(acao).append("\n");
            }

            // Restaura as ações na pilha original
            while (!tempPilha.vazia()) {
                pedido.historicoAcoes.empilhar(tempPilha.desempilhar());
            }

            // Exibe o histórico completo para o usuário
            JOptionPane.showMessageDialog(null, historico.toString());

            // Remove o pedido da lista de pedidos ativos
            pedidosAtivos.excluiNo(pedido.id);
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }
    // Metodo para visualizar todos os pedidos em preparo
    private static void visualizarPedidosAtivos() {
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        StringBuilder lista = new StringBuilder("Pedidos em preparo:\n\n");
        IntNoSimples tempNo = pedidosAtivos.primeiro;

        while (tempNo != null) {
            Pedido pedido = tempNo.pedido;
            if (pedido != null) {
                lista.append("ID: ").append(pedido.id).append(" | ");
                lista.append("Cliente: ").append(pedido.nomeCliente).append(" | ");
                lista.append("Itens: ").append(pedido.itens).append(" | ");
                lista.append("Status: ").append(pedido.status).append(" | ");
                lista.append(pedido.isVip ? "(VIP)" : "(NORMAL)").append("\n"); // Adiciona informação VIP



            }
            tempNo = tempNo.prox;
        }

        JOptionPane.showMessageDialog(null, lista.toString());
    }
    // Metodo para consultar o histórico de um pedido específico
    private static void consultarHistorico() {
        // Verifica se há pedidos em preparo
        if (pedidosAtivos.ContarNos() == 0) {
            JOptionPane.showMessageDialog(null, "Não há pedidos em preparo!");
            return;
        }

        // Solicita o ID do pedido ao usuário
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido:"));
        Pedido pedido = buscarPedidoAtivoPorId(idPedido);

        if (pedido != null) {
            // Prepara a string com o histórico
            StringBuilder historico = new StringBuilder();
            historico.append("Histórico do pedido #").append(pedido.id).append(":\n");

            // Cria pilha temporária para inverter a ordem de exibição
            Pilha tempPilha = new Pilha(100);
            Object acao;

            // Transfere ações para a pilha temporária enquanto monta o histórico
            while (!pedido.historicoAcoes.vazia()) {
                acao = pedido.historicoAcoes.desempilhar();
                tempPilha.empilhar(acao);
                historico.append("- ").append(acao).append("\n");
            }

            // Restaura a pilha original
            while (!tempPilha.vazia()) {
                pedido.historicoAcoes.empilhar(tempPilha.desempilhar());
            }

            // Exibe o histórico completo
            JOptionPane.showMessageDialog(null, historico.toString());
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está em preparo!");
        }
    }
    // Metodo para reencaminhar um pedido finalizado de volta para a fila de espera
    private static void reencaminharPedido() {
        // Solicita o ID do pedido ao usuário
        int idPedido = Integer.parseInt(JOptionPane.showInputDialog("ID do pedido a ser reencaminhado:"));

        // Busca o pedido (simplificado - em sistema real buscaria em lista de finalizados)
        Pedido pedido = buscarPedidoPorId(idPedido);

        if (pedido != null) {
            // Reseta o status e o histórico do pedido
            pedido.status = "Pendente";
            pedido.historicoAcoes = new Pilha(100); // Nova pilha vazia

            // Adiciona o pedido de volta à fila de espera
            filaEspera.enfileirar(pedido.id);

            // Confirma a operação
            JOptionPane.showMessageDialog(null, "Pedido #" + pedido.id + " reencaminhado para a fila de espera!");
        } else {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado!");
        }
    }
    // Metodo auxiliar para buscar um pedido por ID (simplificado)
    private static Pedido buscarPedidoPorId(int id) {
        return todosPedidos.get(id); // Retorna o pedido do mapa ou null se não existir
    }
    // Metodo auxiliar para buscar um pedido ativo (em preparo) por ID
    private static Pedido buscarPedidoAtivoPorId(int id) {
        IntNoSimples tempNo = pedidosAtivos.primeiro;  // Começa pelo primeiro nó

        // Percorre a lista encadeada
        while (tempNo != null) {
            if (tempNo.valor == id) {
                return tempNo.pedido;  // Retorna o pedido encontrado
            }
            tempNo = tempNo.prox;      // Avança para o próximo nó
        }

        return null;  // Retorna null se não encontrou
    }
}

// Classe modificada para nó da lista encadeada
class IntNoSimples {
    int valor;           // Valor armazenado no nó (ID do pedido)
    IntNoSimples prox;   // Referência para o próximo nó na lista
    Pedido pedido;       // Referência para o objeto Pedido associado a este nó
}