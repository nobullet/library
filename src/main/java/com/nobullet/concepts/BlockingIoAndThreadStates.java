package com.nobullet.concepts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BlockingIoAndThreadStates {

  private static volatile long startTime = 0L;
  private static volatile long endTime = 0L;

  static void measureContextSwitchTime() {

    Object theLock = new Object();
    synchronized (theLock) {
      Thread task = new TheTask(theLock);
      task.start();
      try {
        theLock.wait();
        endTime = System.nanoTime();
      } catch (InterruptedException e) {
        // do something if interrupted
      }
    }
    System.out.println("Context Switch Time elapsed: " + (endTime - startTime) + " nano seconds");
  }

  public static void main(String... args) throws Exception {
    if (false) {
      measureContextSwitchTime();
      // 37977 nano seconds
      // 158734
      // 45270
      // 39157
      return;
    }


    ExecutorService executorService = Executors.newFixedThreadPool(500);

    //sendGET("https://janescat.com/sso?id=0");

    if (true) {
      for (int i = 0; i < 100; i++) {
        scheduleGet(executorService, i);
        //scheduleFiles(executorService, i);
        if (i % 10 == 0) {
          //  System.out.println(generateThreadDump());
          //  System.out.println("============-============");
        }
      }

      //Thread.sleep(1);
      //System.out.println("============-============");
      //System.out.println(generateThreadDump());
      //Thread.sleep(300);
      //System.out.println("============-============");
      //System.out.println(generateThreadDump());
    }


    Thread.sleep(10000);
    executorService.shutdown();
  }

  private static void scheduleGet(ExecutorService executorService, int id) {
    Future<?> future = executorService.submit(() -> {
      //sendGET("https://janescat.com/sso?id=" + id);
      sendGET("https://rbm.goog/bot?id=telcel-adelanta-megas-rcs@rbm.goog&v=1.5&ho=334020&hl=es&_id=" + id);
      sendGET("https://rbm.goog/bot?id=telcel@rbm.goog&v=1.5&ho=334020&hl=es&_id=" + id);
    });

    System.out.println("FUTURE CLASS: " + future.getClass().getName());
  }

  private static void scheduleFiles(ExecutorService executorService, int id) {
    executorService.submit(() -> {
      writeAndReadFile(id);
    });
  }

  private static void writeAndReadFile(int id) {
    String file = "experiment_delete_me_" + id + ".txt";
    try (FileWriter writer = new FileWriter(file, false)) {
      BufferedWriter bufferedWriter = new BufferedWriter(writer);
      bufferedWriter.write("Hello World: " + id);
      bufferedWriter.newLine();
      bufferedWriter.write("See You Again!");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }


    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      int character;

      int i = 0;
      while ((character = reader.read()) != -1) {
        i++;
        //System.out.print((char) character);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.print("X");
    try {
      Files.delete(Paths.get(file));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void sendGET(String url) {
    System.out.println("Sending GET request to " + url);
    HttpURLConnection con = null;
    long now = System.nanoTime();
    try {
      URL obj = new URL(url);

      con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", "java");
      con.setReadTimeout(0);
      con.setConnectTimeout(0);
      int responseCode = con.getResponseCode();
      //System.out.println("GET Response Code :: " + responseCode);
      if (responseCode == HttpURLConnection.HTTP_OK) { // success
        BufferedReader in = new BufferedReader(new InputStreamReader(
            con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        // print result
        long endTime = System.nanoTime();
        System.out.println("Response: [" + ((endTime - now) / 1000000.0D) + "  ms, size: "  + response.length() + " b  ]   " + response.toString());
      } else {
        System.out.println("GET request not worked");
      }

    } catch (IOException ioe) {
      System.out.println("Error: " + ioe);
      if (con != null) {
        try {
          con.disconnect();
        } catch (Exception e) {
        }
      }
    }
  }

  public static String generateThreadDump() {
    final StringBuilder dump = new StringBuilder();
    final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
    for (ThreadInfo threadInfo : threadInfos) {
      dump.append('"');
      dump.append(threadInfo.getThreadName());
      dump.append("\" ");
      //dump.append("DAEMON: " + threadInfo.isDaemon() + " ");
      dump.append(" IS_IN_NATIVE: " + threadInfo.isInNative() + " ");
      dump.append(" ");

      final Thread.State state = threadInfo.getThreadState();
      dump.append("\n   java.lang.Thread.State: ");
      dump.append(state);
      final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
      for (final StackTraceElement stackTraceElement : stackTraceElements) {
        dump.append("\n        at ");
        dump.append(stackTraceElement);
      }
      dump.append("\n\n");
    }
    return dump.toString();
  }

  static class TheTask extends Thread {
    private Object theLock;

    public TheTask(Object theLock) {
      this.theLock = theLock;
    }

    public void run() {
      synchronized (theLock) {
        startTime = System.nanoTime();
        theLock.notify();
      }
    }
  }
}

