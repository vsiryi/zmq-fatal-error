package net.overc.zmq.common;

import org.apache.commons.lang.StringUtils;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class AsyncMessage extends BaseBean {

    private String identity;

    private RouterTopic topic;

    private String message;

    public static AsyncMessage create(){
        return new AsyncMessage();
    }

    public static AsyncMessage create(String identity){
        return new AsyncMessage(identity);
    }

    public AsyncMessage(String identity) {
        this.identity = identity;
    }

    public AsyncMessage() {
        //default
    }

    public AsyncMessage withTopic(RouterTopic topic) {
        this.topic = topic;
        return this;
    }

    public AsyncMessage withMessage(String message) {
        this.message = message;
        return this;
    }

    public String getIdentity() {
        return identity;
    }

    public RouterTopic getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    public boolean validate(){
        return StringUtils.isNotEmpty(identity)
                && topic != null
                && StringUtils.isNotEmpty(message);
    }

}
