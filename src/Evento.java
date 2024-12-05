import java.sql.*;
import java.util.Scanner;

public class Evento {

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
                System.out.printf("ID: %d | Descrição: %s | Data: %s | Vagas: %d%n", rs.getInt("id"),
                        rs.getString("descricao"), rs.getTimestamp("data"), rs.getInt("vagas"));
            }
        }
    }

    // 8. Inserir Evento
    public static void inserirEvento(Connection connection, Scanner scanner) throws Exception {

        boolean organizadoresDisponiveis = Utils.listarOrganizadoresDisponiveis(connection);

        if (!organizadoresDisponiveis) {
            System.out.println("\n* Não há organizadores disponíveis para criar um evento.");
            return;
        }

        System.out.print("\n* Digite o ID do organizador: ");
        int idOrganizador = scanner.nextInt();

        Local.listarLocais(connection);

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

    public static void atualizarEvento(Connection connection, Scanner scanner) throws Exception {
        listarEventos(connection);

        System.out.print("\n* Digite o ID do evento a ser atualizado: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer

        // Verifica se o evento existe e pega a descrição atual
        String descricaoAtual = null;
        String dataAtual = null;
        Integer vagasAtual = 0;
        String verificarEvento = "SELECT * FROM Evento WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarEvento)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    descricaoAtual = rs.getString("descricao");
                    dataAtual = rs.getString("data");
                    vagasAtual = rs.getInt("vagas");
                } else {
                    System.out.println("\n* Evento com o ID informado não encontrado.");
                    return;
                }
            }
        }

        // Menu para atualizar descrição ou vagas
        System.out.println("\n* O que você deseja atualizar?");
        System.out.println("1. Descrição");
        System.out.println("2. Data");
        System.out.println("3. Vagas");
        System.out.print("Escolha: ");
        int escolha = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer

        switch (escolha) {
            case 1:
                // Atualizar a descrição
                System.out.println("\n* Descrição atual: " + descricaoAtual);
                System.out.print("\n* Digite a nova descrição: ");
                String novaDescricao = scanner.nextLine();
                String updateDescricao = "UPDATE Evento SET descricao = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateDescricao)) {
                    stmt.setString(1, novaDescricao);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    System.out.println("\n* Descrição atualizada com sucesso!");
                }
                break;

            case 2:
                // Atualizar a data
                System.out.println("\n* Data e Hora atual: " + dataAtual);
                System.out.print("\n* Digite a nova data e hora (YYYY-MM-DD HH:MM:SS): ");
                String novaData = scanner.nextLine();
                String updateData = "UPDATE Evento SET data = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateData)) {
                    stmt.setString(1, novaData);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    System.out.println("\n* Data atualizada com sucesso!");
                }
                break;

            case 3:
                // Atualizar as vagas
                System.out.println("\n* Vagas atuais: " + vagasAtual);
                System.out.print("\n* Digite o novo número de vagas: ");
                int novasVagas = scanner.nextInt();
                String updateVagas = "UPDATE Evento SET vagas = ? WHERE id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateVagas)) {
                    stmt.setInt(1, novasVagas);
                    stmt.setInt(2, id);
                    stmt.executeUpdate();
                    System.out.println("\n* Vagas atualizadas com sucesso!");
                }
                break;
            default:
                System.out.println("\n* Opção inválida!");
                break;
        }
    }

    public static void deletarEvento(Connection connection, Scanner scanner) throws Exception {

        listarEventos(connection);

        System.out.print("\n* Digite o ID do evento a ser deletado: ");
        int id = scanner.nextInt();

        // Verifica se o evento existe
        String verificarEvento = "SELECT COUNT(*) FROM Evento WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarEvento)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("\n* Evento com o ID informado não encontrado.");
                    return; // Retorna sem realizar nenhuma operação
                }
            }
        }

        // Verifica se o evento tem participantes
        String verificarParticipantes = "SELECT COUNT(*) FROM EventoParticipante WHERE idEvento = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarParticipantes)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("\n* O evento tem participantes. Não é possível deletar.");
                    return; // Retorna sem realizar nenhuma operação
                }
            }
        }

        // Deleta o evento
        String deleteEvento = "DELETE FROM Evento WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteEvento)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("\n* Evento deletado com sucesso.");
        }

    }
}
