package model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, Status status, String description, int epicId) {
        super(name, status, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}