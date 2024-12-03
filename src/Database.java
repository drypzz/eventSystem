import java.sql.Connection; // Importa a classe Connection para realizar a conexão com o banco de dados
import java.sql.DriverManager; // Importa a classe DriverManager para gerenciar os drivers de conexão com o banco de dados

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/eventojava";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    } // getConnection
    
};