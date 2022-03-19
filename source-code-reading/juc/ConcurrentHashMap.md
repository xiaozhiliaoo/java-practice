ConcurrentHashMap:
Java7 Segment，分段锁
Java8 Cas，红黑树，CounterCell 
View:KeySetView,ValuesView,EntrySetView

put,remove  get 并发时候发生什么？
Traversing data structures without locking
Dynamic resizing
weakly consistent 迭代器


 
The primary design goal of this hash table is to maintain
concurrent readability (typically method get(), but also
iterators and related methods) while minimizing update
contention. Secondary goals are to keep space consumption about
the same or better than java.util.HashMap, and to support high
initial insertion rates on an empty table by many threads.


该哈希table的主要设计目标是维护并发可读性（通常是get（）方法，还有迭代器和相关方法），同时最大限度地减少更新
竞争。 次要目标是保持空间消耗与java.util.HashMap相同或更好，并且支持许多线程在空table时候的高初始化插入率。

This map usually acts as a binned (bucketed) hash table.  Each
key-value mapping is held in a Node.  Most nodes are instances
of the basic Node class with hash, key, value, and next
fields. However, various subclasses exist: TreeNodes are
arranged in balanced trees, not lists.  TreeBins hold the roots
of sets of TreeNodes. ForwardingNodes are placed at the heads
of bins during resizing. ReservationNodes are used as
placeholders while establishing values in computeIfAbsent and
related methods.  The types TreeBin, ForwardingNode, and
ReservationNode do not hold normal user keys, values, or
hashes, and are readily distinguishable during search etc
because they have negative hash fields and null key and value
fields. (These special nodes are either uncommon or transient,
so the impact of carrying around some unused fields is
insignificant.)

该map通常作为binned（bucket）的哈希table。 每个Key Value对映射保存在Node中。 大多数节点是Node的实例
，由hash，key，value和next字段组成。 但是存在各种子类：TreeNodes是排列在平衡的树上，而不是list。 
TreeBins存储了TreeNodes的根。 ForwardingNodes放在头部当bin重新调整数量时候。 ReservationNodes用作
在computeIfAbsent和相关方法。 类型TreeBin，ForwardingNode和ReservationNode不包含普通用户键，值或
哈希，并且在搜索等过程中易于区分因为它们具有负的hash字段，空key空value。 （这些特殊节点并不常见或临时的，
因此携带一些未使用的字段的影响是微不足道。）

The table is lazily initialized to a power-of-two size upon the
first insertion.  Each bin in the table normally contains a
list of Nodes (most often, the list has only zero or one Node).
Table accesses require volatile/atomic reads, writes, and
CASes.  Because there is no other way to arrange this without
adding further indirections, we use intrinsics
(sun.misc.Unsafe) operations.

该table在第一次插入时候，大小延迟初始化为2的幂。table中的每一个bin中的通常包含一些Node
（大多数情况下，列table只有零个或一个节点）。table访问必须volatile/atomic读，写和
CAS访问。如果不添加更多间接关系就没办法这么操作，所以我们使用内在函数sun.misc.Unsafe进行操作。


We use the top (sign) bit of Node hash fields for control
purposes -- it is available anyway because of addressing
constraints.  Nodes with negative hash fields are specially
handled or ignored in map methods.

我们使用Node哈希字段的最高（符号）位进行控制目的-由于寻址约束仍然可用。 
具有负哈希字段的节点特别是在map方法中处理或忽略。

Insertion (via put or its variants) of the first node in an
empty bin is performed by just CASing it to the bin.  This is
by far the most common case for put operations under most
key/hash distributions.  Other update operations (insert,
delete, and replace) require locks.  We do not want to waste
the space required to associate a distinct lock object with
each bin, so instead use the first node of a bin list itself as
a lock. Locking support for these locks relies on builtin
"synchronized" monitors.

