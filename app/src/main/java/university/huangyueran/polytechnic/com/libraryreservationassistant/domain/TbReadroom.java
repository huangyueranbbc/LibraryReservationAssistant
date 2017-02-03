package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;
import java.util.Date;

public class TbReadroom implements Serializable {
    private Long id;

    private Long libraryId;

    private String roomName;

    private Integer floor;

    private Integer usedSeat;

    private Integer reservationSeat;

    private Integer isdel;

    private Date created;

    private Date updated;

    private Integer seatCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName == null ? null : roomName.trim();
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getUsedSeat() {
        return usedSeat;
    }

    public void setUsedSeat(Integer usedSeat) {
        this.usedSeat = usedSeat;
    }

    public Integer getReservationSeat() {
        return reservationSeat;
    }

    public void setReservationSeat(Integer reservationSeat) {
        this.reservationSeat = reservationSeat;
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
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

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }
}