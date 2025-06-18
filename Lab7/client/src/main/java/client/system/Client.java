package client.system;

import client.UDPClient;
import common.managers.CollectionManager;
import common.managers.CommandManager;
import common.managers.ScannerManager;
import common.managers.ScriptManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Неверное количество аргументов для запуска клиента.");
      System.err.println("Используйте: java -jar client.jar <host> <port>");
      return;
    }

    try {
      String host = args[0];
      int port = Integer.parseInt(args[1]);
      InetSocketAddress serverAddress = new InetSocketAddress(host, port);
      ScannerManager scannerManager = new ScannerManager(new Scanner(System.in));
      ScriptManager scriptManager = new ScriptManager(scannerManager);
      CollectionManager collectionManager = new PlaceholderCollectionManager();
      CommandManager commandManager =
          new CommandManager(collectionManager, scriptManager, scannerManager);
      UDPClient udpClient = new UDPClient(host, port, commandManager, scriptManager);
      udpClient.runClient();
    } catch (IOException e) {
      System.err.println("Ошибка при создании клиента.");
    } catch (NumberFormatException e) {
      System.err.println("Порт должен быть целым числом.");
    }
  }
}
