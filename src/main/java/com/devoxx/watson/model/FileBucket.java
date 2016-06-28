package com.devoxx.watson.model;

import org.springframework.web.multipart.MultipartFile;

public class FileBucket {

    private MultipartFile file;

    private String link;

    private String docName;

    private String speakers;

    private String audioAbstract;

    public String getSpeakers() {
        return speakers;
    }

    @SuppressWarnings("unused")
    public void setSpeakers(final String speakers) {
        this.speakers = speakers;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getDocName() {
        return docName;
    }

    public String getAudioAbstract() {
        return audioAbstract;
    }

    @SuppressWarnings("unused")
    public void setAudioAbstract(String audioAbstract) {
        this.audioAbstract = audioAbstract;
    }

    @SuppressWarnings("unused")
    public void setDocName(final String docName) {
        this.docName = docName;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
