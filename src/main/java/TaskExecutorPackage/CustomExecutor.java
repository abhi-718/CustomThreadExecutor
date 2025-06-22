package TaskExecutorPackage;

import DTO.Task;
import DTO.TaskGroup;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class CustomExecutor<T> implements TaskExecutor<T> {

    private final Integer numberOfThreads;
    private final Thread [] taskExecutor;
    private final ConcurrentHashMap<UUID, Integer> taskToThreadMap;
    private final BlockingQueue<FutureTask<T>>[] queue;
    private final Integer capacity = 100;
    private Integer id = 0;
    private volatile Boolean running;

    Logger logger = Logger.getLogger(CustomExecutor.class.getName());

    public CustomExecutor() {
        this.numberOfThreads = 1;
        taskExecutor = new Thread[1];
        queue = new BlockingQueue[1];
        for (int i = 0; i < 1; i++) {
            queue[i] = new LinkedBlockingQueue<FutureTask<T>>(capacity);
        }
        this.running = true;
        taskToThreadMap = new ConcurrentHashMap<>();
        start();
    }

    public CustomExecutor(int n) {
        this.numberOfThreads = n;
        taskExecutor = new Thread[n];
        queue = new BlockingQueue[n];
        for (int i = 0; i < n; i++) {
            queue[i] = new LinkedBlockingQueue<FutureTask<T>>();
        }
        this.running = true;
        taskToThreadMap = new ConcurrentHashMap<>();
        start();
    }


    public void start() {
            for (int i = 0; i < numberOfThreads; i++) {
                final int index = i;
                taskExecutor[i] = new Thread(() -> {
                    while (running) {
                        try {
                            FutureTask<T> futureTask =  queue[index].take();
                            futureTask.run();
                        } catch (InterruptedException exception) {
                            if (!running) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                });
                taskExecutor[i].start();
            }
    }


    @Override
    public Future<T> submitTask(Task<T> task) {
        TaskGroup taskGp = task.taskGroup();
        FutureTask<T> futureTask = new FutureTask<>(task.taskAction());
        if (taskToThreadMap.containsKey(taskGp.groupUUID())) {
            int threadId = taskToThreadMap.get(taskGp.groupUUID());
            if (!queue[threadId].offer(futureTask)) {
                logger.warning("Task rejected for thread " + threadId + ": " + task.toString());
            } else {
                logger.info("Task with Group id "+taskGp.groupUUID()+" is running on "+threadId);
            }
        }else {
            if (!queue[id].offer(futureTask)) {
                logger.warning("Task rejected for thread " + id + ": " + task.toString());
            }else {
                taskToThreadMap.put(taskGp.groupUUID(), id);
                logger.info("Task with Group id "+taskGp.groupUUID()+" is running on "+id);
                if (id + 1 == this.numberOfThreads) {
                    id = 0;
                } else {
                    id++;
                }
            }
        }
        return futureTask;

    }


    public void shutDown() {
        running = false;
        for (Thread executor: taskExecutor) {
            executor.interrupt();
        }
    }

}
