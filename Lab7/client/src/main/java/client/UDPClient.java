package client;

import common.data.auth.AuthCredentials;
import common.exceptions.CommandExecuteException;
import common.exceptions.UnknownCommandException;
import common.managers.*;
import common.network.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPClient implements ClientControl {
  private final int BUFFER_SIZE = 65535;
  private final int TIMEOUT_MS = 10000;
  private final InetSocketAddress serverAddress;
  private final InetAddress multicastGroup;
  private final int multicastPort;
  private DatagramSocket socket;
  private MulticastSocket multicastSocket;
  private AtomicBoolean isRunning = new AtomicBoolean(true);
  private final CommandManager commandManager;
  private final ScriptManager scriptManager;
  private AuthCredentials auth = null;
  private Thread receiverThread;
  private Thread multicastThread;
  private final ConcurrentMap<UUID, CompletableFuture<Response>> pendingRequests =
      new ConcurrentHashMap<>();

  public UDPClient(
      String host,
      int port,
      CommandManager commandManager,
      ScriptManager scriptManager,
      String multicastAddress,
      int multicastPort)
      throws IOException {
    this.serverAddress = new InetSocketAddress(host, port);
    this.commandManager = commandManager;
    this.scriptManager = scriptManager;
    this.multicastGroup = InetAddress.getByName(multicastAddress);
    this.multicastPort = multicastPort;
    this.multicastSocket = new MulticastSocket(multicastPort);
    this.multicastSocket.joinGroup(multicastGroup);
    this.socket = new DatagramSocket();
    this.socket.setSoTimeout(TIMEOUT_MS);
  }

  public void runClient() {
    System.out.println("[CLIENT] Установлено подключение к серверу: " + serverAddress);
    System.out.println(
        "[CLIENT] Зарегистрируйтесь с помощью команды 'register' или войдите с помощью команды 'login'.");

    startReceiverThread(socket);
    startMulticastThread();

    spinLoop(socket);
  }

  private void startReceiverThread(DatagramSocket socket) {
    receiverThread =
        new Thread(
            () -> {
              ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
              DatagramPacket receivePacket =
                  new DatagramPacket(receiveBuffer.array(), receiveBuffer.array().length);

              while (isRunning.get()) {
                try {
                  socket.receive(receivePacket);
                  if (receivePacket.getData() != null && receivePacket.getLength() > 0) {
                    processResponsePacket(receivePacket);
                  }
                } catch (SocketTimeoutException ignored) {
                } catch (IOException e) {
                  if (isRunning.get()) {
                    System.err.println(
                        "[CLIENT] Ошибка при приеме ответа с сервера: " + e.getMessage());
                  }
                }
              }
            });

    receiverThread.setDaemon(true);
    receiverThread.start();
  }

  private void processResponsePacket(DatagramPacket packet) {
    try {
      Response response =
          (Response)
              ObjectDecoder.decodeObject(
                  ByteBuffer.wrap(Arrays.copyOf(packet.getData(), packet.getLength())));
      if (response.getRequestId() != null) {
        CompletableFuture<Response> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
          future.complete(response);
        }
      }
    } catch (ClassNotFoundException | IOException e) {
      System.err.println("[CLIENT] Ошибка декодирования ответа: " + e.getMessage());
    }
  }

  private void startMulticastThread() {
    multicastThread =
        new Thread(
            () -> {
              byte[] buffer = new byte[BUFFER_SIZE];
              DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
              String localId = socket.getLocalSocketAddress().toString();

              while (isRunning.get()) {
                try {
                  multicastSocket.receive(packet);
                  if (packet.getData() != null && packet.getLength() > 0) {
                    Response response =
                        (Response)
                            ObjectDecoder.decodeObject(
                                ByteBuffer.wrap(
                                    Arrays.copyOf(packet.getData(), packet.getLength())));
                    System.out.println("\n[BROADCAST] " + response.getMessage());
                    showPrompt();
                  }
                } catch (IOException | ClassNotFoundException e) {
                  if (isRunning.get()) {
                    System.err.println("[CLIENT] Multicast ошибка: " + e.getMessage());
                  }
                }
              }
            });

    multicastThread.setDaemon(true);
    multicastThread.start();
  }

  private void processResponse(Response response) {
    System.out.println("[CLIENT] Ответ: " + response.getMessage());

    if (response instanceof ResponseWithException) {
      System.out.println(((ResponseWithException) response).getException().getMessage());
    }

    if (response instanceof ResponseWithAuthCredentials) {
      auth = ((ResponseWithAuthCredentials) response).getAuth();
    }

    if (response.getTickets() != null && !response.getTickets().isEmpty()) {
      response.getTickets().forEach(System.out::println);
    }
  }

  private void showPrompt() {
    String prompt = auth != null ? auth.username() + "> " : "> ";
    System.out.print(prompt);
  }

  private void sendMessageRequest(Request request, DatagramSocket socket) throws IOException {
    try {
      ByteBuffer sendBuffer = ObjectEncoder.encodeObject(request);
      DatagramPacket sendPacket =
          new DatagramPacket(sendBuffer.array(), sendBuffer.array().length, serverAddress);
      socket.send(sendPacket);
      if (request.getCommandName().equals("send_message")) {
        System.out.println("[CLIENT] Сообщение отправлено всем активным пользователям.");
      }
    } catch (Exception e) {
      System.err.println("[CLIENT] Ошибка при передаче команды: " + e.getMessage());
    }
  }

  private Response sendRequestWithTimeout(Request request, DatagramSocket socket)
      throws TimeoutException, IOException {
    CompletableFuture<Response> future = new CompletableFuture<>();
    pendingRequests.put(request.getRequestId(), future);

    sendMessageRequest(request, socket);

    try {
      return future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
    } catch (TimeoutException e) {
      pendingRequests.remove(request.getRequestId());
      throw new TimeoutException();
    } catch (InterruptedException | ExecutionException e) {
      throw new IOException();
    }
  }

  private void spinLoop(DatagramSocket socket) {
    Scanner scanner = new Scanner(System.in);

    try {
      while (isRunning.get()) {
        showPrompt();
        String commandLine = scanner.nextLine().trim();
        if (commandLine.isEmpty()) {
          continue;
        }
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
            request = commandManager.convertInputToCommandRequest(commandLine, auth);
          }

          if (request != null) {
            if (request.getCommandName().equals("send_message")) {
              sendMessageRequest(request, socket);
            } else {
              Response response = sendRequestWithTimeout(request, socket);
              processResponse(response);
            }
          }
        } catch (IOException e) {
          stopClient();
        } catch (TimeoutException e) {
          System.err.println("[CLIENT] Превышено время ожидания ответа от сервера.");
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
    File file = new File(fileName);
    ScannerManager scannerManager = scriptManager.getScannerManager();
    boolean recursionFlag = false;
    if (!file.canRead()) {
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
            request = commandManager.convertInputToCommandRequest(input, auth);

            if (request != null) {
              if (request.getCommandName().equals("send_message")) {
                sendMessageRequest(request, socket);
              } else {
                Response response = sendRequestWithTimeout(request, socket);
                processResponse(response);
              }
            }
          } catch (TimeoutException e) {
            System.err.println("[CLIENT] Превышено время ожидания ответа от сервера.");
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
    isRunning.set(false);
  }
}
