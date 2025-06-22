# CustomThreadExecutor

-   Submit tasks concurrently without blocking the caller.
-   Tasks are executed in the background using worker threads.
-   Configurable number of worker threads to limit concurrency.
-   Tasks are executed in the exact order they are submitted.
-   Tasks within the same `TaskGroup` are never run concurrently.
-   Returns a `Future<T>` from submission so result can be retrieved later.

## Assumptions

### 1. Queue Capacity is Fixed
- The internal task queue for each worker thread has a **fixed capacity of 100** tasks.
- This value is currently **hardcoded** in the constructor and not configurable externally.
- If the queue is full, task submission will **fail silently** (logged as a warning) and will **not block** or retry.
- It is assumed that this capacity is **sufficient** for the expected workload or that external systems handle retry/backpressure.

---

### 2. Round-Robin Scheduling Across Threads
- When a new `TaskGroup` is submitted for the **first time**, it is assigned to one of the available threads in **round-robin** fashion.
- This is implemented using a shared counter (`id++`), which wraps around when it reaches the total number of threads.
- It is assumed that this will provide a **fair and even distribution** of unrelated task groups across all threads.
- Once a `TaskGroup` is assigned to a thread, **all future tasks from that group are bound to the same thread** to ensure **sequential execution** within that group.
