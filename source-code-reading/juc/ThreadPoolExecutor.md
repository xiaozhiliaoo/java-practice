钩子函数，
protected void beforeExecute(Thread t, Runnable r) { }
protected void afterExecute(Runnable r, Throwable t) { }
protected void terminated() { }


Worker抽象： Thread thread  &&  Runnable firstTask;
Worker extends AbstractQueuedSynchronizer implements Runnable

核心方法：
addWorker


API

prestartCoreThread 启动一个核心线程
prestartAllCoreThreads  启动所有核心线程
setKeepAliveTime
allowCoreThreadTimeOut：设置了，超过了KeepAliveTime时间后，worker去哪里了？core去哪里了？是怎么terminated  thread的？
remove
purge:任务排队时候，取消了，这时候并不会在队列取消，只是worker执行时候才取消
finalize

疑惑：
当超过核心线程数时候，任务排队，那么worker是怎么从任务队列拿到任务的呢？Worker的runWoker方法Main worker run loop




 The main pool control state, ctl, is an atomic integer packing
 two conceptual fields
   workerCount, indicating the effective number of threads
   runState,    indicating whether running, shutting down etc

主池控制状态，ctl是原子整数包装两个概念领域
workerCount：标示有效线程数
runState：标示是否运行，关闭
 
 
 In order to pack them into one int, we limit workerCount to
 (2^29)-1 (about 500 million) threads rather than (2^31)-1 (2
 billion) otherwise representable. If this is ever an issue in
 the future, the variable can be changed to be an AtomicLong,
 and the shift/mask constants below adjusted. But until the need
 arises, this code is a bit faster and simpler using an int.
 
 
 为了将它们打包为一个int，我们将workerCount限制为
(2^29)-1个线程（5亿），而不是(2^31)-1 （20亿）。 如果这是一个问题
将来，该变量可以更改为AtomicLong，
并调整以下的shift/mask常数。 但是直到需要
出现时，使用int可以使此代码更快，更简单。

 The workerCount is the number of workers that have been
 permitted to start and not permitted to stop.  The value may be
 transiently different from the actual number of live threads,
 for example when a ThreadFactory fails to create a thread when
 asked, and when exiting threads are still performing
 bookkeeping before terminating. The user-visible pool size is
 reported as the current size of the workers set.
 
 
workerCount是已经工作的worker允许启动但不允许停止。 该值可能是
与活动线程的实际数量暂时不同，例如，当ThreadFactory无法创建线程时，
询问，并且退出线程仍在执行时终止之前记账。 用户可见的池大小为
报告为当前设定的工人人数。

 The runState provides the main lifecycle control, taking on values:

   RUNNING:  Accept new tasks and process queued tasks
   SHUTDOWN: Don't accept new tasks, but process queued tasks
   STOP:     Don't accept new tasks, don't process queued tasks,
             and interrupt in-progress tasks
   TIDYING:  All tasks have terminated, workerCount is zero,
             the thread transitioning to state TIDYING
             will run the terminated() hook method
   TERMINATED: terminated() has completed
   
运行：接受新任务并处理排队的任务
关闭：不接受新任务，但处理排队的任务
停止：不接受新任务，不处理排队的任务，
         并中断正在进行的任务
整理：所有任务已终止，workerCount为零，线程过渡到状态TIDYING将运行终止的（）钩子方法
终止：terminated（）已完成


 The numerical order among these values matters, to allow
 ordered comparisons. The runState monotonically increases over
 time, but need not hit each state. The transitions are:

这些值之间的数字顺序很重要，以允许有序比较。runState单调递增
 时间，但不必击中每个状态。 过渡是：

 RUNNING -> SHUTDOWN
    On invocation of shutdown(), perhaps implicitly in finalize()
 (RUNNING or SHUTDOWN) -> STOP
    On invocation of shutdownNow()
 SHUTDOWN -> TIDYING
    When both queue and pool are empty
 STOP -> TIDYING
    When pool is empty
 TIDYING -> TERMINATED
    When the terminated() hook method has completed

 Threads waiting in awaitTermination() will return when the
 state reaches TERMINATED.

 Detecting the transition from SHUTDOWN to TIDYING is less
 straightforward than you'd like because the queue may become
 empty after non-empty and vice versa during SHUTDOWN state, but
 we can only terminate if, after seeing that it is empty, we see
 that workerCount is 0 (which sometimes entails a recheck -- see
 below).

