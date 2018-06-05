package net.overc.zmq.client;

import net.overc.zmq.common.AsyncMessage;
import net.overc.zmq.common.InitMessageBean;
import net.overc.zmq.common.RouterTopic;
import net.overc.zmq.server.SecurityKeys;
import org.apache.commons.lang.StringUtils;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import zmq.io.mechanism.curve.Curve;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Date: 5/31/18
 *
 * @author Vitalii Siryi
 */
public class MessageClient {

    private static Random rand = new Random(System.currentTimeMillis());
    private static final String BINDING_PATTERN = "tcp://%s:%s";
    private static final String LOCALHOST = "localhost";
    private static final long DEFAULT_TIMEOUT = 5 * 1000;
    private static final int DEFAULT_PORT = 55667;
    private final ZContext context;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private AtomicBoolean stop = new AtomicBoolean(false);

    private long lastBeatTime;
    private long timeout;

    private long userId;
    private String serverHost;
    private int serverPort;

    private String serverPublicKey;

    private ZMQ.Socket pullLocalQueue;
    private ZMQ.Socket pushLocalQueue;
    private ZMQ.Socket worker;
    private ZMQ.Poller poller;

    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();

    public MessageClient(long userId){
        this(DEFAULT_PORT, userId, SecurityKeys.get().getPublicKey());
    }

    public MessageClient(int serverPort, long userId, String serverPublicKey){
        this(LOCALHOST, serverPort, userId, DEFAULT_TIMEOUT, serverPublicKey);
    }

    public MessageClient(String serverHost, int serverPort, long userId, long timeout,
                        String serverPublicKey) {
        this.context = new ZContext();
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.userId = userId;
        this.timeout = timeout;
        this.serverPublicKey = serverPublicKey;
    }

    public void startup() {
        this.worker = this.context.createSocket(ZMQ.DEALER);
        setId(this.worker); //  Set a printable identity

        Curve curve = new Curve();
        String[] clientKeys = curve.keypairZ85();

        this.worker.setCurvePublicKey(clientKeys[0].getBytes());
        this.worker.setCurveSecretKey(clientKeys[1].getBytes());

        this.worker.setCurveServerKey(serverPublicKey.getBytes());

        this.worker.connect(String.format(BINDING_PATTERN, serverHost, serverPort));

        pullLocalQueue = context.createSocket(ZMQ.PULL);
        pullLocalQueue.bind("inproc://local");

        pushLocalQueue = context.createSocket(ZMQ.PUSH);
        pushLocalQueue.connect("inproc://local");

        lastBeatTime = System.currentTimeMillis();

        this.poller = this.context.createPoller(2);
        this.poller.register(this.worker, ZMQ.Poller.POLLIN);
        this.poller.register(this.pullLocalQueue, ZMQ.Poller.POLLIN);

        executor.execute(() -> {
            while (!stop.get()) {
                this.poller.poll(1000);
                if (poller.pollin(0)) {
                    lastBeatTime = System.currentTimeMillis();
                    read(this.worker);
                } else if(poller.pollin(1)) {
                    route(this.pullLocalQueue, this.worker);
                }

                if(System.currentTimeMillis() > lastBeatTime + timeout){
                    shutdown();
                }
            }
        });

        //init connection to the server
        send(RouterTopic.I, InitMessageBean.build(userId).toJson());

        System.out.println(String.format("Client %s connected to server", userId));
    }

    public void shutdown() {
        stop.set(true);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.poller.unregister(this.worker);
        this.poller.unregister(this.pullLocalQueue);

        executor.shutdown();

        pullLocalQueue.close();
        worker.close();

        pushLocalQueue.close();
        context.close();
    }

    private void read(ZMQ.Socket socket) {
        RouterTopic topic = RouterTopic.from(socket.recvStr());
        if (topic != null) {
            String message = socket.recvStr();
            if (StringUtils.isNotEmpty(message)) {

                if(RouterTopic.P.equals(topic)){
                    send(RouterTopic.P, PongMessageBean.build(userId).toJson());
                } else if(RouterTopic.C.equals(topic)){
                    commands.add(message);
                }
            }
        }
    }

    private void route(ZMQ.Socket fromSocket, ZMQ.Socket toSocket){
        AsyncMessage message = concat(fromSocket);
        if(message != null){
            toSocket.sendMore(message.getTopic().name());
            toSocket.send(message.getMessage());
        }
    }

    private AsyncMessage concat(ZMQ.Socket socket){
        RouterTopic topic = RouterTopic.from(socket.recvStr());
        if(topic != null){
            String value = socket.recvStr();
            if(StringUtils.isNotEmpty(value)){
                return AsyncMessage.create().withTopic(topic).withMessage(value);
            }
        }

        return null;
    }

    private void send(RouterTopic topic, String message){
        pushLocalQueue.sendMore(topic.name());
        pushLocalQueue.send(message);
    }

    private static void setId(ZMQ.Socket sock) {
        String identity = String.format("Z-%04X-%04X", rand.nextInt(), rand.nextInt());
        sock.setIdentity(identity.getBytes(ZMQ.CHARSET));
    }

    public String pollCommand(int waitSeconds) throws Exception {
        return commands.poll(waitSeconds, TimeUnit.SECONDS);
    }

    public void clear(){
        commands.clear();
    }

}
