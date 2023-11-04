package com.hurricanedev.muslimpro.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("timings")
    @Expose
    private Timings timings;

    public Data() {
    }

    public Timings getTimings() {
        return timings;
    }

    public void setTimings(Timings timings) {
        this.timings = timings;
    }

    @Override
    public String toString() {
        return "Data{" +
                "timings=" + timings +
                '}';
    }
}

