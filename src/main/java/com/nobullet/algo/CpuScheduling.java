package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class CpuScheduling {

  public static void main(String... args) {
    test(Arrays.asList(new Task("id2", 3, 7), new Task("id1", 0, 10),
        new Task("id4", 12, 5), new Task("id3", 6, 3)));

    test(Arrays.asList(new Task("id2", 300, 7), new Task("id1", 0, 10),
        new Task("id4", 120, 5), new Task("id3", 6, 3)));
  }

  private static void test(List<Task> tasks) {
    System.out.println(inExecutionOrder(tasks).toString());
    System.out.println("");
  }

  public static List<Task> inExecutionOrder(List<Task> source) {
    if (source.isEmpty()) {
      return source;
    }

    List<Task> sorted = new ArrayList<>(source);
    Collections.sort(sorted, (t1, t2) -> Long.compare(t1.queuedInTime, t2.queuedInTime));

    List<Task> result = new ArrayList<>(source.size());
    PriorityQueue<Task> queue = new PriorityQueue<>((t1, t2) -> Long.compare(t1.executionTime, t2.executionTime));

    long currentTime = sorted.stream().findFirst().get().queuedInTime; // Start with the 1st queuedInTime.
    int sortedIndex = 0;

    while (true) {
      while (sortedIndex < sorted.size() && sorted.get(sortedIndex).queuedInTime <= currentTime) {
        queue.add(sorted.get(sortedIndex));
        sortedIndex++;
      }
      if (queue.isEmpty()) {
        if (sortedIndex >= sorted.size()) {
          break;
        }
        // There are remaining items, we need to advance the time:
        currentTime = sorted.get(sortedIndex).queuedInTime;
        continue;
      }
      Task next = queue.poll();
      currentTime += next.executionTime;
      result.add(next);
    }

    return result;
  }

  static final class Task {
    String id;
    long queuedInTime;
    long executionTime;

    public Task(String id,
                long queuedInTime,
                long executionTime) {
      this.id = id;
      this.queuedInTime = queuedInTime;
      this.executionTime = executionTime;
    }

    @Override
    public String toString() {
      return "{'" + id + "': " + queuedInTime + " / " + executionTime + "}";
    }
  }
}
