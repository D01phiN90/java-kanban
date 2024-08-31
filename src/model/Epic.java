package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, Status.NEW, description);
        this.subTasksIds = new ArrayList<>();
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTask(int subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public void removeSubTask(int subTaskId) {
        subTasksIds.remove(Integer.valueOf(subTaskId));
    }

    public void clearSubTasks() {
        subTasksIds.clear();
    }
}