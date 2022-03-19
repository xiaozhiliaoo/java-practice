1 HashMap结构上实现了Map接口，继承了AbstractMap，同时实现了Cloneable,Serializable接口。相关接口还有
2 Map接口定义了HashMap的基本操作，有查询，size(),get(K),contains(K), 修改，put(K,V),putAll(K,V)，
  Map接口同时定义了内部接口Entry代表KV对。
3 AbstractMap是Map接口的最小实现，里面实现了通用Map的equals，toString,hashCode方法，使得HashMap不需要实现这些方法。通过AbstractMap，
  可以实现可变的Map和不可变的Map。
4 HashMap内部结构分为四个部分：
    1 Entry对象：Node(包级可见的静态成员类)
                TreeNode(包级可见终结的静态成员类) (find(),rotateLeft(),rotateRight(),balanceInsertion(),balanceDeletion(),)
    2 迭代器：HashIterator作为抽象类，有KeyIterator(包级可见终结的成员类)，ValueIterator(包级可见终结的成员类)，EntryIterator(包级可见终结的成员类)  
    3 分割器：HashMapSpliterator作为静态类，有KeySpliterator，ValueSpliterator，EntrySpliterator
       数据访问器：1 迭代器把hashmap当做list，核心方法是hasNext和next，2 分割器把hashmap当成流，核心方法trySplit和tryAdvance
       体现在接口上就是Iterable，可迭代的包括了Iterator(直接迭代，确定顺序，顺序执行，总数不知，连续且按照固定顺序处理集合元素)
         和Spliterator(分割迭代-迭代器并行版本-知道源的特征，并行执行，源是否有序，源总数可以优化遍历过程，迭代器不知道源的任何信息，延迟加载，乱序执行，并行执行)
       循环迭代模型，流分割模型
    4 视图：KeySet,Values,EntrySet三者均为终结的成员类
    5 遍历HashMap从迭代器到分割器的思考转变
5 HashMap对于Map的实现 .......



构造函数重载:tableSizeFor(cap)

size()
isEmpty()
put(K,V)组成：putVal() { hash(object)
                      ,resize(),putTreeVal(){balanceInsertion,treeifyBin},afterNodeAccess()}
    1 hashCode不一样，equals不一样，在tab上。
    2 hashCode一样，equals一样, 覆盖老值。
    3 hashCode一样，equals不一样，在链表头结点插入新值。
putAll
get(K)组成：getNode(int hash,Object key) {getTreeNode()}
remove(K): { removeNode  removeTreeNode{balanceDeletion} }
containsKey(K): { getNode, getTreeNode }
containsValue(V)
clear()
keySet(): KeySet
values(): Values
entrySet():EntrySet



5 HashMap不是线程安全的，因为HashMap里面的变量没有做任何并发控制，导致变量在多线程情况下发生异常。
6 子类：LinkedHashMap，类似：IdentityHashMap，WeakHashMap，竞品：HashTable, ConcurrentHashMap
7 Java8的HashMap引入流和分割器的概念，CHM变得search更加复杂了





### Implement note

Implementation notes.

This map usually acts as a binned (bucketed) hash table, but
when bins get too large, they are transformed into bins of
TreeNodes, each structured similarly to those in
java.util.TreeMap. Most methods try to use normal bins, but
relay to TreeNode methods when applicable (simply by checking
instanceof a node).  Bins of TreeNodes may be traversed and
used like any others, but additionally support faster lookup
when overpopulated. However, since the vast majority of bins in
normal use are not overpopulated, checking for existence of
tree bins may be delayed in the course of table methods.

该映射通常用作binned (bucketed) 的哈希表，但是
当bin太大时，它们会转换为TreeNode，每个节点的结构与
java.util.TreeMap类似。 大多数方法尝试使用普通bin，但是
在适用时中继到TreeNode方法（只需通过检查节点的实例）。 可以遍历TreeNodes的Bins
与其他任何人一样使用，但还支持更快的查找当Node过多时。 但是，由于绝大多数bin
正常使用情况下不要过度填充，请检查是否存在在使用表方法的过程中，table可能会延迟。

Tree bins (i.e., bins whose elements are all TreeNodes) are
ordered primarily by hashCode, but in the case of ties, if two
elements are of the same "class C implements Comparable<C>",
type then their compareTo method is used for ordering. (We
conservatively check generic types via reflection to validate
this -- see method comparableClassFor).  The added complexity
of tree bins is worthwhile in providing worst-case O(log n)
operations when keys either have distinct hashes or are
orderable, Thus, performance degrades gracefully under
accidental or malicious usages in which hashCode() methods
return values that are poorly distributed, as well as those in
which many keys share a hashCode, so long as they are also
Comparable. (If neither of these apply, we may waste about a
factor of two in time and space compared to taking no
precautions. But the only known cases stem from poor user
programming practices that are already so slow that this makes
little difference.)

Because TreeNodes are about twice the size of regular nodes, we
use them only when bins contain enough nodes to warrant use
(see TREEIFY_THRESHOLD). And when they become too small (due to
removal or resizing) they are converted back to plain bins.  In
usages with well-distributed user hashCodes, tree bins are
rarely used.  Ideally, under random hashCodes, the frequency of
nodes in bins follows a Poisson distribution
(http://en.wikipedia.org/wiki/Poisson_distribution) with a
parameter of about 0.5 on average for the default resizing
threshold of 0.75, although with a large variance because of
resizing granularity. Ignoring variance, the expected
occurrences of list size k are (exp(-0.5)  pow(0.5, k) /
factorial(k)). The first values are:

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

The root of a tree bin is normally its first node.  However,
sometimes (currently only upon Iterator.remove), the root might
be elsewhere, but can be recovered following parent links
(method TreeNode.root()).

All applicable internal methods accept a hash code as an
argument (as normally supplied from a public method), allowing
them to call each other without recomputing user hashCodes.
Most internal methods also accept a "tab" argument, that is
normally the current table, but may be a new or old one when
resizing or converting.

When bin lists are treeified, split, or untreeified, we keep
them in the same relative access/traversal order (i.e., field
Node.next) to better preserve locality, and to slightly
simplify handling of splits and traversals that invoke
iterator.remove. When using comparators on insertion, to keep a
total ordering (or as close as is required here) across
rebalancings, we compare classes and identityHashCodes as
tie-breakers.

The use and transitions among plain vs tree modes is
complicated by the existence of subclass LinkedHashMap. See
below for hook methods defined to be invoked upon insertion,
removal and access that allow LinkedHashMap internals to
otherwise remain independent of these mechanics. (This also
requires that a map instance be passed to some utility methods
that may create new nodes.)

The concurrent-programming-like SSA-based coding style helps
avoid aliasing errors amid all of the twisty pointer operations.

