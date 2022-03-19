-- protect final 定义同步器的状态含义。
protected final int getState()  
protected final void setState(int newState)  
protected final boolean compareAndSetState(int expect, int update)  只为了子类使用，而不是覆写


-- protect 实现类肯定是tryXXX 只有5个protected，这几个方法是研究的起点，
protected boolean tryAcquire(int arg) true 自己获得锁，会阻塞别的线程
protected boolean tryRelease(int arg) 
protected boolean isHeldExclusively()   //独占
protected int tryAcquireShared(int arg)
protected boolean tryReleaseShared(int arg) //共享 
为了让子类覆写的类

-- public final
public final void acquire(int arg)
public final void acquireInterruptibly(int arg)
public final void acquireShared(int arg) 
public final void acquireSharedInterruptibly(int arg)
public final boolean release(int arg)
public final boolean releaseShared(int arg)
public final Collection<Thread> getWaitingThreads(ConditionObject condition)
public final int getWaitQueueLength(ConditionObject condition) 
public final boolean hasContended() 
public final boolean tryAcquireNanos(int arg, long nanosTimeout)
public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)


synchronized关键字和AQS,AOS

exclusive synchronization takes the form:

 Acquire:
     while (!tryAcquire(arg)) {
        enqueue thread if it is not already queued;
        possibly block current thread;
     }

 Release:
     if (tryRelease(arg))
        unblock the first queued thread;
        


















Provides a framework for implementing blocking locks and related synchronizers (semaphores, events, etc) that 
rely on first-in-first-out (FIFO) wait queues. This class is designed to be a useful basis for most kinds of 
synchronizers that rely on a single atomic int value to represent state. Subclasses must define the protected 
methods that change this state, and which define what that state means in terms of this object being acquired or 
released. Given these, the other methods in this class carry out all queuing and blocking mechanics. Subclasses 
can maintain other state fields, but only the atomically updated int value manipulated using methods getState(), 
setState(int) and compareAndSetState(int, int) is tracked with respect to synchronization.




  
  
  
  
  
  
  
  
  Wait queue node class.
  
  等待队列节点类。
 
  The wait queue is a variant of a "CLH" (Craig, Landin, and
Hagersten) lock queue. CLH locks are normally used for
spinlocks.  We instead use them for blocking synchronizers, but
use the same basic tactic of holding some of the control
information about a thread in the predecessor of its node.  A
"status" field in each node keeps track of whether a thread
should block.  A node is signalled when its predecessor
releases.  Each node of the queue otherwise serves as a
specific-notification-style monitor holding a single waiting
thread. The status field does NOT control whether threads are
granted locks etc though.  A thread may try to acquire if it is
first in the queue. But being first does not guarantee success;
it only gives the right to contend.  So the currently released
contender thread may need to rewait.

  To enqueue into a CLH lock, you atomically splice it in as new
tail. To dequeue, you just set the head field.
  
等待队列是“ CLH”（Craig，Landin和Hagersten）锁定队列。 CLH锁通常用于自旋锁。 相反，我们使用它们来阻塞同步器，但是
使用持有某些控件的相同基本策略有关其节点的前身中的线程的信息。 每个节点中的“status”字段跟踪线程是否
应该阻塞。 节点收到信号当节点前驱节点释放。 队列中的每个节点充当特定通知样式的管程，仅等待一个线程。 状态字段不控制线程是否
虽然获得锁等。 线程可能会尝试获取是否首先在队列中。 但是先行并不能保证成功。它只赋予竞争权。 所以目前释放竞争者线程可能需要重新等待。
   
 要入队CLH锁，您可以自动将其自动拼接尾巴。 要出队，您只需设置头字段。
  
  <pre>
       +------+  prev +-----+       +-----+
  head |      | <---- |     | <---- |     |  tail
       +------+       +-----+       +-----+
  </pre>
 
   Insertion into a CLH queue requires only a single atomic
