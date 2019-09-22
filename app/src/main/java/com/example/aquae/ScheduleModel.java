package com.example.aquae;

public class ScheduleModel {

    private String schedId, clientId, schedule, onOff;

    public ScheduleModel(String schedId, String clientId, String schedule, String onOff) {
        this.schedId = schedId;
        this.clientId = clientId;
        this.schedule = schedule;
        this.onOff = onOff;
    }

    public String getSchedId() {
        return schedId;
    }

    public void setSchedId(String schedId) {
        this.schedId = schedId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getOnOff() {
        return onOff;
    }

    public void setOnOff(String onOff) {
        this.onOff = onOff;
    }

}
