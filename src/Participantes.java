import java.sql.*;
import java.util.Scanner;

public class Participantes {

    // Listar Participantes
    public static void listarParticipantesDeEvento(Connection connection, Scanner scanner) throws Exception {

        Evento.listarEventos(connection);

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

    // Adicionar Participante ao Evento
    public static void adicionarParticipanteAoEvento(Connection connection, Scanner scanner) throws Exception {

        Evento.listarEventos(connection);

        System.out.print("\n* Digite o ID do evento: ");
        int idEvento = scanner.nextInt();
    
        // Listar participantes disponíveis
        boolean participantesDisponiveis = Utils.listarParticipantesDisponiveis(connection);
    
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

    // Remover Participante de Evento
    public static void removerParticipanteDeEvento(Connection connection, Scanner scanner) throws Exception{
            
        Evento.listarEventos(connection);

        System.out.print("\n* Digite o ID do evento: ");
        int idEvento = scanner.nextInt();

        String sql = "SELECT P.id, P.nome FROM EventoParticipante EP JOIN Pessoa P ON EP.idParticipante = P.id WHERE EP.idEvento = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("\n* Nenhum participante cadastrado para este evento.");
                    return;
                }
                System.out.println("\n===== Participantes =====");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + " | Nome: " + rs.getString("nome"));
                }
            }
        }

        System.out.print("\n* Digite o ID do participante: ");
        int idParticipante = scanner.nextInt();

        // Verifica se o participante está no evento
        String verificarParticipante = "SELECT COUNT(*) FROM EventoParticipante WHERE idEvento = ? AND idParticipante = ?";
        try (PreparedStatement stmt = connection.prepareStatement(verificarParticipante)) {
            stmt.setInt(1, idEvento);
            stmt.setInt(2, idParticipante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("\n* O participante não está cadastrado neste evento.");
                    return; // Retorna sem realizar nenhuma operação
                }
            }
        }

        connection.setAutoCommit(false); // Inicia a transação

        String deletarParticipanteDoEvento = "DELETE FROM EventoParticipante WHERE idEvento = ? AND idParticipante = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deletarParticipanteDoEvento)) {
            stmt.setInt(1, idEvento);
            stmt.setInt(2, idParticipante);
            stmt.executeUpdate();
            // Atualiza o número de vagas no evento
            String atualizarVagasSql = "UPDATE Evento SET vagas = vagas + 1 WHERE id = ?";
            try (PreparedStatement stmt2 = connection.prepareStatement(atualizarVagasSql)) {
                stmt2.setInt(1, idEvento);
                stmt2.executeUpdate();
            }
            connection.commit(); // Confirma a transação
            System.out.println("\n* Participante removido do evento com sucesso!");
        }
    }
}
