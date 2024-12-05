import java.sql.*;

public class Utils {
    
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
    
    // Função para listar organizadores disponíveis e retornar se há organizadores ou não
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