package com.tky.lte.ui.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by I am on 2018/6/23.
 */
@Entity
public class CallJilEntity {
    @Id(autoincrement = true)
    private Long id;
    private String funNumberName;
    private String funNumber;
    private String peerNumber;
    private String date;
    private int call_way;
    private String status;
    private int callType;

    @Generated(hash = 321955378)
    public CallJilEntity(Long id, String funNumberName, String funNumber,
            String peerNumber, String date, int call_way, String status,
            int callType) {
        this.id = id;
        this.funNumberName = funNumberName;
        this.funNumber = funNumber;
        this.peerNumber = peerNumber;
        this.date = date;
        this.call_way = call_way;
        this.status = status;
        this.callType = callType;
    }
    @Generated(hash = 2004567689)
    public CallJilEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFunNumberName() {
        return this.funNumberName;
    }
    public void setFunNumberName(String funNumberName) {
        this.funNumberName = funNumberName;
    }
    public String getFunNumber() {
        return this.funNumber;
    }
    public void setFunNumber(String funNumber) {
        this.funNumber = funNumber;
    }
    public String getPeerNumber() {
        return this.peerNumber;
    }
    public void setPeerNumber(String peerNumber) {
        this.peerNumber = peerNumber;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getCall_way() {
        return this.call_way;
    }
    public void setCall_way(int call_way) {
        this.call_way = call_way;
    }
    public int getCallType() {
        return this.callType;
    }
    public void setCallType(int callType) {
        this.callType = callType;
    }
}
