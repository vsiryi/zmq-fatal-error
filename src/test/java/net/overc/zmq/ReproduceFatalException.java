package net.overc.zmq;

import net.overc.zmq.client.MessageClient;
import net.overc.zmq.server.ServerRunner;
import org.junit.Test;

/**
 * Date: 6/5/18
 *
 * @author Vitalii Siryi
 */
public class ReproduceFatalException {

    @Test
    public void reproduce() throws Exception {
        //run server
        ServerRunner runner = new ServerRunner();
        runner.startup();

        Thread.sleep(2000);

        //run 15 clients in a loop
        for(int i = 0; i < 25; i++){
            MessageClient client = new MessageClient(i);
            client.startup();
            Thread.sleep(500);
        }

        Thread.sleep(5000);
    }

}
