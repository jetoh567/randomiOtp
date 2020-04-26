package com.example.randomiotp;

import java.io.Serializable;

public class Global implements Serializable {

    private String seed;
    private String nowDate;
    private String nowDateMinus;
    private String nowDatePlus;

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getNowDate() {
        return nowDate;
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }

    public String getNowDateMinus() {
        return nowDateMinus;
    }

    public void setNowDateMinus(String nowDateMinus) {
        this.nowDateMinus = nowDateMinus;
    }

    public String getNowDatePlus() {
        return nowDatePlus;
    }

    public void setNowDatePlus(String nowDatePlus) {
        this.nowDatePlus = nowDatePlus;
    }

}
