package net.overc.zmq.server;

import com.google.common.collect.Lists;
import net.overc.zmq.common.AsyncMessage;
import net.overc.zmq.common.RouterTopic;
import net.overc.zmq.monitor.ConnectionMonitor;
import org.apache.commons.lang.StringUtils;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMonitor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class BiDirectionMessageServer {

    private static final String BINDING_PATTERN = "tcp://%s:%s";
    private final ZContext context;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private AtomicBoolean stop = new AtomicBoolean(false);

    private String internalIp;
    private int serverPort;
    private String publicKey;
    private String secretKey;

    private ZMQ.Socket broker;
    private ZMQ.Socket pullQueue;
    private ZMQ.Socket pushQueue;
    private ZMQ.Poller poller;

    private List<MessageServer> servers = Lists.newArrayList();
    private ConnectionMonitor monitor = new ConnectionMonitor();

    public BiDirectionMessageServer(String internalIp, int serverPort, String publicKey, String secretKey) {
        this.context = new ZContext();
        this.internalIp = internalIp;
        this.serverPort = serverPort;
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    public void register(MessageServer server){
        this.servers.add(server);
    }

    public void startup() {
        this.broker = this.context.createSocket(ZMQ.ROUTER);
        monitor.startup(this.context, this.broker);

        broker.setAsServerCurve(true);
        broker.setCurvePublicKey(publicKey.getBytes());
        broker.setCurveSecretKey(secretKey.getBytes());

        this.broker.bind(String.format(BINDING_PATTERN, internalIp, serverPort));

        pullQueue = context.createSocket(ZMQ.PULL);
        pullQueue.bind("inproc://heartbeat");

        pushQueue = context.createSocket(ZMQ.PUSH);
        pushQueue.connect("inproc://heartbeat");

        this.poller = this.context.createPoller(2);
        this.poller.register(this.broker, ZMQ.Poller.POLLIN);
        this.poller.register(this.pullQueue, ZMQ.Poller.POLLIN);

        executor.execute(() -> {
            while (!stop.get()) {
                this.poller.poll(1000);
                if (poller.pollin(0)) {
                    AsyncMessage message = concat(this.broker);
                    if (message != null) {
                        process(message);
                    }
                } else if (poller.pollin(1)) {
                    String message = this.pullQueue.recvStr();
                    if (StringUtils.isNotEmpty(message)) {
                        send(AsyncMessage.fromJson(message, AsyncMessage.class));
                    }
                }
            }
        });

        servers.forEach(it -> it.startup(pushQueue));
    }

    public void shutdown() {
        stop.set(true);
        monitor.shutdown();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.poller.unregister(this.broker);
        this.poller.unregister(this.pullQueue);

        executor.shutdown();

        broker.close();
        pullQueue.close();
        pushQueue.close();

        context.close();

        servers.forEach(MessageServer::shutdown);

    }

    private void send(AsyncMessage message) {
        broker.sendMore(message.getIdentity());
        broker.sendMore(message.getTopic().name());
        broker.send(message.getMessage());
    }

    private AsyncMessage concat(ZMQ.Socket socket) {
        String identity = socket.recvStr();
        if (parseIdentity(identity)) {
            AsyncMessage message = new AsyncMessage(identity);
            RouterTopic topic = RouterTopic.from(socket.recvStr());
            if (topic != null) {
                message.withTopic(topic);
                String value = socket.recvStr();
                if (value != null) {
                    message.withMessage(value);
                }
            } else {
                return null;
            }

            return message.validate() ? message : null;
        } else {
            return null;
        }

    }

    public void process(AsyncMessage message) {
        if(message == null || !message.validate()){
            return;
        }

        servers.stream().filter(server -> server.support(message)).forEach(server -> server.onMessage(message));
    }

    private boolean parseIdentity(String s) {
        return s != null && s.startsWith("Z-");
    }

}
