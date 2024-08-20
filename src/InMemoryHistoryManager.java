import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public final List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        historyList.add(task.getClone());
        if (historyList.size() > 10) {
            historyList.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
