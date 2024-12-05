import java.sql.*;
import java.util.Scanner;

public class Local {

    // Listar Locais
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
                System.out.printf("ID: %d | Descrição: %s | Vagas: %d%n", rs.getInt("id"), rs.getString("descricao"),
                        rs.getInt("vagas"));
            }
        }
    }

    // Inserir Local
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

    // Atualizar Local
    public static void atualizarLocal(Connection connection, Scanner scanner) throws Exception {

        listarLocais(connection);

        System.out.print("\n* Digite o ID do local a ser atualizado: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer

        // Verifica se o local existe e pega a descrição atual
        String descricaoAtual = null;
        Integer vagaAtual = 0;
        String verificarLocal = "SELECT * FROM Local WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarLocal)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    descricaoAtual = rs.getString("descricao");
                    vagaAtual = rs.getInt("vagas");
                } else {
                    System.out.println("\n* Local com o ID informado não encontrado.");
                    return;
                }
            }
        }

        // Menu para atualizar descrição ou vagas
        System.out.println("\n* O que você deseja atualizar?");
        System.out.println("1. Descrição");
        System.out.println("2. Vagas");
        System.out.print("Escolha: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer

        switch (escolha) {
            case 1:
                // Atualizar a descrição
                System.out.println("\n* Descrição atual: " + descricaoAtual);
                System.out.print("\n* Digite a nova descrição: ");
                String novaDescricao = scanner.nextLine();
                String updateDescricao = "UPDATE Local SET descricao = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateDescricao)) {
                    stmt.setString(1, novaDescricao);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    System.out.println("\n* Descrição atualizada com sucesso!");
                }
                break;

            case 2:
                // Atualizar as vagas
                System.out.println("\n* Vagas atuais: " + vagaAtual);
                System.out.print("\n* Digite o novo número de vagas: ");
                int novasVagas = scanner.nextInt();
                String updateVagas = "UPDATE Local SET vagas = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateVagas)) {
                    stmt.setInt(1, novasVagas);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    System.out.println("\n* Vagas atualizadas com sucesso!");
                }
                break;

            default:
                System.out.println("\n* Opção inválida!");
        }
    }

    // Deletar Local
    public static void deletarLocal(Connection connection, Scanner scanner) throws Exception {
        listarLocais(connection);

        System.out.print("\n* Digite o ID do local a ser deletado: ");
        int id = scanner.nextInt();

        // Verifica se o local existe
        String verificarLocal = "SELECT COUNT(*) FROM Local WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarLocal)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("\n* Local com o ID informado não encontrado.");
                    return; // Retorna sem realizar nenhuma operação
                }
            }
        }

        // Verifica se o local está associado a algum evento
        String verificarEvento = "SELECT COUNT(*) FROM Evento WHERE idLocal = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarEvento)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("\n* O local está associado a um evento. Não é possível deletar.");
                    return; // Retorna sem realizar nenhuma operação
                }
            }
        }

        // Deleta o local
        String deleteLocal = "DELETE FROM Local WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteLocal)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("\n* Local deletado com sucesso.");
        }
    }
}