检测从SHUTDOWN到TIDYING的过渡要少比您想要的简单，因为队列可能变得
在非空状态后为空，反之亦然；我们只有在看到它为空之后才能终止
该workerCount为0（有时需要重新检查-请参阅下面）。


    
The queue used for holding tasks and handing off to worker
threads.  We do not require that workQueue.poll() returning
null necessarily means that workQueue.isEmpty(), so rely
solely on isEmpty to see if the queue is empty (which we must
do for example when deciding whether to transition from
SHUTDOWN to TIDYING).  This accommodates special-purpose
queues such as DelayQueues for which poll() is allowed to
return null even if it may later return non-null when delays
expire.

用于保留任务并移交给工作人员的队列线程。 我们不要求workQueue.poll（）返回
null必然意味着workQueue.isEmpty（），因此请依赖仅在isEmpty上查看队列是否为空（我们必须
例如在决定是否从关闭并整理）。 这可容纳特殊用途允许poll（）执行以下操作的队列，例如DelayQueues
即使在延迟后稍后可能返回非null，也返回null到期。



Lock held on access to workers set and related bookkeeping.
While we could use a concurrent set of some sort, it turns out
to be generally preferable to use a lock. Among the reasons is
that this serializes interruptIdleWorkers, which avoids
unnecessary interrupt storms, especially during shutdown.
Otherwise exiting threads would concurrently interrupt those
that have not yet interrupted. It also simplifies some of the
associated statistics bookkeeping of largestPoolSize etc. We
also hold mainLock on shutdown and shutdownNow, for the sake of
ensuring workers set is stable while separately checking
permission to interrupt and actually interrupting.

锁定时要锁定工人位置和相关簿记。
虽然我们可以使用某种并发集，但事实证明
通常最好使用锁。 原因之一是
序列化interruptIdleWorkers，避免了
不必要的中断风暴，尤其是在关机期间。
否则退出线程会同时中断那些线程
尚未中断。 这也简化了一些
最大的PoolSize等的相关统计簿记。
为了保持关闭状态，也要在关闭和shutdownNow时保持mainLock
确保工人在单独检查时稳定下来
允许中断和实际中断。



All user control parameters are declared as volatiles so that
ongoing actions are based on freshest values, but without need
for locking, since no internal invariants depend on them
changing synchronously with respect to other actions.

所有用户控制参数都声明为volatile，因此
持续的行动基于最新的价值，但没有必要
用于锁定，因为没有内部不变式依赖于它们
关于其他动作同步变化。



Factory for new threads. All threads are created using this
factory (via method addWorker).  All callers must be prepared
for addWorker to fail, which may reflect a system or user's
policy limiting the number of threads.  Even though it is not
treated as an error, failure to create threads may result in
new tasks being rejected or existing ones remaining stuck in
the queue.

We go further and preserve pool invariants even in the face of
errors such as OutOfMemoryError, that might be thrown while
trying to create threads.  Such errors are rather common due to
the need to allocate a native stack in Thread.start, and users
will want to perform clean pool shutdown to clean up.  There
will likely be enough memory available for the cleanup code to
complete without encountering yet another OutOfMemoryError.

新线程的工厂。 所有线程都使用此创建工厂（通过方法addWorker）。 
所有来电者必须做好准备使addWorker失败，这可能反映了系统或用户的
限制线程数的策略。 即使不是被视为错误，创建线程失败可能会导致
新任务被拒绝或现有任务被卡在其中队列。

我们走得更远，即使面对错误，例如OutOfMemoryError，可能会在
尝试创建线程。 由于需要在Thread.start和用户中分配本机堆栈
将要执行干净池关闭以进行清理。 那里可能有足够的内存供清理代码使用
完成而不会遇到另一个OutOfMemoryError。


Permission required for callers of shutdown and shutdownNow.
We additionally require (see checkShutdownAccess) that callers
have permission to actually interrupt threads in the worker set
(as governed by Thread.interrupt, which relies on
ThreadGroup.checkAccess, which in turn relies on
SecurityManager.checkAccess). Shutdowns are attempted only if
these checks pass.

All actual invocations of Thread.interrupt (see
interruptIdleWorkers and interruptWorkers) ignore
SecurityExceptions, meaning that the attempted interrupts
silently fail. In the case of shutdown, they should not fail
unless the SecurityManager has inconsistent policies, sometimes
allowing access to a thread and sometimes not. In such cases,
failure to actually interrupt threads may disable or delay full
termination. Other uses of interruptIdleWorkers are advisory,
and failure to actually interrupt will merely delay response to
configuration changes so is not handled exceptionally.


