package net.overc.zmq.server;

import net.overc.zmq.common.AsyncMessage;
import net.overc.zmq.common.RouterTopic;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public abstract class BaseMessageServer implements MessageServer {

    protected BlockingQueue<String> queue;

    protected void send(String identity, RouterTopic topic, String message){
        AsyncMessage asyncMessage = AsyncMessage.create(identity)
                .withTopic(topic)
                .withMessage(message);

        try {
            queue.put(asyncMessage.toJson());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean support(AsyncMessage message) {
        return topics().contains(message.getTopic());
    }

    @Override
    public void startup(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void shutdown() {
        //nothing
    }

    abstract Set<RouterTopic> topics();

}
