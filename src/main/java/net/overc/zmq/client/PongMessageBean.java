package net.overc.zmq.client;

import net.overc.zmq.common.BaseBean;

/**
 * Date: 5/31/18
 *
 * @author Vitalii Siryi
 */
public class PongMessageBean extends BaseBean {

    //some statistics here
    private long userId;

    public static PongMessageBean build(Long userId){
        PongMessageBean bean = new PongMessageBean();
        bean.setUserId(userId);
        return bean;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
