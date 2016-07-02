package com.github.ma1co.openmemories.appstore;

import java.io.Serializable;

public class App implements Serializable {
    public final String id;
    public final String author;
    public final String name;
    public final String desc;
    public final int rank;
    public final String releaseVersion;
    public final String releaseDesc;

    public App(String id, String author, String name, String desc, int rank, String releaseVersion, String releaseDesc) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.desc = desc;
        this.rank = rank;
        this.releaseVersion = releaseVersion;
        this.releaseDesc = releaseDesc;
    }
}
