package TaskExecutorPackage;

import DTO.Task;
import DTO.TaskGroup;
import DTO.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class CustomExecutorTest {
    private CustomExecutor<String> customExecutor;

    @BeforeEach
    void setUp() {
        customExecutor = new CustomExecutor<>(4);
    }

    @AfterEach
    void shutDown(){
        customExecutor.shutDown();
    }

    @Test
    public void testFutureResult() throws ExecutionException, InterruptedException {
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hello !!";
            }
        };
        Task<String> task1 = new Task<>(UUID.randomUUID(), new TaskGroup(UUID.randomUUID()), TaskType.READ, task);
        Future<String> future = customExecutor.submitTask(task1);
        assertEquals(future.get(), "Hello !!");
    }

    @Test
    public void testOrderPreserve() throws InterruptedException {
        List<Integer> order = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < 5; i++) {{
            final int val = i;
            Callable<String> task = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    order.add(val);
                    return "Done";
                }
            };
            Future<String> future = customExecutor.submitTask(new Task<String>(UUID.randomUUID(),new TaskGroup(UUID.randomUUID()), TaskType.READ, task));

        }};

        Thread.sleep(1000);
        assertEquals(List.of(0,1,2,3,4), order);

    }

    @Test
    void testNullGroupThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            customExecutor.submitTask(new Task<>(null,null,TaskType.READ, () -> "fail"));
        });
    }


}