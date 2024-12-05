import java.sql.*;
import java.util.Scanner;

public class Pessoa {

    // Listar Pessoas
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

    // Inserir Pessoa
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

    // Atualizar Pessoa
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
    
    // Deletar Pessoa
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
}
