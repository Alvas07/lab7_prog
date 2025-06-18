package server.system;

import common.exceptions.FileReadException;
import common.managers.CollectionManager;
import common.managers.CommandManager;
import common.managers.FileManager;
import common.managers.IdManager;
import java.io.IOException;
import server.UDPServer;

public class Server {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Неверное количество аргументов для запуска сервера.");
      System.err.println("Используйте: java -jar server.jar <fileName> <port>");
      return;
    }

    try {
      String fileName = args[0];
      int port = Integer.parseInt(args[1]);
      FileManager fileManager = new FileManager(fileName);
      IdManager idManager = new IdManager();
      CollectionManager collectionManager = new ServerCollectionManager(fileManager, idManager);
      fileManager.fillCollectionFromXml(collectionManager);
      CommandManager commandManager = new CommandManager(collectionManager, null, null);
      UDPServer udpServer = new UDPServer(commandManager, collectionManager, fileManager);
      udpServer.runServer(port);
    } catch (NumberFormatException e) {
      System.err.println("Порт должен быть целым числом.");
    } catch (FileReadException e) {
      System.err.println(e.getMessage());
    } catch (IOException e) {
      System.err.println("Ошибка при запуске сервера.");
    }
  }
}
