package com.efei.student.tablet.models;

/**
 * Created by jesse on 15-9-17.
 */
public class VideoState {
    public String dataSource;
    public long progress;
    public boolean isPause;

    public VideoState(String dataSource, long progress, boolean isPause) {
        this.dataSource = dataSource;
        this.progress = progress;
        this.isPause = isPause;
    }

    public void reset() {
        this.dataSource = "";
        this.progress = 0;
        this.isPause = false;
    }
}
