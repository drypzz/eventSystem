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
                    Funcoes.listarPessoas(connection);
                    break;
                case 2:
                    Funcoes.inserirPessoa(connection, scanner);
                    break;
                case 3:
                    Funcoes.atualizarPessoa(connection, scanner);
                    break;
                case 4:
                    Funcoes.deletarPessoa(connection, scanner);
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
            System.out.println("[3] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Funcoes.listarLocais(connection);
                    break;
                case 2:
                    Funcoes.inserirLocal(connection, scanner);
                    break;
                case 3:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 3);
    }

    // Menu de Eventos
    private static void menuEventos(Connection connection, Scanner scanner) throws Exception {
        int opcao;
        do {
            System.out.println("\n\n=====[ MENU EVENTOS ]=====");
            System.out.println("[1] - Listar Eventos");
            System.out.println("[2] - Inserir Evento");
            System.out.println("[3] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Funcoes.listarEventos(connection);
                    break;
                case 2:
                    Funcoes.inserirEvento(connection, scanner);
                    break;
                case 3:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 3);
    }

    // Menu de Participantes
    private static void menuParticipantes(Connection connection, Scanner scanner) throws Exception {
        int opcao;
        do {
            System.out.println("\n\n=====[ MENU PARTICIPANTES ]=====");
            System.out.println("[1] - Listar Participantes de um Evento");
            System.out.println("[2] - Adicionar Participante ao Evento");
            System.out.println("[3] - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer

            switch (opcao) {
                case 1:
                    Funcoes.listarParticipantesDeEvento(connection, scanner);
                    break;
                case 2:
                    Funcoes.adicionarParticipanteAoEvento(connection, scanner);
                    break;
                case 3:
                    System.out.println("\n* Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("\n* Opção inválida!");
                    break;
            }
        } while (opcao != 3);
    }
};