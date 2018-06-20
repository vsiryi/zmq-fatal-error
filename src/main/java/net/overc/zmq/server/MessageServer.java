package net.overc.zmq.server;

import net.overc.zmq.common.AsyncMessage;
import org.zeromq.ZMQ;

import java.util.concurrent.BlockingQueue;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public interface MessageServer {

    boolean support(AsyncMessage message);

    void onMessage(AsyncMessage message);

    void startup(BlockingQueue<String> queue);

    void shutdown();

}
