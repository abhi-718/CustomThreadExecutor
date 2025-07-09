package TaskExecutorPackage;

import DTO.Task;
import DTO.TaskGroup;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class CustomExecutor implements TaskExecutor {

    private final Integer numberOfThreads;
    private final ExecutorService [] executorServices;
    private final ConcurrentHashMap<UUID, Integer> taskToThreadMap;
    private Integer id;

    Logger logger = Logger.getLogger(CustomExecutor.class.getName());

    public CustomExecutor() {
        this.numberOfThreads = 1;
        executorServices = new ExecutorService[1];
        executorServices[0] = Executors.newSingleThreadExecutor();
        taskToThreadMap = new ConcurrentHashMap<>();
        id = 0;
    }

    public CustomExecutor(int n) {
        this.numberOfThreads = n;
        executorServices = new ExecutorService[n];
        for (int i = 0; i < n; i++) {
            executorServices[i] = Executors.newSingleThreadExecutor();
        }
        taskToThreadMap = new ConcurrentHashMap<>();
        id = 0;
    }



    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        TaskGroup taskGp = task.taskGroup();
        Callable<T> callable = task.taskAction();
        FutureTask<T> futureTask = new FutureTask<>(callable);

        int threadId;
        if (taskToThreadMap.containsKey(taskGp.groupUUID())) {
            threadId = taskToThreadMap.get(taskGp.groupUUID());
        } else {
            threadId = id;
            taskToThreadMap.put(taskGp.groupUUID(), threadId);
            id = (id + 1) % numberOfThreads;
        }

        logger.info("Task with Group id " + taskGp.groupUUID() + " is assigned to thread " + threadId);
        executorServices[threadId].submit(futureTask);

        return futureTask;

    }


    public void shutDown() {

        for (ExecutorService executor : executorServices) {
            executor.shutdown();
        }

        for (ExecutorService executor : executorServices) {
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
