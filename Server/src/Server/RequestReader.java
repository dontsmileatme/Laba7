package Server;

import ServerConnection.Connection;
import Tools.Deserializer;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RequestReader implements Callable<String> {
    SelectionKey key;
    Connection connection;
    Selector selector;
    int maxThreads;

    public RequestReader(SelectionKey key, Connection connection, Selector selector, int maxThreads) {
        this.key = key;
        this.connection = connection;
        this.selector = selector;
        this.maxThreads = maxThreads;
    }

    @Override
    public String call() {
        try {

            SocketChannel channel = (SocketChannel) key.channel();
            byte[] bytes = connection.read(channel);
            key.channel().register(selector, SelectionKey.OP_WRITE);
            if (bytes == null) return null;
            int flag = Deserializer.flagCheck(bytes);
            ExecutorService service = Executors.newFixedThreadPool(maxThreads);
            CommandExecutor executor = new CommandExecutor(flag, bytes);
            Future future = service.submit(executor);
            Thread.sleep(100);
            String response = (String) future.get();
            service.shutdown();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
