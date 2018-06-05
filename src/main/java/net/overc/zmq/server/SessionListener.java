package net.overc.zmq.server;

import net.overc.zmq.common.InitMessageBean;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public interface SessionListener {

    void onSessionCreate(String identity, InitMessageBean bean);

    void onSessionDestroy(String identity);

}
