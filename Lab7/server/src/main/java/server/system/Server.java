package server.system;

import common.managers.CollectionManager;
import common.managers.CommandManager;
import common.managers.UserManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import server.UDPServer;
import server.managers.SqlCollectionManager;
import server.managers.SqlUserManager;

public class Server {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Неверное количество аргументов для запуска сервера.");
      System.err.println("Используйте: java -jar server.jar <port> ");
      return;
    }

    String dbHost = System.getenv("DB_HOST");
    String dbName = System.getenv("DB_NAME");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");
    if (dbHost == null || dbName == null || dbUser == null || dbPassword == null) {
      System.err.println(
          "Необходимо задать переменные окружения DB_HOST, DB_NAME, DB_USER, DB_PASSWORD.");
      return;
    }

    try {
      int port = Integer.parseInt(args[0]);
      Connection connection =
          DriverManager.getConnection(
              "jdbc:postgresql://" + dbHost + "/" + dbName, dbUser, dbPassword);
      UserManager userManager = new SqlUserManager(connection);
      CollectionManager collectionManager = new SqlCollectionManager(connection);
      CommandManager commandManager =
          new CommandManager(collectionManager, null, null, userManager);
      UDPServer udpServer = new UDPServer(commandManager, collectionManager);
      udpServer.runServer(port);
    } catch (NumberFormatException e) {
      System.err.println("Порт должен быть целым числом.");
    } catch (IOException e) {
      System.err.println("Ошибка при запуске сервера: " + e.getMessage());
    } catch (SQLException e) {
      System.err.println("Не удалось подключиться к базе данных: " + e.getMessage());
    }
  }
}
