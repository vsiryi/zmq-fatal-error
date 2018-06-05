package net.overc.zmq.common;

import java.util.Objects;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class InitMessageBean extends BaseBean {

    private String identity;

    private Long userId;

    public static InitMessageBean build(Long userId){
        InitMessageBean bean = new InitMessageBean();
        bean.setUserId(userId);
        return bean;
    }

    public InitMessageBean withIdentity(String identity){
        InitMessageBean bean = new InitMessageBean();
        bean.userId = this.userId;
        bean.identity = identity;
        return bean;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIdentity() {
        return identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitMessageBean that = (InitMessageBean) o;
        return Objects.equals(identity, that.identity) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(identity, userId);
    }
}
