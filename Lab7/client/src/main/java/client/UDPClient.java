package client;

import common.exceptions.CommandExecuteException;
import common.exceptions.UnknownCommandException;
import common.managers.*;
import common.network.ObjectDecoder;
import common.network.ObjectEncoder;
import common.network.Request;
import common.network.Response;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UDPClient implements ClientControl {
  private final int BUFFER_SIZE = 65535;
  private final int TIMEOUT_MS = 10000;
  private final InetSocketAddress serverAddress;
  private boolean isRunning = true;
  private final CommandManager commandManager;
  private final ScriptManager scriptManager;

  public UDPClient(
      String host, int port, CommandManager commandManager, ScriptManager scriptManager)
      throws IOException {
    this.serverAddress = new InetSocketAddress(host, port);
    this.commandManager = commandManager;
    this.scriptManager = scriptManager;
  }

  public void runClient() {
    try (DatagramSocket socket = new DatagramSocket()) {
      socket.setSoTimeout(TIMEOUT_MS);
      System.out.println("[CLIENT] Установлено подключение к серверу: " + serverAddress);
      spinLoop(socket);
    } catch (IOException e) {
      System.err.println("[CLIENT] Ошибка при подключении к серверу.");
    }
  }

  private void sendRequest(Request request, DatagramSocket socket) throws IOException {
    try {
      ByteBuffer sendBuffer = ObjectEncoder.encodeObject(request);
      DatagramPacket sendPacket =
          new DatagramPacket(sendBuffer.array(), sendBuffer.array().length, serverAddress);
      socket.send(sendPacket);

      ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
      DatagramPacket receivePacket =
          new DatagramPacket(receiveBuffer.array(), receiveBuffer.array().length);
      try {
        socket.receive(receivePacket);
        if (receivePacket.getData() != null) {
          Response response =
              (Response) ObjectDecoder.decodeObject(ByteBuffer.wrap(receivePacket.getData()));

          System.out.println("[CLIENT] Ответ: " + response.getMessage());

          if (response.getTickets() != null && !response.getTickets().isEmpty()) {
            response.getTickets().forEach(System.out::println);
          }
        }
      } catch (SocketTimeoutException e) {
        System.err.println("[CLIENT] Превышено время ожидания от сервера.");
      } catch (IOException e) {
        System.err.println("[CLIENT] Ошибка при передаче команды: " + e.getMessage());
      }

    } catch (Exception e) {
      System.err.println("[CLIENT] Ошибка при передаче команды: " + e.getMessage());
    }
  }

  private void spinLoop(DatagramSocket socket) {
    Scanner scanner = new Scanner(System.in);

    try {
      while (isRunning) {
        System.out.print("> ");
        String commandLine = scanner.nextLine().trim();
        String[] parts = commandLine.split("\\s+");
        try {
          Request request = null;
          if (parts[0].equals("execute_script")) {
            if (parts.length != 2) {
              System.out.println("[CLIENT] Команда принимает один обязательный аргумент.");
            } else {
              executeScript(parts[1], socket);
            }
          } else if (parts[0].equals("exit")) {
            if (parts.length != 1) {
              System.out.println("[CLIENT] Команда не принимает аргументов.");
            } else {
              stopClient();
            }
          } else {
            request = commandManager.convertInputToCommandRequest(commandLine);
          }

          if (request != null) {
            sendRequest(request, socket);
          }
        } catch (IOException e) {
          stopClient();
        } catch (Exception e) {
          System.err.println("[CLIENT] Непредвиденная ошибка: " + e.getMessage());
        }
      }
    } catch (NoSuchElementException e) {
      System.out.println("[CLIENT] Завершение работы клиента...");
      stopClient();
    }
  }

  private void executeScript(String fileName, DatagramSocket socket) {
    FileManager fileManager = new FileManager(fileName);
    ScannerManager scannerManager = scriptManager.getScannerManager();
    boolean recursionFlag = false;
    if (!fileManager.canRead()) {
      System.err.println("[CLIENT] Невозможно прочитать информацию из файла скрипта.");
    }

    try {
      scriptManager.activateFileMode();
      scriptManager.addPath(fileName);
      Scanner currentScanner;

      while (!scriptManager.getAllScanners().isEmpty()) {
        currentScanner = scriptManager.getLastScanner();
        if (currentScanner.hasNextLine()) {
          scannerManager.setScanner(currentScanner);
        } else {
          scriptManager.removePath();
          scannerManager.setScanner(scriptManager.getLastScanner());
          currentScanner = scriptManager.getLastScanner();
        }

        String input = currentScanner.nextLine();
        String[] commandParts = input.trim().split("\\s+");

        if (commandParts[0].equalsIgnoreCase("execute_script")
            && scriptManager.isRecursive(commandParts[1])) {
          System.err.println(
              "[CLIENT] Обнаружена рекурсия! Отмена скрипта! Повторно вызывается файл "
                  + new File(commandParts[1]).getAbsolutePath());
          recursionFlag = true;
          continue;
        }

        System.out.println("[CLIENT] Выполнение команды " + commandParts[0] + ":");

        if (commandParts[0].equalsIgnoreCase("execute_script")) {
          executeScript(commandParts[1], socket);
        } else {
          try {
            Request request = null;
            request = commandManager.convertInputToCommandRequest(input);

            if (request != null) {
              sendRequest(request, socket);
            }
          } catch (UnknownCommandException | CommandExecuteException | IOException e) {
            System.out.println("[CLIENT] Непредвиденная ошибка: " + e.getMessage());
          } catch (NoSuchElementException e) {
            currentScanner = new Scanner(System.in);
            scannerManager.setScanner(currentScanner);
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("[CLIENT] Не удалось найти файл: " + e.getMessage());
    } catch (NoSuchElementException ignored) {
    } finally {
      scriptManager.deactivateFileMode();
      if (!recursionFlag) {
        System.out.println("[CLIENT] Скрипт " + fileName + " выполнен!");
      }
    }
  }

  @Override
  public void stopClient() {
    isRunning = false;
  }
}
