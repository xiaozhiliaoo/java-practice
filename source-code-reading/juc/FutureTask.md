WaitNode作用是什么？为什么会有多个WaitNode呢？


Revision notes: This differs from previous versions of this
class that relied on AbstractQueuedSynchronizer, mainly to
avoid surprising users about retaining interrupt status during
cancellation races. Sync control in the current design relies
on a "state" field updated via CAS to track completion, along
with a simple Treiber stack to hold waiting threads.

Style note: As usual, we bypass overhead of using
AtomicXFieldUpdaters and instead directly use Unsafe intrinsics.

修订说明：与本版本的先前版本不同
依赖AbstractQueuedSynchronizer的类，主要用于
避免使用户惊讶地保留中断状态
取消比赛。当前设计中的同步控制依赖
在“状态”字段中通过CAS更新以跟踪完成情况，以及
一个简单的Treiber堆栈来容纳等待线程。

样式说明：与往常一样，我们绕过了使用的开销
AtomicXFieldUpdaters，而是直接使用Unsafe内部函数。



The run state of this task, initially NEW.  The run state
transitions to a terminal state only in methods set,
setException, and cancel.  During completion, state may take on
transient values of COMPLETING (while outcome is being set) or
INTERRUPTING (only while interrupting the runner to satisfy a
cancel(true)). Transitions from these intermediate to final
states use cheaper ordered/lazy writes because values are unique
and cannot be further modified.

此任务的运行状态，最初为NEW。运行状态
仅在方法集中转换为终端状态，
set，setException，cancel。在完成期间，状态可能会发生
瞬态值COMPLETING（设置结果时）或
中断（仅在中断跑步者满足
取消（true））。从这些中间过渡到最终
各州使用便宜的有序/惰性写入，因为值是唯一的
并且无法进一步修改。



Possible state transitions:
NEW -> COMPLETING -> NORMAL
NEW -> COMPLETING -> EXCEPTIONAL
NEW -> CANCELLED
NEW -> INTERRUPTING -> INTERRUPTED
