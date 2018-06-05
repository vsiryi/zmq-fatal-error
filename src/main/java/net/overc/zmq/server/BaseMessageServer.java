package net.overc.zmq.server;

import net.overc.zmq.common.AsyncMessage;
import net.overc.zmq.common.RouterTopic;
import org.zeromq.ZMQ;

import java.util.Set;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public abstract class BaseMessageServer implements MessageServer {

    protected ZMQ.Socket queue;

    protected void send(String identity, RouterTopic topic, String message){
        AsyncMessage asyncMessage = AsyncMessage.create(identity)
                .withTopic(topic)
                .withMessage(message);

        queue.send(asyncMessage.toJson(), 0);
    }

    @Override
    public boolean support(AsyncMessage message) {
        return topics().contains(message.getTopic());
    }

    abstract Set<RouterTopic> topics();

}
