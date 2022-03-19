Basic thread blocking primitives for creating locks and other synchronization classes.
This class associates, with each thread that uses it, a permit (in the sense of the Semaphore class). 
A call to park will return immediately if the permit is available, consuming it in the process; otherwise it may block. 
A call to unpark makes the permit available, if it was not already available. (Unlike with Semaphores though, permits do
 not accumulate. There is at most one.) Reliable usage requires the use of volatile (or atomic) variables to control when 
 to park or unpark. Orderings of calls to these methods are maintained with respect to volatile variable accesses, but not 
 necessarily non-volatile variable accesses.


用于创建锁和其他同步类的基本线程阻塞原语。
此类与使用它的每个线程关联一个许可（就Semaphore类而言）。
如果有许可证，将立即返回停车请求，并在此过程中消耗掉它； 否则可能会阻塞。
取消停车的调用使许可证可用（如果尚不可用）。 （尽管与信号量不同，许可证可以
  不积累。 最多有一个。）可靠的使用要求使用易失性（或原子）变量来控制何时
  停放。 相对于易失变量访问，保留了对这些方法的调用顺序，但没有
  必然是非易失性变量访问。
  
  

Methods park and unpark provide efficient means of blocking and unblocking threads that do not encounter the problems that
 cause the deprecated methods Thread.suspend and Thread.resume to be unusable for such purposes: Races between one thread 
 invoking park and another thread trying to unpark it will preserve liveness, due to the permit. Additionally, park will 
 return if the caller's thread was interrupted, and timeout versions are supported. The park method may also return at any
  other time, for "no reason", so in general must be invoked within a loop that rechecks conditions upon return. In this 
  sense park serves as an optimization of a "busy wait" that does not waste as much time spinning, but must be paired with 
  an unpark to be effective.

停放和取消停放方法提供了阻塞和取消阻塞未遇到以下问题的线程的有效方法
  导致不赞成使用的方法Thread.suspend和Thread.resume不能用于以下目的：在一个线程之间进行竞争
  由于许可，调用停放和试图取消停放的另一个线程将保留生命。 此外，公园将
  如果调用者的线程被中断，并且支持超时版本，则返回。 park方法也可以在任何时候返回
   其他时间，由于“没有理由”，因此通常必须在循环中调用该循环，以在返回时重新检查条件。 在这个
   感觉公园是对“繁忙等待”的优化，它不会浪费太多时间，而必须与
   取消停车才能生效。
   




The three forms of park each also support a blocker object parameter. This object is recorded while the thread is blocked 
to permit monitoring and diagnostic tools to identify the reasons that threads are blocked. (Such tools may access blockers 
using method getBlocker(Thread).) The use of these forms rather than the original forms without this parameter is strongly 
encouraged. The normal argument to supply as a blocker within a lock implementation is this.

停放的三种形式也都支持阻塞对象参数。 线程被阻止时记录该对象
允许监视和诊断工具确定线程被阻塞的原因。 （此类工具可能会访问阻止程序
使用方法getBlocker（Thread）。）强烈建议使用这些形式，而不使用没有此参数的原始形式
鼓励。 在锁定实现中提供作为阻止程序的正常参数是这个。



These methods are designed to be used as tools for creating higher-level synchronization utilities, and are not in 
themselves useful for most concurrency control applications. The park method is designed for use only in constructions of the form:

 
 while (!canProceed()) {
   // ensure request to unpark is visible to other threads
   ...
   LockSupport.park(this);
 }
where no actions by the thread publishing a request to unpark, prior to the call to park, entail locking or blocking. 
Because only one permit is associated with each thread, any intermediary uses of park, including implicitly via class 
loading, could lead to an unresponsive thread (a "lost unpark").
Sample Usage. Here is a sketch of a first-in-first-out non-reentrant lock class:

如果线程在调用停放之前没有任何操作发布释放停放的请求，则需要进行锁定或阻塞。
因为每个线程仅关联一个许可证，所以park的任何中间用途，包括隐式地通过类
加载，可能导致线程无响应（“丢失的停放状态”）。
样本用法。 我在这