package com.exapmle.sttproject;

public class VoiceCommandModel {
    String responseId;
    String switchDetail;
    String switchCondition;
    String action;

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

    public String getPoleHeight() {
        return poleHeight;
    }

    public void setPoleHeight(String poleHeight) {
        this.poleHeight = poleHeight;
    }

    String poleHeight;

    public VoiceCommandModel(String responseId, String switchDetail, String switchCondition, String action, String poleHeight) {
        this.responseId = responseId;
        this.switchDetail = switchDetail;
        this.switchCondition = switchCondition;
        this.action = action;
        this.poleHeight = poleHeight;
    }
}
