package net.overc.zmq.server;

import com.google.common.collect.Sets;
import net.overc.zmq.common.AsyncMessage;
import net.overc.zmq.common.RouterTopic;
import org.zeromq.ZMQ;

import java.util.Set;

/**
 * This implementation allow to send message (event) to a connection with necessary identity
 *
 * Date: 5/31/18
 *
 * @author Vitalii Siryi
 */
public class EventServer extends BaseMessageServer {

    @Override
    Set<RouterTopic> topics() {
        return Sets.newHashSet();
    }

    @Override
    public void onMessage(AsyncMessage message) {
        //do not handle messages
    }

    @Override
    public void startup(ZMQ.Socket queue) {
        this.queue = queue;
    }

    @Override
    public void shutdown() {
        //nothing
    }

    public void send(String identity, String uuid) {
        send(identity, RouterTopic.E, uuid);
    }
}
