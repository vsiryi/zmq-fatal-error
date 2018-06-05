package net.overc.zmq;

import net.overc.zmq.client.MessageClient;

/**
 * Date: 5/31/18
 *
 * @author Vitalii Siryi
 */
public class ClientApplication {

    public static void main(String[] args) {
        MessageClient client = new MessageClient(1);
        client.startup();
    }

}
