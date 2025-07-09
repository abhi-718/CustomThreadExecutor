# CustomThreadExecutor

-   Submit tasks concurrently without blocking the caller.
-   Tasks are executed in the background using worker threads.
-   Configurable number of worker threads to limit concurrency.
-   Tasks are executed in the exact order they are submitted.
-   Tasks within the same `TaskGroup` are never run concurrently.
-   Returns a `Future<T>` from submission so result can be retrieved later.

## Assumptions

1. Tasks can be submitted concurrently and do not block submitter.
2. Tasks must be executed asynchronously and concurrently across groups.
3. Tasks in the same group must not overlap (strictly one at a time).
4. Tasks in a group must execute in FIFO order.
5. Each group will be mapped to a single-thread executor to guarantee order and non-concurrent execution.
6. Number of TaskGroups is limited or acceptable to handle using separate executors.
7. System resources (threads) are sufficient for the number of executors created.
8. Futures are used to retrieve results, and callers handle blocking if needed.
9. No explicit external blocking queue is required since executor internal queues are sufficient.
10. Task logic is self-contained and thread-safe across groups.
11. Shutdown will be explicitly called by the application lifecycle.
12. Proper resource cleanup and termination are expected during shutdown.
