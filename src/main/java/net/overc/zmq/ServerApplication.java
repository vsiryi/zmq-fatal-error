package net.overc.zmq;

import net.overc.zmq.server.ServerRunner;

/**
 * Date: 5/31/18
 *
 * @author Vitalii Siryi
 */
public class ServerApplication {

    public static void main(String[] args) {
        ServerRunner runner = new ServerRunner();
        runner.startup();
    }

}
