package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> nodesMap = new HashMap<>();

    Node<Task>  head;
    Node<Task>  tail;
    int size;

    @Override
    public void add(Task task) {
        Task clone = task.getClone();
        if (!nodesMap.containsKey(task.getId())) {
            this.linkLast(clone);
        } else {
            Node<Task> node = nodesMap.get(task.getId());
            this.replaceNode(node, clone);
        }
    }

    @Override
    public void remove(int id) {
        if (nodesMap.containsKey(id)) {
            Node<Task> node = nodesMap.get(id);
            this.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return this.getTasks();
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        nodesMap.put(task.getId(), newNode);
        size++;
    }

    private List<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Node<Task> x = head; x != null; x = x.next) {
            list.add(x.task);
        }
        return list;
    }

    private void replaceNode(Node<Task> x, Task task) {
        if (tail == x) {
            x.task = task;
            return;
        }
        Node<Task> next = x.next;
        Node<Task> prev = x.prev;
        if (next == tail) {
            next.next = x;
            x.prev = next;
            x.next = null;
            tail = x;
        } else {
            Node<Task> last = tail;
            last.next = x;
            x.prev = last;
            x.next = null;
            tail = x;
        }
        if (prev == null) {
            head = next;
            next.prev = null;
        } else {
            prev.next = next;
            next.prev = prev;
        }
        x.task = task;
    }

    private void removeNode(Node<Task> x) {
        if (x == null) {
            throw new IllegalArgumentException("Node не может быть пустым");
        }
        final Node<Task> next = x.next;
        final Node<Task> prev = x.prev;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            x.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.task = null;
        size--;
    }

    public static class Node<Task> {
        Node<Task> next;
        Node<Task> prev;
        Task task;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.next = next;
            this.prev = prev;
            this.task = data;
        }
    }
}
