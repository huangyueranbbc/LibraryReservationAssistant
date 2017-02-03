package com.seatview.seatchoosetest;

import com.seatview.seatchoosetest.model.CH_seatInfo;

public abstract interface OnNewSeatClickListener {
    /**
     * 取消选择
     *
     * @return
     */
    public abstract boolean unClick(CH_seatInfo seatInfo);

    /**
     * 点击选择
     *
     * @return
     */
    public abstract boolean onClick(CH_seatInfo seatInfo);
}