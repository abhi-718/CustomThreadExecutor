# CustomThreadExecutor

-   Submit tasks concurrently without blocking the caller.
-   Tasks are executed in the background using worker threads.
-   Configurable number of worker threads to limit concurrency.
-   Tasks are executed in the exact order they are submitted.
-   Tasks within the same `TaskGroup` are never run concurrently.
-   Returns a `Future<T>` from submission so result can be retrieved later.