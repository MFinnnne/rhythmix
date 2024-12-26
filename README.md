# rhythmix

rhythmix（可以理解为节奏混合） 是一个简单的用于流数据处理的规则表达式面。至于为什么要叫rhythmix是因为我觉得它在流数据中找出符合某种规律(节奏)的数据，有点想按照某种节奏运作一样（果然很牵强哈哈哈哈）。目前作者只有在流数据场景应用比较多，比如生产跟踪和报警，大家可以根据自己需求应用在其他场景。



## :star: 如何开始

### 举一个例子

> ```js
> count(>4,3)
> ```

这行表达式对的意思就是当一个传感器的值大于4 并且满足3次(非连续三次)之后返回真。当然这只是表达式，要想真实起作用其实你要这样写。

```java
   String code = "count(>4,3)";【1】
   Executor exe = Compiler.compile(code);【2】
   // 构造三个数据
   SensorEvent p1 = Util.genEventData("11", "1", new Timestamp(System.currentTimeMillis()));
   SensorEvent p2 = Util.genEventData("12", "11", new Timestamp(System.currentTimeMillis()+100));
   SensorEvent p3 = Util.genEventData("13", "9", new Timestamp(System.currentTimeMillis()+200));
	// 依次执行前两个数据
   boolean res = exe.execute(p1, p2);
```

第【1】【2】其实就是编译表达式，他会给你返回一个执行器，最后调用` exe.execute(p1, p2)`即可，execute 方法可以传入一个或多个参数，意味着你也可以这样写  `exe.execute(p1）`  。执行execute方法会返回给你一个bool值，当你是数据满足表达式时返回值为true反之为false。

其中p1，p2就是传感器数据也就是 `EventData`实例

```java
public class EventData {
    private String id;  //  传感器事件的id
    private String name; // 传感器事件名
    private String value; // 传感器值
    private Timestamp ts; // 时间戳
    private EventValueType valueType; // 数据类型
}
```

### 再来一个例子

我们经常会遇见当一个事件值从0变化到1时，执行某个动作的情况。这时我们的表达式该如何写呢

```java
String code = "{==0}->{==1}";
Executor executor = Compiler.compile(code);
 boolean res =executor.execute(eventData)
```

表达式的最小单元就是 `{expr}`花括号内的内容，`==`是等于表达式。`->`箭头为变化表达式，表达式事件值由前一个状态变为后一个状态。`{==0}->{==1`表达的意思就是事件的值 **从等于0的状态变为等于1的状态**当满足此时表**executor.execute**返回true，否则false;

当然面对这种只有等于表达式的情况，我们也可以这样写。

> ```
>  <0,1>
> ```

一对尖括号0和1用逗号隔开即可。

# 常用表达式和方法

### 表达式

#### 比较表达式

> 用于表示数据值之间大小等于比较

主要包含一下几种：

- `>`  ,大于表达式，比如`count(>4,3)`表示若干数据中有三个大于4
- `<` ,小于表达式，比如`count(<4,3)`表示若干数据中有三个小于4

- `>=`,大于等于表达式，比如 `{>=0}->{<=0}`表示数据需要先满足大于等于0然后满足小等于0
- `<=` ,小于等于表达式
- `==`,等于表达式
- `!=`,不等于表达式

#### 区间表达式

> 用于表示数据处于某个范围之类，表达式语法直接抄的数学中的表示

- `(a,b)`,表示数据大于a且小于b，比如 `(1,3)`表达式数据需满足 大于1 且小于3
- `(a,b]`,表示数据大于a且小于等于b，比如  `{(1,3]}->{(4,7)}`表示数据需先满足从大于1且小于等于3然后满足大于4小于等于7
- `[a,b)`,表示数据大于等于a且小于b
- `[a,b]`，表示数据大于等于a且小于等于b

大家可以很明显的看出来 左右小括号（`()`）表示大于小于，左右中括号（`[]`）表示小于等于和大于等于。

#### 逻辑表达式

> 就是 `||,&&,!`表示或与非

- `||` 与操作，比如 `{==0||!=2}->{==1}`表示数据先满足等于0或者不等于2然后满不足等于1

  > com.df.rhythmix.integration.TestSimpleExample#testOrOp

  ```java
  @Test
  void testOrOp() throws TranslatorException {
      String code = "{==0||!=2}->{==1}";
      Executor executor = Compiler.compile(code);
  
      EventData p1 = Util.genEventData("1", "3", new Timestamp(System.currentTimeMillis()));
      EventData p2 = Util.genEventData("2", "1", new Timestamp(System.currentTimeMillis()));
      Assertions.assertTrue(executor.execute(p1, p2)); // 满足该条件
  }
  ```

  
