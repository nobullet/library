package com.nobullet.concepts;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NIO example (introduced in Java 1.4). Unable to finish: selector doesn't work for some reason for client socket
 * channel. :(
 */
public class JavaNIOExample {

    private static final String HOST = "google.com";
    private static final int PORT = 80;

    static final Logger logger = Logger.getLogger(JavaNIOExample.class.getName());
    static final Charset UTF8 = Charset.forName("UTF-8");

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel channel = SocketChannel.open();

        // we open this channel in non blocking mode
        channel.configureBlocking(false);
        SocketAddress address = new InetSocketAddress(InetAddress.getByName(HOST), PORT);
        channel.connect(address);

        while (!channel.finishConnect()) {
            logger.info("Still connecting...");
        }
        while (true) {
            // see if any message has been received
            ByteBuffer bufferA = ByteBuffer.allocate(20);

            CharBuffer buffer = CharBuffer.wrap("GET /");
            while (buffer.hasRemaining()) {
                channel.write(Charset.defaultCharset().encode(buffer));
            }

            int count = 0;
            String message = "";
            while ((count = channel.read(bufferA)) > 0) {
                bufferA.flip();
                message += Charset.defaultCharset().decode(bufferA);
            }
            if (!message.isEmpty()) {
                logger.info("READ:" + message);
                message = "";
            }

        }
    }

    public static void main2(String... args) throws IOException {
        // Getting address is blocking.
        SocketAddress address = new InetSocketAddress(InetAddress.getByName(HOST), PORT);

        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_CONNECT
                & SelectionKey.OP_READ & SelectionKey.OP_WRITE, "channel1");

        ByteBuffer writeBuffer = ByteBuffer.wrap("GET /".getBytes(Charset.forName("UTF-8")));

        ByteBuffer readBuffer = ByteBuffer.allocate(64 * 1024);
        readBuffer.flip();

        long calls = 0L;

        logger.log(Level.INFO, "Connected: {0}", socketChannel.connect(address));
        while (!socketChannel.finishConnect()) {
            logger.info("Connecting...");
        }
        logger.log(Level.INFO, "Wrote: {0}", socketChannel.write(writeBuffer));
        while (true) {
            int numberOfChannels = selector.select(50L);
            if (numberOfChannels <= 0) {
                calls++;
                if (calls % 100L == 0) {
                    logger.log(Level.INFO, "Looped : {0} connected: {1}", new Object[]{calls, socketChannel.isConnected()});
                }
                continue;
            }
            calls = 0;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                logger.log(Level.INFO, "Selector chose: {0}", key.toString());
                if (key.isAcceptable()) {
                    logger.info("Acceptable...");
                } else if (key.isConnectable()) {
                    logger.info("Connectable...");
                } else if (key.isReadable()) {
                    socketChannel.read(readBuffer);
                    logger.log(Level.INFO, "READ: {0}", new String(readBuffer.array(), UTF8));
                    readBuffer.reset();
                } else if (key.isWritable()) {
                    logger.info("WRITE: ");
                }

                keyIterator.remove();
            }
        }
    }
}
