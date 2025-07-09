


import DTO.Task;
import DTO.TaskGroup;
import DTO.TaskType;
import TaskExecutorPackage.CustomExecutor;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CustomExecutor executor = new CustomExecutor(5);

        Future<String> var[] = new Future[10];
//        UUID comm = UUID.randomUUID();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Callable<String> fun = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return "Hi I am done !! "+ finalI;
                }
            };
            Task<String> task = new Task<>(UUID.randomUUID(), new TaskGroup(UUID.randomUUID()), TaskType.READ, fun);
            var[i] = executor.submitTask(task);
        }


        for (int i = 0; i < 10; i++) {
            System.out.println(var[i].get());
        }
        executor.shutDown();
    }

}
