package com.github.myzhan.locust4j.rpc;

import java.io.IOException;

import com.github.myzhan.locust4j.Log;
import com.github.myzhan.locust4j.message.Message;
import org.zeromq.ZMQ;

/**
 * Locust used to support both plain-socket and zeromq.
 * Since Locust 0.8, it removes the plain-socket implementation.
 *
 * Locust4j only supports zeromq.
 *
 * @author myzhan
 */
public class ZeromqClient implements Client {

    private ZMQ.Context context = ZMQ.context(1);
    private String identity;
    private ZMQ.Socket dealerSocket;

    public ZeromqClient(String host, int port, String nodeID) {
        this.identity = nodeID;
        this.dealerSocket = context.socket(ZMQ.DEALER);
        this.dealerSocket.setIdentity(this.identity.getBytes());
        this.dealerSocket.connect(String.format("tcp://%s:%d", host, port));

        Log.debug(String.format("Locust4j is connected to master(%s:%d)", host, port));
    }

    @Override
    public Message recv() throws IOException {
        byte[] bytes = this.dealerSocket.recv();
        return new Message(bytes);
    }

    @Override
    public void send(Message message) throws IOException {
        byte[] bytes = message.getBytes();
        this.dealerSocket.send(bytes);
    }

    @Override
    public void close() {
        dealerSocket.close();
        context.close();
    }
}

