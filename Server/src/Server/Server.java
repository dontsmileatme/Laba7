package Server;

import ServerConnection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    static Selector selector = null;
    ServerSocketChannel channel = ServerSocketChannel.open();
    ServerSocket serverSocket = channel.socket();
    SelectionKey key;
    Connection connection = new Connection();
    private String response = null;
    int maxThreads;

    public Server(int port, int maxThreads) throws IOException {
        try {
            selector = Selector.open();
            serverSocket.bind(new InetSocketAddress("localhost", port));
            channel.configureBlocking(false);
            int ops = channel.validOps();
            channel.register(selector, ops, null);
            ForkJoinPool pool = new ForkJoinPool();
            ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            this.maxThreads = maxThreads;
            System.out.println("Сервер запущен.");
            System.out.println("\nВведите 'save' для сохранения коллекции в базе данных или 'exit' для выхода:");

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    key = keys.next();

                    if (key.isAcceptable()) {
                        SocketChannel client = channel.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }

                    if (key.isReadable()) {
                        try {
                            readWriteLock.readLock().lock();
                            RequestReader requestReader = new RequestReader(key, connection, selector, maxThreads);
                            FutureTask<String> futureTask = new FutureTask<>(requestReader);
                            Thread.sleep(100);
                            Thread thread = new Thread(futureTask);
                            thread.start();
                            response = futureTask.get();
                            if (response == null) {
                                keys.remove();
                                continue;
                            }
                        } finally {
                            readWriteLock.readLock().unlock();
                        }
                        try {
                            readWriteLock.writeLock().lock();
                            ResponseWriter responseWriter = new ResponseWriter(response, connection, key, selector);
                            pool.execute(responseWriter);
                            response = null;
                        } finally {
                            readWriteLock.writeLock().unlock();
                        }
                    }
                    keys.remove();
                }
            }

        } catch (IOException e) {
            System.err.println("Соединение разорвано.");
            e.printStackTrace();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
