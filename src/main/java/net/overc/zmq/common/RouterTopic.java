package net.overc.zmq.common;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public enum RouterTopic {

    I, //init
    P,  //pong
    E; //event

    public static RouterTopic from(String value){
        for(RouterTopic topic : values()){
            if(topic.name().equals(value)){
                return topic;
            }
        }

        return null;
    }

}
