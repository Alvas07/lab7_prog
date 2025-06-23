package server;

import common.managers.CollectionManager;
import common.managers.CommandManager;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPServer {
  private record RequestTask(Request request, InetSocketAddress clientAddress) {}

  private record ResponseTask(Response response, InetSocketAddress clientAddress) {}

  private final int BUFFER_SIZE = 65535;
  private final int SELECTOR_TIMEOUT = 100;
  private final CommandManager commandManager;
  private final CollectionManager collectionManager;
  private static final Logger logger = LogManager.getLogger();
  private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
  private final AtomicBoolean isRunning = new AtomicBoolean(true);
  private final ReentrantLock selectorLock = new ReentrantLock();

  // чтение запросов
  private final ExecutorService readPool = Executors.newCachedThreadPool();
  // обработка запросов
  private final ForkJoinPool processPool = new ForkJoinPool();
  // отправка ответов
  private final ExecutorService sendPool = Executors.newCachedThreadPool();

  public UDPServer(CommandManager commandManager, CollectionManager collectionManager) {
    this.commandManager = commandManager;
    this.collectionManager = collectionManager;
  }

  public void runServer(int port) throws IOException {
    try (Selector selector = Selector.open();
        DatagramChannel channel = DatagramChannel.open()) {
      channel.configureBlocking(false);
      channel.bind(new InetSocketAddress(port));
      channel.register(selector, SelectionKey.OP_READ);

      logger.info("Сервер запущен на порту " + port);

      ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);

      while (isRunning.get()) {
        if (isConsoleInput()) {
          shutdown();
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
                receiveBuffer.flip();
                byte[] data = new byte[receiveBuffer.remaining()];
                receiveBuffer.get(data);
                receiveBuffer.clear();

                readPool.execute(() -> handleData(data, clientAddress, selector));
              }
            }
          } catch (IOException e) {
            logger.error("Возникла ошибка на сервере: " + e.getMessage());
          }
        }
      }
    }
  }

  private void handleData(byte[] data, InetSocketAddress clientAddress, Selector selector) {
    try {
      Request request = (Request) ObjectDecoder.decodeObject(ByteBuffer.wrap(data));
      logger.info("Получен запрос с командой " + request.getCommandName());

      processPool.execute(() -> processRequest(new RequestTask(request, clientAddress)));
    } catch (IOException | ClassNotFoundException e) {
      logger.error("Возникла ошибка при обработке данных на сервере: " + e.getMessage());
    }
  }

  private void processRequest(RequestTask task) {
    logger.info("Обработка запроса с командой " + task.request().getCommandName());
    Response response = commandManager.executeRequest(task.request());
    sendPool.execute(() -> sendResponse(new ResponseTask(response, task.clientAddress())));
  }

  private void sendResponse(ResponseTask task) {
    try (DatagramChannel channel = DatagramChannel.open()) {
      ByteBuffer sendBuffer = ObjectEncoder.encodeObject(task.response());
      channel.send(sendBuffer, task.clientAddress());
      logger.info("Сервер отправил ответ клиенту: " + task.response().getMessage());
    } catch (IOException e) {
      logger.error("Возникла ошибка при отправке ответа клиенту: " + e.getMessage());
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
          System.out.println("Завершение работы сервера...");
          logger.info("Сервер завершил свою работу.");
          return true;
        }
        case "" -> {}
        default -> System.out.println("Неизвестное имя команды. Доступна только команда shutdown.");
      }
    }
    return false;
  }

  private void shutdown() {
    isRunning.set(false);

    shutdownPool(readPool, "ReadPool");
    shutdownPool(processPool, "ProcessPool");
    shutdownPool(sendPool, "SendPool");

    logger.info("Сервер завершил работу.");
  }

  private void shutdownPool(ExecutorService pool, String poolName) {
    pool.shutdown();
    try {
      if (!pool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
        pool.shutdownNow();
        logger.warn("Принудительное завершение " + poolName);
      }
    } catch (InterruptedException e) {
      pool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
