package university.huangyueran.polytechnic.com.libraryreservationassistant.domain;

import java.io.Serializable;

public class TbSeatInfo implements Serializable {
    private Long seatId;

    private String qrCode;

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode == null ? null : qrCode.trim();
    }
}