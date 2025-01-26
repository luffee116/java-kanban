package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> receivedHistory;

    public InMemoryHistoryManager() {
        this.receivedHistory = new HashMap<>();
    }

    private static class Node<E> {

        public E data;
        public Node<E>  next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(receivedHistory.get(id));
        receivedHistory.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> taskNode = new Node<>(oldTail, task, null);
        tail = taskNode;
        receivedHistory.put(task.getId(), taskNode);
        if (oldTail == null) {
            head = taskNode;
        } else {
            oldTail.next = taskNode;
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> taskToDelete) {
       if (taskToDelete != null) {
           taskToDelete.data = null;

           if (head == taskToDelete && tail == taskToDelete) {
               head = null;
               tail = null;
           } else if (head == taskToDelete) {
               head = taskToDelete.next;
               head.prev = null;
           } else if (tail == taskToDelete) {
               tail = taskToDelete.prev;
               tail.next = null;
           } else {
               taskToDelete.prev.next = taskToDelete.next;
               taskToDelete.next.prev = taskToDelete.prev;
           }

       }
    }
}
