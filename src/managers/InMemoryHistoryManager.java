package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodesMap = new HashMap<>();

    private Node head;
    private Node tail;
    private int size;

    @Override
    public void add(Task task) {
        Task clone = task.getClone();
        if (!nodesMap.containsKey(task.getId())) {
            this.linkLast(clone);
        } else {
            Node node = nodesMap.get(task.getId());
            this.replaceNode(node, clone);
        }
    }

    @Override
    public void remove(int id) {
        if (nodesMap.containsKey(id)) {
            Node node = nodesMap.get(id);
            this.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return this.getTasks();
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
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
        for (Node x = head; x != null; x = x.next) {
            list.add(x.task);
        }
        return list;
    }

    private void replaceNode(Node x, Task task) {
        if (tail == x) {
            x.task = task;
            return;
        }
        final Node next = x.next;
        final Node prev = x.prev;
        if (next == tail) {
            next.next = x;
            x.prev = next;
            x.next = null;
            tail = x;
        } else {
            Node last = tail;
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

    private void removeNode(Node x) {
        if (x == null) {
            throw new IllegalArgumentException("Node не может быть пустым");
        }
        final Node next = x.next;
        final Node prev = x.prev;
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

    public static class Node {

        private Node next;
        private Node prev;
        private Task task;

        public Node(Node prev, Task data, Node next) {
            this.next = next;
            this.prev = prev;
            this.task = data;
        }
    }
}
