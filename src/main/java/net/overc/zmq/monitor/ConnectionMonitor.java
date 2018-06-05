package net.overc.zmq.monitor;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMonitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Date: 5/31/18
 *
 * @author Vitalii Siryi
 */
public class ConnectionMonitor {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private AtomicBoolean stop = new AtomicBoolean(false);

    public void startup(ZContext ctx, ZMQ.Socket socket){
        final ZMonitor monitor = new ZMonitor(ctx, socket);
        monitor.verbose(false);
        monitor.add(
                ZMonitor.Event.LISTENING,
                ZMonitor.Event.ACCEPTED,
                ZMonitor.Event.HANDSHAKE_PROTOCOL,
                ZMonitor.Event.CLOSED,
                ZMonitor.Event.DISCONNECTED
        );
        monitor.start();

        executor.execute(() -> {
            while (!stop.get()) {
                ZMonitor.ZEvent event = monitor.nextEvent(1000);
                if(event != null){
                    System.out.println(
                            String.format(
                                    "type:%s, address:%s",
                                    event.type,
                                    event.address
                            )
                    );
                }
            }
        });
    }

    public void shutdown() {
        stop.set(true);
        executor.shutdown();
    }
}
