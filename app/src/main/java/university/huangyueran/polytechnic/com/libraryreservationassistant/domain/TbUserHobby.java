package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;

public class TbUserHobby implements Serializable {
    private Long userId;

    private String userHobby;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserHobby() {
        return userHobby;
    }

    public void setUserHobby(String userHobby) {
        this.userHobby = userHobby;
    }
}