调用shutdown和shutdownNow所需的权限。
我们还要求（请参阅checkShutdownAccess）调用者
有权实际中断工作程序集中的线程
（由Thread.interrupt所控制，该依赖于
ThreadGroup.checkAccess，这又依赖于
SecurityManager.checkAccess）。 仅在以下情况下尝试关机
这些检查通过了。

Thread.interrupt的所有实际调用（请参阅
interruptIdleWorkers和interruptWorkers）忽略
SecurityExceptions，表示尝试中断
默默地失败。 在关机的情况下，它们不应失败
除非SecurityManager的策略不一致，否则有时
允许访问线程，有时不允许访问。 在这种情况下
未能实际中断线程可能会禁用或延迟完整线程
终止。 interruptIdleWorkers的其他用途是建议性的，
实际中断失败只会延迟对
配置更改，因此不会进行异常处理。



Class Worker mainly maintains interrupt control state for
threads running tasks, along with other minor bookkeeping.
This class opportunistically extends AbstractQueuedSynchronizer
to simplify acquiring and releasing a lock surrounding each
task execution.  This protects against interrupts that are
intended to wake up a worker thread waiting for a task from
instead interrupting a task being run.  We implement a simple
non-reentrant mutual exclusion lock rather than use
ReentrantLock because we do not want worker tasks to be able to
reacquire the lock when they invoke pool control methods like
setCorePoolSize.  Additionally, to suppress interrupts until
the thread actually starts running tasks, we initialize lock
state to a negative value, and clear it upon start (in
runWorker).





  Proceed in 3 steps:
 
  1. If fewer than corePoolSize threads are running, try to
  start a new thread with the given command as its first
  task.  The call to addWorker atomically checks runState and
  workerCount, and so prevents false alarms that would add
  threads when it shouldn't, by returning false.
 
  2. If a task can be successfully queued, then we still need
  to double-check whether we should have added a thread
  (because existing ones died since last checking) or that
  the pool shut down since entry into this method. So we
  recheck state and if necessary roll back the enqueuing if
  stopped, or start a new thread if there are none.
 
  3. If we cannot queue task, then we try to add a new
  thread.  If it fails, we know we are shut down or saturated
  and so reject the task.

1.如果正在运行的线程少于corePoolSize线程，请尝试执行以下操作：
   使用给定的命令作为第一个启动新线程
   任务。 对addWorker的调用自动检查runState和
   workerCount，这样可以防止假警报增加
   通过返回false返回不应该执行的线程。
 
   2.如果任务可以成功排队，那么我们仍然需要
   仔细检查我们是否应该添加线程
   （因为现有的自上次检查后死亡）或
   自进入此方法以来，该池已关闭。 所以我们
   重新检查状态，并在必要时回退排队
   停止，如果没有，则启动一个新线程。
 
   3.如果我们无法将任务排队，那么我们尝试添加一个新的
   线。 如果失败，我们知道我们已经关闭或饱和
   因此拒绝任务。
   


  Checks if a new worker can be added with respect to current
  pool state and the given bound (either core or maximum). If so,
  the worker count is adjusted accordingly, and, if possible, a
  new worker is created and started, running firstTask as its
  first task. This method returns false if the pool is stopped or
  eligible to shut down. It also returns false if the thread
  factory fails to create a thread when asked.  If the thread
  creation fails, either due to the thread factory returning
  null, or due to an exception (typically OutOfMemoryError in
  Thread.start()), we roll back cleanly.
  
检查是否可以添加新的worker
池状态和给定的界限（核心或最大值）。 如果是这样，
相应地调整了工人人数，如果可能的话，
创建并启动新工作程序，并运行firstTask作为其
第一项任务。 如果池已停止，则此方法返回false。
有资格关闭。 如果线程也返回false
询问时工厂无法创建线程。 如果线程
创建失败，可能是由于线程工厂返回了
null，或由于异常（通常在
Thread.start（）），我们干净地回滚。
 
  @param firstTask the task the new thread should run first (or
  null if none). Workers are created with an initial first task
  (in method execute()) to bypass queuing when there are fewer
  than corePoolSize threads (in which case we always start one),
  or when the queue is full (in which case we must bypass queue).
  Initially idle threads are usually created via
  prestartCoreThread or to replace other dying workers.
 
  @param core if true use corePoolSize as bound, else
  maximumPoolSize. (A boolean indicator is used here rather than a
  value to ensure reads of fresh values after checking other pool
  state).
  @return true if successful
 








