package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;
import java.util.Date;

public class TbSeat  implements Serializable {
    private Long id;

    private Long readroomId;

    private Integer status;

    private Date expireTime;

    private Date created;

    private Date updated;

    private Integer seatIndex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReadroomId() {
        return readroomId;
    }

    public void setReadroomId(Long readroomId) {
        this.readroomId = readroomId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Integer getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(Integer seatIndex) {
        this.seatIndex = seatIndex;
    }
}