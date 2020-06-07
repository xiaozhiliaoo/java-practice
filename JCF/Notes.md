https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html
https://docs.oracle.com/javase/tutorial/collections/intro/index.html

https://github.com/CarpenterLee/JCFInternals

High Level

Smalltalk's collection hierarchy

A collections framework is a unified architecture for representing and manipulating collections,
 enabling collections to be manipulated independently of implementation details.

Design Goals：
To keep the number of core interfaces small, the interfaces do not attempt
 to capture such subtle distinctions as mutability, modifiability, and resizability. 
 Instead, certain calls in the core interfaces are optional, enabling implementations to
  throw an UnsupportedOperationException to indicate that they do not support a specified 
  optional operation. Collection implementers must clearly document which optional operations 
  are supported by an implementation.

For the Set interface, HashSet is the most commonly used implementation.
For the List interface, ArrayList is the most commonly used implementation.
For the Map interface, HashMap is the most commonly used implementation.
For the Queue interface, LinkedList is the most commonly used implementation.
For the Deque interface, ArrayDeque is the most commonly used implementation.


集合框架包括什么？
Collections Framework
Interfaces(接口是最重要的,接口是集合的灵魂，JCF基于接口设计的框架)
Implementations(实现影响性能!)
Algorithms(加强集合功能!)

size()方法多态性，在Set里面叫Cardinality，也就是HyperLogLog的Cardinality
理解多态。


Query Operations
Modification Operations
Bulk Operations
Aggregate Operations
Array Operations



 Iterator.remove is the only safe way to modify a collection during iteration
  the behavior is unspecified if the underlying collection is modified in any other way while the iteration is in 
  progress.

c.removeAll(Collections.singleton(e));
c.removeAll(Collections.singleton(null));


Set size() = cardinality
Set的Bulk Operations 就是交并补集操作

List (sequence) 比Collection多了
Positional access：set，get，add，
Search：indexOf，
Iteration：ListIterator
range operations:subList

集合是Java技术里面最复杂的一环节。但是封装的好，导致使用异常简单。

polymorphic algorithm

LinkedList竟然实现了Deque

Map is  mathematical function abstraction.

Queue不加强equals和hashCode方法

SortedSet：Range view ，Endpoints ，Comparator access，

SortedMap：Range view ，Endpoints ，Comparator access，

core collection interfaces：

Map's subinterface(子接口), SortedMap

These interfaces allow collections to be manipulated independently of the details of their representation.

Implementations：
1  General-purpose
2  Special-purpose
3  Concurrent 
4  Wrapper 
5  Convenience 
6  Abstract implementations


LinkedList 是 FIFO Queue的实现，也是Deque的实现。

fail-fast iterators

In general, it is good API design practice not to make users pay for a feature they don't use


As a rule, you should be thinking about the interfaces, not the implementations. 
That is why there are no programming examples in this section. For the most part,
the choice of implementation affects only performance. The preferred style, as
mentioned in the Interfaces section, is to choose an implementation when a Collection 
is created and to immediately assign the new collection to a variable of the 
corresponding interface type (or to pass the collection to a method expecting 
an argument of the interface type). In this way, the program does not become
dependent on any added methods in a given implementation, leaving the programmer 
free to change implementations anytime that it is warranted by performance concerns 
or behavioral details.

集合接口区别？
https://docs.oracle.com/javase/tutorial/collections/interfaces/index.html

集合实现区别？
https://docs.oracle.com/javase/tutorial/collections/implementations/index.html

HashSet tuning parameter：initial capacity(resize)，load factor
ArrayList tuning parameter： initial capacity(grow)

为啥集合接口很重要，讲例子时候都是用接口讲，因为接口包含了所有操作，而实现只是影响性能。

WeakHashMap："registry-like" data structures, where the utility of an entry vanishes when its key is no longer reachable by any thread

You plan to write a program that uses several basic collection interfaces: Set, List, Queue, and Map. You're not sure 
which implementations will work best, so you decide to use general-purpose implementations until you get a better
 idea how your program will work in the real world. Which implementations are these?
 
Custom Collection Implementations：
Persistent
Application-specific
High-performance, special-purpose
High-performance, general-purpose
Enhanced functionality
Convenience
Adapter

abstract implementations designed expressly to facilitate custom implementation

 an interface-based Collections Framework
 
https://docs.oracle.com/javase/tutorial/collections/interoperability/api-design.html
API Design：
1  Parameters
2  Return Values
