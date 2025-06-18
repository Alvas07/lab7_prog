package server;

import common.exceptions.FileWriteException;
import common.managers.CollectionManager;
import common.managers.CommandManager;
import common.managers.FileManager;
import common.network.ObjectDecoder;
import common.network.ObjectEncoder;
import common.network.Request;
import common.network.Response;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPServer {
  private final int BUFFER_SIZE = 65535;
  private final int SELECTOR_TIMEOUT = 100;
  private final CommandManager commandManager;
  private final CollectionManager collectionManager;
  private final FileManager fileManager;
  private final ExecutorService requestPool = Executors.newCachedThreadPool();
  private static final Logger logger = LogManager.getLogger();
  private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
  private boolean isRunning = true;

  public UDPServer(
      CommandManager commandManager, CollectionManager collectionManager, FileManager fileManager) {
    this.commandManager = commandManager;
    this.collectionManager = collectionManager;
    this.fileManager = fileManager;
  }

  public void runServer(int port) throws IOException {
    try (Selector selector = Selector.open();
        DatagramChannel channel = DatagramChannel.open()) {
      channel.configureBlocking(false);
      channel.bind(new InetSocketAddress(port));
      channel.register(selector, SelectionKey.OP_READ);

      logger.info("Сервер запущен на порту " + port);

      ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);

      while (isRunning) {
        if (isConsoleInput()) {
          shutdown(selector, channel);
          return;
        }

        if (selector.select(SELECTOR_TIMEOUT) == 0) continue;

        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
          SelectionKey key = keys.next();
          keys.remove();

          try {
            if (key.isReadable()) {
              receiveBuffer.clear();
              DatagramChannel clientChannel = (DatagramChannel) key.channel();
              InetSocketAddress clientAddress =
                  (InetSocketAddress) clientChannel.receive(receiveBuffer);

              if (clientAddress != null) {
                Request request = (Request) ObjectDecoder.decodeObject(receiveBuffer);
                logger.info("Сервер получил запрос с командой " + request.getCommandName());

                Response response = commandManager.executeRequest(request);

                ByteBuffer sendBuffer = ObjectEncoder.encodeObject(response);
                channel.send(sendBuffer, clientAddress);
                logger.info("Сервер отправил ответ: " + response.getMessage());
              }
            }
          } catch (ClassNotFoundException e) {
            logger.error("Возникла ошибка на сервере: " + e.getMessage());
          }
        }
      }
    }
  }

  private boolean isConsoleInput() throws IOException {
    if (System.in.available() > 0) {
      String commandName = in.readLine();
      if (commandName == null) {
        commandName = "shutdown";
      }
      switch (commandName) {
        case "shutdown" -> {
          logger.warn("Введена команда 'shutdown'.");
          System.out.println("Сохранение коллекции перед завершением работы сервера...");
          try {
            fileManager.saveCollectionToXml(collectionManager);
            logger.info("Коллекция сохранена в файл.");
          } catch (FileWriteException e) {
            logger.error("Возникла ошибка при сохранении в файл.");
            System.err.println(e.getMessage());
          }
          System.out.println("Завершение работы сервера...");
          logger.info("Сервер завершил свою работу.");
          return true;
        }
        case "save" -> {
          logger.warn("Введена команда 'save'.");
          System.out.println("Сохранение коллекции...");
          try {
            fileManager.saveCollectionToXml(collectionManager);
            logger.info("Коллекция сохранена в файл.");
          } catch (FileWriteException e) {
            logger.error("Возникла ошибка при сохранении в файл.");
            System.err.println(e.getMessage());
          }
          System.out.println("Коллекция успешно сохранена.");
          return false;
        }
        case "" -> {}
        default ->
            System.out.println(
                "Неизвестное имя команды. Введите одну из доступных: shutdown/save.");
      }
    }
    return false;
  }

  private void shutdown(Selector selector, DatagramChannel channel) throws IOException {
    selector.close();
    channel.close();
    requestPool.shutdown();
    isRunning = false;
  }
}
