package com.vorogushinigor.notes.other;

/**
 * Created by viv on 07.07.2016.
 */
public class Notes {
    private String name;
    private String main;
    private String timeCreated;
    private String timeChanged;
    private String pathPhoto;

    public Notes(String name, String main, String timeCreated, String timeChanged, String pathPhoto) {
        this.name = name;
        this.main = main;
        this.timeCreated = timeCreated;
        this.timeChanged = timeChanged;
        this.pathPhoto = pathPhoto;
    }


    public String getName() {
        return name;
    }

    public String getMain() {
        return main;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getTimeChanged() {
        return timeChanged;
    }

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setTimeChanged(String timeChanged) {
        this.timeChanged = timeChanged;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
    }
}
