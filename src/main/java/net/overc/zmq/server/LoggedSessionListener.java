package net.overc.zmq.server;

import net.overc.zmq.common.InitMessageBean;

import java.util.Set;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class LoggedSessionListener implements SessionListener {

    private Set<String> identities;

    public LoggedSessionListener(Set<String> identities) {
        this.identities = identities;
    }

    @Override
    public void onSessionCreate(String identity, InitMessageBean bean) {
        System.out.println(String.format("Identity %s connected", identity));
        identities.add(identity);
    }

    @Override
    public void onSessionDestroy(String identity) {
        identities.remove(identity);
        System.out.println(String.format("Identity %s disconnected", identity));
    }
}
