import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = Database.getConnection();
                Scanner scanner = new Scanner(System.in)) {

            int opcaoPrincipal;
            do {
                System.out.println("\n\n=====[ MENU PRINCIPAL ]=====");
                System.out.println("[1] - Gerenciar Pessoas");
                System.out.println("[2] - Gerenciar Locais");
                System.out.println("[3] - Gerenciar Eventos");
                System.out.println("[4] - Gerenciar Participantes");
                System.out.println("[5] - Sair");
                System.out.print("Escolha uma opção: ");
                opcaoPrincipal = scanner.nextInt();
                scanner.nextLine(); // Limpa o buffer

                switch (opcaoPrincipal) {
                    case 1:
                        menuPessoas(connection, scanner);
                        break;
                    case 2:
                        menuLocais(connection, scanner);
                        break;
                    case 3:
                        menuEventos(connection, scanner);
                        break;
                    case 4:
                        menuParticipantes(connection, scanner);
                        break;
                    case 5:
                        System.out.println("\n* Saindo...");
                        break;
                    default:
                        System.out.println("\n* Opção inválida!");
                        break;
                }
            } while (opcaoPrincipal != 5);

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    // Menu de Pessoas
    private static void menuPessoas(Connection connection, Scanner scanner) throws Exception {
        int opcao;
        do {
            System.out.println("\n\n=====[ MENU PESSOAS ]=====");
            System.out.println("[1] - Listar Pessoas");
            System.out.println("[2] - Inserir Pessoa");
            System.out.println("[3] - Atualizar Pessoa");
            System.out.println("[4] - Deletar Pessoa");
            System.out.println("[5] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Pessoa.listarPessoas(connection);
                    break;
                case 2:
                    Pessoa.inserirPessoa(connection, scanner);
                    break;
                case 3:
                    Pessoa.atualizarPessoa(connection, scanner);
                    break;
                case 4:
                    Pessoa.deletarPessoa(connection, scanner);
                    break;
                case 5:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 5);
    }

    // Menu de Locais
    private static void menuLocais(Connection connection, Scanner scanner) throws Exception {
        int opcao;
        do {
            System.out.println("\n\n=====[ MENU LOCAIS ]=====");
            System.out.println("[1] - Listar Locais");
            System.out.println("[2] - Inserir Local");
            System.out.println("[3] - Atualizar Local");
            System.out.println("[4] - Deletar Local");
            System.out.println("[5] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Local.listarLocais(connection);
                    break;
                case 2:
                    Local.inserirLocal(connection, scanner);
                    break;
                case 3:
                    Local.atualizarLocal(connection, scanner);
                    break;
                case 4:
                    Local.deletarLocal(connection, scanner);
                    break;
                case 5:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 5);
    }

    // Menu de Eventos
    private static void menuEventos(Connection connection, Scanner scanner) throws Exception {
        int opcao;
        do {
            System.out.println("\n\n=====[ MENU EVENTOS ]=====");
            System.out.println("[1] - Listar Eventos");
            System.out.println("[2] - Inserir Evento");
            System.out.println("[3] - Atualizar Evento");
            System.out.println("[4] - Deletar Evento");
            System.out.println("[5] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Evento.listarEventos(connection);
                    break;
                case 2:
                    Evento.inserirEvento(connection, scanner);
                    break;
                case 3:
                    Evento.atualizarEvento(connection, scanner);
                    break;
                case 4:
                    Evento.deletarEvento(connection, scanner);
                    break;
                case 5:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 5);
    }

    // Menu de Participantes
    private static void menuParticipantes(Connection connection, Scanner scanner) throws Exception {
        int opcao;
        do {
            System.out.println("\n\n=====[ MENU PARTICIPANTES ]=====");
            System.out.println("[1] - Listar Participantes de um Evento");
            System.out.println("[2] - Adicionar Participante ao Evento");
            System.out.println("[3] - Remover Participante de um Evento");
            System.out.println("[4] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Participantes.listarParticipantesDeEvento(connection, scanner);
                    break;
                case 2:
                    Participantes.adicionarParticipanteAoEvento(connection, scanner);
                    break;
                case 3:
                    Participantes.removerParticipanteDeEvento(connection, scanner);
                    break;
                case 4:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 4);
    }
};