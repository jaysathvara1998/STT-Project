package com.exapmle.sttproject;

public class VoiceCommandModel {
    String responseId;

    public VoiceCommandModel(String responseId, String switchDetail, String switchCondition, String action) {
        this.responseId = responseId;
        this.switchDetail = switchDetail;
        this.switchCondition = switchCondition;
        this.action = action;
    }

    String switchDetail;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getSwitchDetail() {
        return switchDetail;
    }

    public void setSwitchDetail(String switchDetail) {
        this.switchDetail = switchDetail;
    }

    public String getSwitchCondition() {
        return switchCondition;
    }

    public void setSwitchCondition(String switchCondition) {
        this.switchCondition = switchCondition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    String switchCondition;
    String action;
}
