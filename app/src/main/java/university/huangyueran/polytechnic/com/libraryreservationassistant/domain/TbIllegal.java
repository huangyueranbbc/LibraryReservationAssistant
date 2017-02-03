package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;
import java.util.Date;

public class TbIllegal implements Serializable {
    private Long id;

    private Long userId;

    private String nickname;

    private Date illegalTime;

    private String illegalInfo;

    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public Date getIllegalTime() {
        return illegalTime;
    }

    public void setIllegalTime(Date illegalTime) {
        this.illegalTime = illegalTime;
    }

    public String getIllegalInfo() {
        return illegalInfo;
    }

    public void setIllegalInfo(String illegalInfo) {
        this.illegalInfo = illegalInfo == null ? null : illegalInfo.trim();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}