operation on "tail", so there is a simple atomic point of
demarcation from unqueued to queued. Similarly, dequeuing
involves only updating the "head". However, it takes a bit
more work for nodes to determine who their successors are,
in part to deal with possible cancellation due to timeouts
and interrupts.
  
  插入到CLH队列中只需要一个原子对“tail”进行操作，因此有一个简单的原子点
从未排队到排队的分界。 同样，出队仅涉及更新“头部”。 但是，这需要一点时间
节点需要做更多工作来确定其后继者，部分处理由于超时而可能取消的问题
和中断。


 
   The "prev" links (not used in original CLH locks), are mainly
needed to handle cancellation. If a node is cancelled, its
successor is (normally) relinked to a non-cancelled
predecessor. For explanation of similar mechanics in the case
of spin locks, see the papers by Scott and Scherer at
http://www.cs.rochester.edu/u/scott/synchronization/
  
  
   prev链接（未在原始CLH锁中使用）主要是需要处理取消。 如果取消某个节点，则其
后继者（通常）重新链接到未取消前继者。 对于该案例中类似机制的解释自旋锁，请参见
Scott和Scherer的论文，网址为http://www.cs.rochester.edu/u/scott/synchronization/
 
   We also use "next" links to implement blocking mechanics.
The thread id for each node is kept in its own node, so a
predecessor signals the next node to wake up by traversing
next link to determine which thread it is.  Determination of
successor must avoid races with newly queued nodes to set
the "next" fields of their predecessors.  This is solved
when necessary by checking backwards from the atomically
updated "tail" when a node's successor appears to be null.
(Or, said differently, the next-links are an optimization
so that we don't usually need a backward scan.)

我们还使用“next”链接来实现阻塞机制。每个节点的线程ID保留在其自己的节点中，因此
前驱者通过遍历通知下一个节点唤醒下一个链接以确定它是哪个线程。 决定后继者必须避免与
新排队的节点进行竞争其前身的“下一个”字段。 解决了必要时通过从原子上向后检查
当节点的后继者似乎为空时，更新“tail”。（或者换句话说，下一个链接是一种优化
因此我们通常不需要向后扫描。）



Cancellation introduces some conservatism to the basic
algorithms.  Since we must poll for cancellation of other
nodes, we can miss noticing whether a cancelled node is
ahead or behind us. This is dealt with by always unparking
successors upon cancellation, allowing them to stabilize on
a new predecessor, unless we can identify an uncancelled
predecessor who will carry this responsibility.

取消将一些保守性引入基本算法。 由于我们必须轮询其他取消节点，我们可能会错过注意到被取消的节点是否为
在我们前面还是后面。 这可以通过始终unparking前驱来解决
取消后，使他们能够稳定下来一个新的前驱，除非我们能找到一个未取消的人
前任将承担这项责任。

CLH queues need a dummy header node to get started. But
we don't create them on construction, because it would be wasted
effort if there is never contention. Instead, the node
is constructed and head and tail pointers are set upon first
contention.

CLH队列需要一个虚拟头节点才能开始。 但我们不会在构造函数中创建它们，因为这将被浪费
如果没有竞争， 相反，节点构造，并且首先设置头和尾指针竞争。

Threads waiting on Conditions use the same nodes, but
use an additional link. Conditions only need to link nodes
in simple (non-concurrent) linked queues because they are
only accessed when exclusively held.  Upon await, a node is
inserted into a condition queue.  Upon signal, the node is
transferred to the main queue.  A special value of status
field is used to mark which queue a node is on.

等待条件的线程使用相同的节点，但是
使用其他链接。 条件只需要链接节点
在简单（非并发）链接队列中，因为它们是
仅在专用时访问。 等待时，一个节点是
插入条件队列中。 收到信号后，该节点为
转移到主队列。 地位的特殊价值
字段用于标记节点所在的队列。

Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill
Scherer and Michael Scott, along with members of JSR-166
expert group, for helpful ideas, discussions, and critiques
on the design of this class.
