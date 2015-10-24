package com.nobullet.concepts;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Async NIO.2 example (introduced in Java 1.7). Requests /robots.txt from server, while waiting
 * {@link AsynchronousSocketChannel} to finish it's job using single thread pool. Uses wait/notify mechanism to wait for
 * the end of the request. {@link java.nio.channels.AsynchronousServerSocketChannel} has 'accept' method with
 * {@link CompletionHandler} that receives new channel (connection).
 */
public class JavaAsyncNIOExample {

    static final String HOST = "www.google.com";
    static final int PORT = 80;
    static final Charset UTF8 = Charset.forName("UTF-8");
    static final long TIMEOUT_SECONDS = 30;
    static final long PROCESS_TIMEOUT_SECONDS = TIMEOUT_SECONDS * 2 + 1;
    static final int BUFFER_SIZE = 512 * 1024;
    static volatile boolean requested = false;

    // Logger.
    static final Logger logger = Logger.getLogger(JavaAsyncNIOExample.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketAddress address = new InetSocketAddress(InetAddress.getByName(HOST), PORT);

        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(ForkJoinPool.commonPool());

        try (AsynchronousSocketChannel asyncChannel = AsynchronousSocketChannel.open(group)) {
            logger.info("Connecting...");
            asyncChannel.connect(address, address, new ConnectionComplete(asyncChannel));
            synchronized (asyncChannel) {
                while (!requested) {
                    logger.info("Waiting to finish...");
                    asyncChannel.wait(PROCESS_TIMEOUT_SECONDS * 1000L);
                }
            }
        } finally {
            group.shutdown();
            logger.info("Exiting.");
        }
    }

    /**
     * Finalizer for failed stages and final successful stage when response has been received.
     * @param channel Channel to notify.
     */
    static void onFinish(AsynchronousSocketChannel channel) {
        requested = true;
        synchronized (channel) {
            channel.notify();
        }
    }

    /**
     * Handler to be invoked when response is received.
     */
    static class ResponseComplete implements CompletionHandler<Integer, SocketAddress> {

        ByteBuffer readBuffer;
        AsynchronousSocketChannel asyncChannel;

        public ResponseComplete(ByteBuffer readBuffer, AsynchronousSocketChannel asyncChannel) {
            this.readBuffer = readBuffer;
            this.asyncChannel = asyncChannel;
        }

        @Override
        public void completed(Integer result, SocketAddress address) {
            readBuffer.flip();

            String log = String.format("Thread: %s. Response received: %s. Bytes read: %d. Read buffer: %s",
                    Thread.currentThread().getName(), address, result, readBuffer.toString());
            logger.info(log);

            String response = new String(readBuffer.array(), 0, readBuffer.limit(), UTF8); // Read only actual bytes.
            logger.info(String.format("\n\nRESPONSE (may be gzipped):\n%s\n\n%d bytes.", response, readBuffer.limit()));
            onFinish(asyncChannel);
        }

        @Override
        public void failed(Throwable exc, SocketAddress address) {
            String log = String.format("Thread: %s. Failed to receive: %s", Thread.currentThread().getName(), address);
            logger.log(Level.SEVERE, log, exc);
            onFinish(asyncChannel);
        }
    }

    /**
     * Handler to be invoked when request is sent completely.
     */
    static class RequestComplete implements CompletionHandler<Integer, SocketAddress> {

        AsynchronousSocketChannel asyncChannel;

        public RequestComplete(AsynchronousSocketChannel asyncChannel) {
            this.asyncChannel = asyncChannel;
        }

        @Override
        public void completed(Integer result, SocketAddress address) {
            String log = String.format("Thread: %s. Request sent to: %s", Thread.currentThread().getName(), address);
            logger.info(log);
            ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            asyncChannel.read(readBuffer, TIMEOUT_SECONDS, TimeUnit.SECONDS, address,
                    new ResponseComplete(readBuffer, asyncChannel));
        }

        @Override
        public void failed(Throwable exc, SocketAddress address) {
            String log = String.format("Thread: %s. Failed to send: %s", Thread.currentThread().getName(), address);
            logger.log(Level.SEVERE, log, exc);
            onFinish(asyncChannel);
        }
    }

    /**
     * Connection complete handler.
     */
    static class ConnectionComplete implements CompletionHandler<Void, SocketAddress> {

        AsynchronousSocketChannel asyncChannel;

        public ConnectionComplete(AsynchronousSocketChannel asyncChannel) {
            this.asyncChannel = asyncChannel;
        }

        @Override
        public void completed(Void result, SocketAddress address) {
            String log = String.format("Thread: %s. Connected to: %s", Thread.currentThread().getName(), address);
            logger.info(log);
            ByteBuffer request = buildGetRequest(HOST, "/robots.txt");
            logger.log(Level.INFO, "Request:\n\n{0}", new String(request.array(), UTF8));
            asyncChannel.write(request, TIMEOUT_SECONDS, TimeUnit.SECONDS, address, new RequestComplete(asyncChannel));
        }

        @Override
        public void failed(Throwable exc, SocketAddress address) {
            String log = String.format("Thread: %s. Failed to connect: %s", Thread.currentThread().getName(), address);
            logger.log(Level.SEVERE, log, exc);
            onFinish(asyncChannel);
        }
    }

    static ByteBuffer buildGetRequest(String host, String url) {
        StringBuilder sb = new StringBuilder("GET ");
        sb.append(url).append(" HTTP/1.1\r\n");
        sb.append("Host: ").append(host.trim()).append("\r\n");
        sb.append("Connection: Keep-Alive\r\n");
        sb.append("User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n\r\n");
        return ByteBuffer.wrap(sb.toString().getBytes(UTF8));
    }
}
