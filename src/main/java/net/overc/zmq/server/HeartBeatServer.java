package net.overc.zmq.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.overc.zmq.common.AsyncMessage;
import net.overc.zmq.common.InitMessageBean;
import net.overc.zmq.common.RouterTopic;
import org.zeromq.ZMQ;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat server track  online users and map connection identity with user id
 *
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class HeartBeatServer extends BaseMessageServer {

    private Set<RouterTopic> topics = Sets.immutableEnumSet(RouterTopic.I, RouterTopic.P);

    private Map<String, Long> workers = Maps.newConcurrentMap();
    private SessionListener listener;

    private int sessionTimeout;
    private int beatInterval;

    private ScheduledExecutorService timer;

    public HeartBeatServer(SessionListener listener, int sessionTimeout, int beatInterval) {
        this.listener = listener;
        this.sessionTimeout = sessionTimeout * 1000; //convert to millisecond;
        this.beatInterval = beatInterval;
    }

    @Override
    public void startup(BlockingQueue<String> queue) {
        super.startup(queue);

        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleWithFixedDelay(() -> {
            try {
                tick();
                removeExpiredSessions();
            } catch (Exception t) {
                t.printStackTrace(System.err);
            }
        }, 0, beatInterval, TimeUnit.SECONDS);
    }

    @Override
    Set<RouterTopic> topics() {
        return this.topics;
    }

    @Override
    public void onMessage(AsyncMessage message) {
        Long lastVisit = workers.get(message.getIdentity());
        switch (message.getTopic()) {
            case I:
                if(lastVisit == null){
                    workers.put(message.getIdentity(), System.currentTimeMillis());
                    listener.onSessionCreate(
                            message.getIdentity(),
                            InitMessageBean.fromJson(message.getMessage(), InitMessageBean.class)
                    );
                    send(message.getIdentity(), RouterTopic.P, String.valueOf(System.currentTimeMillis()));
                }
                break;
            case P:
                if(lastVisit != null){
                    workers.put(message.getIdentity(), System.currentTimeMillis());
                }
                break;
        }
    }

    private void tick() {
        //send ping message for each client
        workers.keySet().forEach(it -> {
            send(it, RouterTopic.P, String.valueOf(System.currentTimeMillis()));
        });
    }

    private void removeExpiredSessions() {
        Map<String, Long> copy = Maps.newHashMap(workers);
        final long current = System.currentTimeMillis();
        copy.forEach((k, v) -> {
            if(current > v + sessionTimeout){
                workers.remove(k);
                listener.onSessionDestroy(k);
            }
        });
    }

    @Override
    public void shutdown() {
        timer.shutdown();
    }
}