将第一个节点插入(通过put或其变体)空的bin上仅仅通过CAS到bin上。
这是到目前为止最常见的key/hash分布情况。 其他更新操作（insert，delete，replace）需要获取锁。
 我们不想浪费空间在bin关联的每一个不同的锁对象，因此将使用bin list本身的第一个节点作为
一把锁。对这些锁的锁定支持依赖于内置synchronized监视器。


Using the first node of a list as a lock does not by itself
suffice though: When a node is locked, any update must first
validate that it is still the first node after locking it, and
retry if not. Because new nodes are always appended to lists,
once a node is first in a bin, it remains first until deleted
or the bin becomes invalidated (upon resizing).

将列table的第一个节点用作锁本身不是足够了，当一个节点被锁定后，任何更新都必须先
 确认它仍然是锁定后的第一个节点，并且如果没有就重试。 由于新节点总是append到list中，
 一旦节点成为bin中的第一个节点，它将一直保持到删除或bin无效时候（resizing）。


The main disadvantage of per-bin locks is that other update
operations on other nodes in a bin list protected by the same
lock can stall, for example when user equals() or mapping
functions take a long time.  However, statistically, under
random hash codes, this is not a common problem.  Ideally, the
frequency of nodes in bins follows a Poisson distribution
(http://en.wikipedia.org/wiki/Poisson_distribution) with a
parameter of about 0.5 on average, given the resizing threshold
of 0.75, although with a large variance because of resizing
granularity. Ignoring variance, the expected occurrences of
list size k are (exp(-0.5)  pow(0.5, k) / factorial(k)). The
first values are:

每个bin加锁的主要缺点是被相同锁保护的bin列table当有其他操作在更新时候可能会停止
例如，当用户equals（）或mapping function需要很长时间。 
但是，从统计学上讲，随机哈希码，这不是普遍问题。 理想情况下，
bin中的节点的频率遵循泊松分布均参数约为0.5
（http://en.wikipedia.org/wiki/Poisson_distribution），
给定调整大小阈值0.75，尽管由于调整大小而差异很大粒度。 忽略方差，
预期的发生列table大小k为（exp（-0.5）pow（0.5，k）/阶乘（k））。 的
第一个值是：

0:    0.60653066
1:    0.30326533
2:    0.07581633
3:    0.01263606
4:    0.00157952
5:    0.00015795
6:    0.00001316
7:    0.00000094
8:    0.00000006
more: less than 1 in ten million

Lock contention probability for two threads accessing distinct
elements is roughly 1 / (8  #elements) under random hashes.

Actual hash code distributions encountered in practice
sometimes deviate significantly from uniform randomness.  This
includes the case when N > (1<<30), so some keys MUST collide.
Similarly for dumb or hostile usages in which multiple keys are
designed to have identical hash codes or ones that differs only
in masked-out high bits. So we use a secondary strategy that
applies when the number of nodes in a bin exceeds a
threshold. These TreeBins use a balanced tree to hold nodes (a
specialized form of red-black trees), bounding search time to
O(log N).  Each search step in a TreeBin is at least twice as
slow as in a regular list, but given that N cannot exceed
(1<<64) (before running out of addresses) this bounds search
steps, lock hold times, etc, to reasonable constants (roughly
100 nodes inspected per operation worst case) so long as keys
are Comparable (which is very common -- String, Long, etc).
TreeBin nodes (TreeNodes) also maintain the same "next"
traversal pointers as regular nodes, so can be traversed in
iterators in the same way.

实践中遇到的实际哈希码分布有时会明显偏离统一的随机性。这个
包括N>（1 << 30）的情况，因此某些键必须发生冲突。类似地，对于其中有多个键的愚蠢或恶意使用
设计为具有相同的哈希码或仅具有不同的哈希码在被掩盖的高位中。因此，我们使用次要策略当bin中的节点数超过threshold。
这些TreeBins使用平衡树（红黑树的特殊形式）来保存Node，将搜索时间限制在
O（LogN）。TreeBin中的每个搜索步骤至少两倍于慢于常规列table，但考虑到N不能超过（1 << 64）（在地址用完之前）此边界搜索
步长，锁定保持时间等到合理的常数（大致每个操作检查100个节点（最坏的情况），只要有key是可比较的（这很常见String，Long等）。
TreeBin节点（TreeNodes）也维护相同的“ next”
遍历指针作为常规Node，因此可以遍历迭代器的方式相同。


The table is resized when occupancy exceeds a percentage
threshold (nominally, 0.75, but see below).  Any thread
noticing an overfull bin may assist in resizing after the
initiating thread allocates and sets up the replacement array.
However, rather than stalling, these other threads may proceed
with insertions etc.  The use of TreeBins shields us from the
worst case effects of overfilling while resizes are in
progress.  Resizing proceeds by transferring bins, one by one,
from the table to the next table. However, threads claim small
blocks of indices to transfer (via field transferIndex) before
doing so, reducing contention.  A generation stamp in field
sizeCtl ensures that resizings do not overlap. Because we are
using power-of-two expansion, the elements from each bin must
either stay at same index, or move with a power of two
offset. We eliminate unnecessary node creation by catching
cases where old nodes can be reused because their next fields
won't change.  On average, only about one-sixth of them need
cloning when a table doubles. The nodes they replace will be
garbage collectable as soon as they are no longer referenced by
any reader thread that may be in the midst of concurrently
traversing table.  Upon transfer, the old table bin contains
only a special forwarding node (with hash field "MOVED") that
contains the next table as its key. On encountering a
forwarding node, access and update operations restart, using
the new table.


当table占用率阈值（0.75，但请参见下文）超过百分比时，将resize table的大小。
任何线程注意到bin过满可能有助于调整大小启动线程分配并设置替换数组。
但是，这些其他线程可能会继续执行插入，而不是停滞不前等。
使用TreeBins可以使免受我们正在rezise时，过度填充的最坏情况。
通过逐个转移bin来resize大小，从table到下一个table。
但是，线程执行之前声明很小的indices block要传输（通过transferIndex字段）
这样做，减少了争用。 字段中generation stamp sizeCtl确保resize大小不会重叠
。因为我们是使用2的幂展开，每个bin中的元素必须要么保持相同的索引，要么以2的幂offset
抵消。我们通过捕获来消除不必要的节点创建旧节点由于其下一个字段而可以重用的情况
不会改变。平均而言，只有六分之一需要将table clone一倍。他们替换的节点将是
不再被引用的垃圾可收集。可能同时存在的任何读取器线程遍历table。传输后，旧table包含
只有一个特殊的转发节点（哈希字段为“ MOVED”）包含下一个table作为其键。在遇到一个
转发节点，使用以下命令重新启动访问和更新操作新table。

Each bin transfer requires its bin lock, which can stall
waiting for locks while resizing. However, because other
threads can join in and help resize rather than contend for
locks, average aggregate waits become shorter as resizing
progresses.  The transfer operation must also ensure that all
accessible bins in both the old and new table are usable by any
traversal.  This is arranged in part by proceeding from the
last bin (table.length - 1) up towards the first.  Upon seeing
a forwarding node, traversals (see class Traverser) arrange to
move to the new table without revisiting nodes.  To ensure that
no intervening nodes are skipped even when moved out of order,
a stack (see class TableStack) is created on first encounter of
a forwarding node during a traversal, to maintain its place if
later processing the current table. The need for these
save/restore mechanics is relatively rare, but when one
forwarding node is encountered, typically many more will be.
So Traversers use a simple caching scheme to avoid creating so
many new TableStack nodes. (Thanks to Peter Levart for
suggesting use of a stack here.)

每个bin转移都需要使用其bin锁，该锁可能会失速在resize时等待锁。
但是，因为其他线程可以加入并帮助调整大小，而不是争夺锁，平均总等待时间随着调整大小而变短
进展。转移操作还必须确保所有旧表和新表中的可访问bin可由任何人使用
遍历。这部分是通过从最后一个bin（table.length-1）朝着第一个。通过查看
一个forwarding node ，遍历（请参阅Traverser类）无需重新访问节点即可移至新表。为了保证
即使顺序混乱，也不会跳过中间节点，首次遇到以下内容时会创建一个堆栈（请参见TableStack类）
遍历过程中的转发节点，以保持其位置稍后处理当前表。对这些的需求保存/恢复机制相对较少，但是当
遇到转发节点，通常会更多。所以Traversers使用简单的缓存方案来避免创建许多新的TableStack节点。 
（感谢Peter Levart为建议在这里使用堆栈。）

The traversal scheme also applies to partial traversals of
ranges of bins (via an alternate Traverser constructor)
to support partitioned aggregate operations.  Also, read-only
operations give up if ever forwarded to a null table, which
provides support for shutdown-style clearing, which is also not
currently implemented.

遍历方案也适用于部分遍历bin范围（通过可选的Traverser构造函数）
支持分区聚合操作。 如果转发到空表，则只读操作将放弃该操作
提供shutdown-style clearing，
但是当前还没实现。

Lazy table initialization minimizes footprint until first use,
and also avoids resizings when the first operation is from a
putAll, constructor with map argument, or deserialization.
These cases attempt to override the initial capacity settings,
but harmlessly fail to take effect in cases of races.

延迟表初始化可最大程度地减少占用空间，直到首次使用为止；
并且避免了第一个操作来自putAll，带有map参数的构造函数或反序列化。
这些情况会尝试覆盖capacity设置，在比赛中无害地失效。

The element count is maintained using a specialization of
LongAdder. We need to incorporate a specialization rather than
just use a LongAdder in order to access implicit
contention-sensing that leads to creation of multiple
CounterCells.  The counter mechanics avoid contention on
updates but can encounter cache thrashing if read too
frequently during concurrent access. To avoid reading so often,
resizing under contention is attempted only upon adding to a
bin already holding two or more nodes. Under uniform hash
distributions, the probability of this occurring at threshold
is around 13%, meaning that only about 1 in 8 puts check
threshold (and after resizing, many fewer do so).

元素数量通过使用LongAdder。 我们需要操作规范而不是
只需使用LongAdder即可访问隐式竞态导致创建多个CounterCells。counter机制避免竞态
更新，但如果读取在并发访问期间频繁访问可能会遇到高速缓存崩溃。 
为了避免经常read，仅在添加到bin已经拥有两个或更多节点。 
在统一哈希下分布，此概率在阈值处发生大约是13％，这意味着只有1 in 8 puts check
 threshold（并且在调整大小之后，很少这样做）。

TreeBins use a special form of comparison for search and
related operations (which is the main reason we cannot use
existing collections such as TreeMaps). TreeBins contain
Comparable elements, but may contain others, as well as
elements that are Comparable but not necessarily Comparable for
the same T, so we cannot invoke compareTo among them. To handle
this, the tree is ordered primarily by hash value, then by
Comparable.compareTo order if applicable.  On lookup at a node,
if elements are not comparable or compare as 0 then both left
and right children may need to be searched in the case of tied
hash values. (This corresponds to the full list search that
would be necessary if all elements were non-Comparable and had
tied hashes.) On insertion, to keep a total ordering (or as
close as is required here) across rebalancings, we compare
classes and identityHashCodes as tie-breakers. The red-black
balancing code is updated from pre-jdk-collections
(http://gee.cs.oswego.edu/dl/classes/collections/RBCell.java)
based in turn on Cormen, Leiserson, and Rivest "Introduction to
Algorithms" (CLR). 算法导论

TreeBins使用一种特殊的比较形式进行搜索和相关操作（这是我们无法使用的主要原因
现有集合（例如TreeMaps）。 TreeBins包含可比较的元素，但可能包含其他元素，以及
可比较但不一定可比较的元素相同的T，因此我们无法在其中调用compareTo。处理
这样，树主要是按哈希值排序，然后按Comparable.compareTo订购（如果适用）。在查找节点时，
如果元素不可比较或比较为0，则都剩下如果绑在一起，可能需要对合适的孩子进行搜索
哈希值。 （这对应于如果所有元素都是不可比较的并且具有散列。）插入时，要保持整体排序（或
重新平衡），我们比较类和identityHashCodes作为决胜局。红黑平衡代码从jdk之前的集合中更新
（http://gee.cs.oswego.edu/dl/classes/collections/RBCell.java）
依次基于Cormen，Leiserson和Rivest“算法”（CLR）。


TreeBins also require an additional locking mechanism.  While
list traversal is always possible by readers even during
updates, tree traversal is not, mainly because of tree-rotations
that may change the root node and/or its linkages.  TreeBins
include a simple read-write lock mechanism parasitic on the
main bin-synchronization strategy: Structural adjustments
associated with an insertion or removal are already bin-locked
(and so cannot conflict with other writers) but must wait for
ongoing readers to finish. Since there can be only one such
waiter, we use a simple scheme using a single "waiter" field to
block writers.  However, readers need never block.  If the root
lock is held, they proceed along the slow traversal path (via
next-pointers) until the lock becomes available or the list is
exhausted, whichever comes first. These cases are not fast, but
maximize aggregate expected throughput.

TreeBins还需要其他锁定机制。而读者始终可以遍历在列表更新时候，tree遍历则不行，
主要是因为树旋转可能会更改根节点和/或其链接。 TreeBin 包含一个读写锁策略：当结构调整
与插入或删除相关联的文件已被bin锁定
（因此不能与其他写者发生冲突），但必须等待正在进行的读者来完成。 由于只能有一个这样的
waiter，我们使用一个简单的方案，即使用单个“waiter”字段块作家。 但是，读者永远不需要阻塞。 如果根
保持锁定，它们沿着慢速遍历路径（通过下一个指针），直到锁定可用或列表为
耗尽，以先到者为准。 这些情况不是很快，但是使总预期吞吐量最大化。

Maintaining API and serialization compatibility with previous
versions of this class introduces several oddities. Mainly: We
leave untouched but unused constructor arguments refering to
concurrencyLevel. We accept a loadFactor constructor argument,
but apply it only to initial table capacity (which is the only
time that we can guarantee to honor it.) We also declare an
unused "Segment" class that is instantiated in minimal form
only when serializing.

Maintaining API and serialization compatibility with previous
versions of this class introduces several oddities. Mainly: We
leave untouched but unused constructor arguments refering to
concurrencyLevel. We accept a loadFactor constructor argument,
but apply it only to initial table capacity (which is the only
time that we can guarantee to honor it.) We also declare an
unused "Segment" class that is instantiated in minimal form
only when serializing.

与以前的版本保持API和序列化兼容性
此类的版本引入了几个怪异之处。 主要是：我们保持不变但未使用的构造函数参数引用
concurrencyLevel。 我们接受一个loadFactor构造函数参数，但仅将其应用于初始表容量（这是唯一的
我们可以保证兑现的时间。）我们还宣布了以最小形式实例化的未使用的“Segment”类仅在序列化时。



Also, solely for compatibility with previous versions of this
class, it extends AbstractMap, even though all of its methods
are overridden, so it is just useless baggage.

另外，仅是为了与此版本的先前版本兼容类，它扩展了AbstractMap，即使其所有方法
被覆盖，所以这只是没用的baggage。


This file is organized to make things a little easier to follow
while reading than they might otherwise: First the main static
declarations and utilities, then fields, then main public
methods (with a few factorings of multiple public methods into
internal ones), then sizing methods, trees, traversers, and
bulk operations.

该文件经过整理使更容易理解，在阅读时会比其他方式更重要：
首先是静态声明和实用程序，
然后是字段，然后是主要公众
方法（将多个公共方法的因素纳入内部的），
然后调整大小，树，遍历器和批量操作。


