package client.system;

import client.UDPClient;
import common.managers.*;
import java.io.IOException;
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
      ScannerManager scannerManager = new ScannerManager(new Scanner(System.in));
      ScriptManager scriptManager = new ScriptManager(scannerManager);
      CommandManager commandManager = new CommandManager(null, scriptManager, scannerManager, null);
      UDPClient udpClient =
          new UDPClient(host, port, commandManager, scriptManager, "230.0.0.1", 4446);
      udpClient.runClient();
    } catch (IOException e) {
      System.err.println("Ошибка при создании клиента.");
    } catch (NumberFormatException e) {
      System.err.println("Порт должен быть целым числом.");
    }
  }
}
