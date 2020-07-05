package Server;

import ServerConnection.Connection;
import Tools.Serializer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.RecursiveAction;

public class ResponseWriter extends RecursiveAction {
    String response;
    Connection connection;
    SelectionKey key;
    Selector selector;
    //ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ResponseWriter(String response, Connection connection, SelectionKey key, Selector selector) {
        this.response = response;
        this.connection = connection;
        this.key = key;
        this.selector = selector;
    }

    @Override
    public void compute() {
        try {
            //readWriteLock.writeLock().lock();
            SocketChannel channel = (SocketChannel) key.channel();
            connection.write(Serializer.toSerialize(response), channel);
            channel.register(selector, SelectionKey.OP_READ);
            //key.channel().register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
            // } finally {
            //     readWriteLock.writeLock().unlock();
            // }
        }
    }
}
