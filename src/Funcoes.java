import java.sql.*;
import java.util.Scanner;

public class Funcoes {
    // 1. Listar Pessoas
    public static void listarPessoas(Connection connection) throws Exception {
        // Consulta para listar as pessoas e identificar se são Organizadores ou Participantes
        String sql = "SELECT p.id, p.nome, CASE WHEN o.id IS NOT NULL THEN 'Organizador' WHEN pa.id IS NOT NULL THEN 'Participante' ELSE 'Nenhum' END AS tipo FROM Pessoa p LEFT JOIN Organizador o ON p.id = o.id LEFT JOIN Participante pa ON p.id = pa.id;";
    
        try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("\n* Nenhuma pessoa cadastrada.");
                return;
            }
            System.out.println("\n==== Lista de Pessoas ====");
            while (rs.next()) {    
                System.out.printf("ID: %d | Nome: %s - %s%n", rs.getInt("id"), rs.getString("nome"), rs.getString("tipo"));
            }
        }
    }
    

    // 2. Inserir Pessoa
    public static void inserirPessoa(Connection connection, Scanner scanner) throws Exception {
        System.out.print("\n* Digite o nome da pessoa: ");
        String nome = scanner.nextLine();
    
        // Inserir pessoa na tabela Pessoa
        String sqlPessoa = "INSERT INTO Pessoa (nome) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();
    
            // Obter o ID gerado automaticamente
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idPessoa = rs.getInt(1);
    
                    // Determinar o tipo (Organizador ou Participante)
                    System.out.println("\n* A pessoa será:");
                    System.out.println("1. Organizador");
                    System.out.println("2. Participante");
                    System.out.print("Escolha uma opção: ");
                    int tipo = scanner.nextInt();
                    scanner.nextLine(); // Limpar buffer
    
                    if (tipo == 1) {
                        // Inserir na tabela Organizador
                        System.out.print("\n* Digite o email do organizador: ");
                        String email = scanner.nextLine();
                        String sqlOrganizador = "INSERT INTO Organizador (id, email) VALUES (?, ?)";
                        try (PreparedStatement stmtOrg = connection.prepareStatement(sqlOrganizador)) {
                            stmtOrg.setInt(1, idPessoa);
                            stmtOrg.setString(2, email);
                            stmtOrg.executeUpdate();
                        }
                        System.out.println("\n* Organizador cadastrado com sucesso!");
                    } else if (tipo == 2) {
                        // Inserir na tabela Participante
                        System.out.print("\n* Digite o telefone do participante: ");
                        String telefone = scanner.nextLine();
                        String sqlParticipante = "INSERT INTO Participante (id, telefone) VALUES (?, ?)";
                        try (PreparedStatement stmtPart = connection.prepareStatement(sqlParticipante)) {
                            stmtPart.setInt(1, idPessoa);
                            stmtPart.setString(2, telefone);
                            stmtPart.executeUpdate();
                        }
                        System.out.println("\n* Participante cadastrado com sucesso!");
                    } else {
                        System.out.println("\n* Opção inválida. Pessoa cadastrada, mas sem função específica.");
                    }
                }
            }
        }
    }

    // 3. Atualizar Pessoa
    public static void atualizarPessoa(Connection connection, Scanner scanner) throws Exception {

        listarPessoas(connection);
    
        System.out.print("\n* Digite o ID da pessoa a ser atualizada: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer
    
        // Verifica se a pessoa existe e pega o nome atual
        String nomeAtual = null;
        String verificaPessoaSql = "SELECT nome FROM Pessoa WHERE id = ?";
        try (PreparedStatement verificaStmt = connection.prepareStatement(verificaPessoaSql)) {
            verificaStmt.setInt(1, id);
            try (ResultSet rs = verificaStmt.executeQuery()) {
                if (rs.next()) {
                    nomeAtual = rs.getString("nome");
                } else {
                    System.out.println("\n* Pessoa com o ID informado não encontrada.");
                    return;
                }
            }
        }
    
        // Verifica se a pessoa é Organizador ou Participante
        String verificaSql = "SELECT 'Organizador' AS tipo FROM Organizador WHERE id = ? " + "UNION " + "SELECT 'Participante' AS tipo FROM Participante WHERE id = ?";
        String tipoPessoa = null;
    
        try (PreparedStatement verificaStmt = connection.prepareStatement(verificaSql)) {
            verificaStmt.setInt(1, id);
            verificaStmt.setInt(2, id);
    
            try (ResultSet rs = verificaStmt.executeQuery()) {
                if (rs.next()) {
                    tipoPessoa = rs.getString("tipo");
                } else {
                    System.out.println("\n* Pessoa com o ID informado não encontrada.");
                    return;
                }
            }
        }
    
        // Menu para atualizar nome ou tipo
        System.out.println("\n* O que você deseja atualizar?");
        System.out.println("1. Nome");
        System.out.println("2. Alterar tipo (Organizador/Participante)");
        System.out.print("Escolha: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer
    
        switch (escolha) {
            case 1:
                // Atualizar o nome
                System.out.println("\n* Nome atual: " + nomeAtual);
                System.out.print("\n* Digite o novo nome: ");
                String novoNome = scanner.nextLine();
                String updateNomeSql = "UPDATE Pessoa SET nome = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateNomeSql)) {
                    stmt.setString(1, novoNome);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    System.out.println("\n* Nome atualizado com sucesso!");
                }
                break;
    
            case 2:
                // Alterar tipo (Organizador ou Participante)
                if (tipoPessoa.equals("Organizador")) {
                    System.out.println("\n* A pessoa é um Organizador. Deseja mudar para Participante?");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    System.out.print("Escolha: ");
                    int confirmacao = scanner.nextInt();
                    scanner.nextLine(); // Limpa o buffer após nextInt()
    
                    if (confirmacao == 1) {
                        System.out.print("\n* Digite o telefone do participante: ");
                        String telefone = scanner.nextLine();
    
                        if (telefone.length() < 11 || telefone.isEmpty()) {
                            System.out.println("\n* Telefone inválido. Não foi possível alterar.");
                            return;
                        }
    
                        // Deleta da tabela Organizador e insere na tabela Participante
                        String deleteOrganizadorSql = "DELETE FROM Organizador WHERE id = ?";
                        String insertParticipanteSql = "INSERT INTO Participante (id, telefone) VALUES (?, ?)";
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteOrganizadorSql);
                        PreparedStatement insertStmt = connection.prepareStatement(insertParticipanteSql)) {
                            deleteStmt.setInt(1, id);
                            deleteStmt.executeUpdate();
    
                            insertStmt.setInt(1, id);
                            insertStmt.setString(2, telefone);
                            insertStmt.executeUpdate();
    
                            System.out.println("\n* Tipo alterado para Participante com sucesso!");
                        }
                    }
                } else if (tipoPessoa.equals("Participante")) {
                    System.out.println("\n* A pessoa é um Participante. Deseja mudar para Organizador?");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    System.out.print("Escolha: ");
                    int confirmacao = scanner.nextInt();
                    scanner.nextLine(); // Limpa o buffer após nextInt()
    
                    if (confirmacao == 1) {
                        System.out.print("\n* Digite o email do organizador: ");
                        String email = scanner.nextLine();
    
                        if (email.isEmpty()) {
                            System.out.println("\n* Email inválido. Não foi possível alterar.");
                            return;
                        }
    
                        // Deleta da tabela Participante e insere na tabela Organizador
                        String deleteParticipanteSql = "DELETE FROM Participante WHERE id = ?";
                        String insertOrganizadorSql = "INSERT INTO Organizador (id, email) VALUES (?, ?)";
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteParticipanteSql);
                        PreparedStatement insertStmt = connection.prepareStatement(insertOrganizadorSql)) {
                            deleteStmt.setInt(1, id);
                            deleteStmt.executeUpdate();
    
                            insertStmt.setInt(1, id);
                            insertStmt.setString(2, email);
                            insertStmt.executeUpdate();
    
                            System.out.println("\n* Tipo alterado para Organizador com sucesso!");
                        }
                    }
                } else {
                    System.out.println("\n* Tipo desconhecido. Não foi possível alterar.");
                }
                break;
    
            default:
                System.out.println("\n* Opção inválida!");
        }
    }
    


    // 4. Deletar Pessoa
    public static void deletarPessoa(Connection connection, Scanner scanner) throws Exception {

        listarPessoas(connection);

        System.out.print("\n* Digite o ID da pessoa a ser deletada: ");
        int id = scanner.nextInt();

        // Verifica se a pessoa existe
        String verificarPessoa = "SELECT COUNT(*) FROM Pessoa WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarPessoa)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("\n* Pessoa com o ID informado não encontrada.");
                    return; // Retorna sem realizar nenhuma operação
                }
            }
        }

    
        // Inicia uma transação
        connection.setAutoCommit(false);
    
        try {

            // Verifica se a pessoa é um participante registrado em eventos
            String buscarEventosParticipante = "SELECT idEvento FROM EventoParticipante WHERE idParticipante = ?";
            try (PreparedStatement stmt = connection.prepareStatement(buscarEventosParticipante)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int idEvento = rs.getInt("idEvento");

                        // Restaura uma vaga no evento
                        String atualizarVagasSql = "UPDATE Evento SET vagas = vagas + 1 WHERE id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(atualizarVagasSql)) {
                            updateStmt.setInt(1, idEvento);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }

            // Verifica se a pessoa está registrada em um evento como organizador ou participante
            String deleteOrganizadorEvento = "DELETE FROM Evento WHERE idOrganizador = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteOrganizadorEvento)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            String deleteParticipanteEvento = "DELETE FROM EventoParticipante WHERE idParticipante = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteParticipanteEvento)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Verifica se a pessoa é um Organizador ou Participante e deleta da tabela correspondente
            String deleteOrganizador = "DELETE FROM Organizador WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteOrganizador)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
    
            String deleteParticipante = "DELETE FROM Participante WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteParticipante)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
    
            String deletePessoa = "DELETE FROM Pessoa WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deletePessoa)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
    
            // Confirma a transação
            connection.commit();
            System.out.println("\n* Pessoa deletada com sucesso.");
    
        } catch (Exception e) {
            // Reverte a transação em caso de erro
            connection.rollback();
            System.err.println("\n* Erro ao deletar a pessoa: " + e.getMessage());
        } finally {
            // Restaura o modo de auto-commit
            connection.setAutoCommit(true);
        }
    }
    

    // 5. Listar Locais
    public static void listarLocais(Connection connection) throws Exception {
        String sql = "SELECT * FROM Local";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            if (!rs.isBeforeFirst()) {
                System.out.println("\n* Nenhum local cadastrado.");
                return;
            }
    
            System.out.println("\n===== Lista de Locais =====");
            while (rs.next()) {
                System.out.printf("ID: %d | Descrição: %s | Vagas: %d%n", rs.getInt("id"), rs.getString("descricao"), rs.getInt("vagas"));
            }
        }
    }

    // 6. Inserir Local
    public static void inserirLocal(Connection connection, Scanner scanner) throws Exception {
        System.out.print("\n* Digite a descrição do local: ");
        String descricao = scanner.nextLine();
        System.out.print("\n* Digite o número de vagas: ");
        int vagas = scanner.nextInt();

        String sql = "INSERT INTO Local (descricao, vagas) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, descricao);
            stmt.setInt(2, vagas);
            stmt.executeUpdate();
            System.out.println("\n* Local inserido com sucesso!");
        }
    }

    // 7. Listar Eventos
    public static void listarEventos(Connection connection) throws Exception {
        String sql = "SELECT * FROM Evento";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("\n* Nenhum evento cadastrado.");
                return;
            }
            System.out.println("\n===== Lista de Eventos =====");
            while (rs.next()) {
                System.out.printf("ID: %d | Descrição: %s | Data: %s | Vagas: %d%n", rs.getInt("id"), rs.getString("descricao"), rs.getTimestamp("data"), rs.getInt("vagas"));
            }
        }
    }

    // 8. Inserir Evento
    public static void inserirEvento(Connection connection, Scanner scanner) throws Exception {

        boolean organizadoresDisponiveis = listarOrganizadoresDisponiveis(connection);

        if (!organizadoresDisponiveis) {
            System.out.println("\n* Não há organizadores disponíveis para criar um evento.");
            return;
        }

        System.out.print("\n* Digite o ID do organizador: ");
        int idOrganizador = scanner.nextInt();

        listarLocais(connection);

        System.out.print("\n* Digite o ID do local: ");
        int idLocal = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer
        System.out.print("\n* Digite a descrição do evento: ");
        String descricao = scanner.nextLine();
        System.out.print("\n* Digite a data e hora (YYYY-MM-DD HH:MM:SS): ");
        String data = scanner.nextLine();
        System.out.print("\n* Digite o número de vagas: ");
        int vagas = scanner.nextInt();

        String sql = "INSERT INTO Evento (idOrganizador, idLocal, data, descricao, vagas) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idOrganizador);
            stmt.setInt(2, idLocal);
            stmt.setString(3, data);
            stmt.setString(4, descricao);
            stmt.setInt(5, vagas);
            stmt.executeUpdate();
            System.out.println("\n* Evento inserido com sucesso!");
        }
    }

    // 9. Listar Participantes de um Evento
    public static void listarParticipantesDeEvento(Connection connection, Scanner scanner) throws Exception {

        listarEventos(connection);

        System.out.print("\n* Digite o ID do evento: ");
        int idEvento = scanner.nextInt();

        // Consulta para listar os participantes de um evento
        String sql = "SELECT P.nome FROM EventoParticipante EP JOIN Pessoa P ON EP.idParticipante = P.id WHERE EP.idEvento = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("\n* Nenhum participante cadastrado para este evento.");
                    return;
                }
                System.out.println("\n===== Participantes =====");
                while (rs.next()) {
                    System.out.println("Nome: " + rs.getString("nome"));
                }
            }
        }
    }

    // 10. Adicionar Participante ao Evento
    public static void adicionarParticipanteAoEvento(Connection connection, Scanner scanner) throws Exception {

        listarEventos(connection);

        System.out.print("\n* Digite o ID do evento: ");
        int idEvento = scanner.nextInt();
    
        // Listar participantes disponíveis
        boolean participantesDisponiveis = listarParticipantesDisponiveis(connection);
    
        // Se não houver participantes disponíveis, cancela a operação
        if (!participantesDisponiveis) {
            System.out.println("\n* Não há participantes disponíveis.");
            return;
        }
    
        System.out.print("\n* Digite o ID do participante: ");
        int idParticipante = scanner.nextInt();
    
        connection.setAutoCommit(false); // Inicia a transação
    
        try {
            // Verifica se há vagas no evento
            String verificarVagasSql = "SELECT vagas FROM Evento WHERE id = ?";
            int vagasDisponiveis = 0;
    
            try (PreparedStatement stmt = connection.prepareStatement(verificarVagasSql)) {
                stmt.setInt(1, idEvento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        vagasDisponiveis = rs.getInt("vagas");
                    } else {
                        System.out.println("\n* Evento não encontrado.");
                        return;
                    }
                }
            }
    
            if (vagasDisponiveis <= 0) {
                System.out.println("\n* Não há vagas disponíveis neste evento.");
                return;
            }
    
            // Adiciona o participante ao evento
            String inserirParticipanteSql = "INSERT INTO EventoParticipante (idEvento, idParticipante) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(inserirParticipanteSql)) {
                stmt.setInt(1, idEvento);
                stmt.setInt(2, idParticipante);
                stmt.executeUpdate();
            }
    
            // Atualiza o número de vagas no evento
            String atualizarVagasSql = "UPDATE Evento SET vagas = vagas - 1 WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(atualizarVagasSql)) {
                stmt.setInt(1, idEvento);
                stmt.executeUpdate();
            }
    
            connection.commit(); // Confirma a transação
            System.out.println("\n* Participante adicionado ao evento com sucesso!");
    
        } catch (Exception e) {
            connection.rollback(); // Reverte a transação em caso de erro
            System.err.println("\n* Erro ao adicionar participante ao evento: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true); // Restaura o modo de auto-commit
        }
    }
    
    // Função para listar participantes disponíveis e retornar se há participantes ou não
    public static boolean listarParticipantesDisponiveis(Connection connection) throws Exception {
        // Consulta para listar participantes disponíveis
        String sql = "SELECT p.id, p.nome FROM Pessoa p JOIN Participante t ON p.id = t.id WHERE p.id NOT IN ( SELECT idParticipante FROM EventoParticipante );";
    
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            if (!rs.isBeforeFirst()) {
                return false; // Não há participantes disponíveis
            }
    
            System.out.println("\n==== Lista de Participantes Disponíveis ====");
            while (rs.next()) {
                System.out.printf("ID: %d | Nome: %s%n", rs.getInt("id"), rs.getString("nome"));
            }
            return true; // Há participantes disponíveis
        }
    }
    
    public static boolean listarOrganizadoresDisponiveis(Connection connection) throws Exception {
        // Consulta para listar organizadores disponíveis
        String sql = "SELECT p.id, p.nome FROM Pessoa p JOIN Organizador t ON p.id = t.id WHERE p.id NOT IN ( SELECT idOrganizador FROM Evento );";
    
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            if (!rs.isBeforeFirst()) {
                return false; // Não há organizadores disponíveis
            }
    
            System.out.println("\n==== Lista de Organizadores Disponíveis ====");
            while (rs.next()) {
                System.out.printf("ID: %d | Nome: %s%n", rs.getInt("id"), rs.getString("nome"));
            }
            return true; // Há organizadores disponíveis
        }
    }
}