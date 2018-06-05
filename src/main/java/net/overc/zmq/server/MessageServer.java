package net.overc.zmq.server;

import net.overc.zmq.common.AsyncMessage;
import org.zeromq.ZMQ;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public interface MessageServer {

    boolean support(AsyncMessage message);

    void onMessage(AsyncMessage message);

    void startup(ZMQ.Socket queue);

    void shutdown();

}
