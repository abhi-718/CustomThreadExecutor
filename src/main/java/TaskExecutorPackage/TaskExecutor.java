package TaskExecutorPackage;

import DTO.Task;

import java.util.concurrent.Future;

public interface TaskExecutor<T> {
    /**
     * Submit new task to be queued and executed.
     *
     * @param task Task to be executed by the executor. Must not be null.
     * @return Future for the task asynchronous computation result.
     */
     Future<T> submitTask(Task<T> task);
}