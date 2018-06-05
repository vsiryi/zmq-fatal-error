package net.overc.zmq.server;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class ServerRunner {

    private static String SERVER_IP = "localhost";
    private static int SERVER_PORT = 55667;
    private static int SESSION_TIMEOUT = 10;
    private static int BEAT_INTERVAL = 1;

    private BiDirectionMessageServer server;
    private Set<String> identities = Sets.newConcurrentHashSet();

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private AtomicBoolean stop = new AtomicBoolean(false);
    private EventServer eventServer = new EventServer();

    public void startup() {
        server = new BiDirectionMessageServer(
                SERVER_IP,
                SERVER_PORT,
                SecurityKeys.get().getPublicKey(),
                SecurityKeys.get().getSecretKey()
        );

        server.register(
                new HeartBeatServer(
                        new LoggedSessionListener(identities),
                        SESSION_TIMEOUT,
                        BEAT_INTERVAL)
        );

        server.register(eventServer);

        server.startup();

        executor.execute(() -> {
            while (!stop.get()) {
                identities.forEach(it ->{
                    eventServer.send(it, UUID.randomUUID().toString());
                });
            }
        });

        System.out.println(String.format("Server started up on port %s", SERVER_PORT));
    }

    public void shutdown() {
        if(server != null){
            server.shutdown();
        }
        System.out.println("Server shutdown");
    }

}
