package com.daita.dermi.data;

public class Disease {
    String diseaseName;
    float confidence;
    String information;
    String link;

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Disease(String diseaseName, float confidence, String information, String link) {
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.information = information;
        this.link = link;
    }
}
