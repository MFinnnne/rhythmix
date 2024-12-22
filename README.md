# rhythmix

rhythmix（可以理解为节奏混合） 是一个用于流数据处理的库，这个库主要作用其实是在报警方面。至少为什么要叫rhythmix是因为我觉得它在数据中找出符合某种规律(节奏)的数据，有点想按照某种节奏运作一样（果然很牵强哈哈哈哈）。

## :star: 如何开始

**:bamboo:**举一个例子

> ```js
> count(>4,3)
> ```

这行表达式对的意思就是当一个传感器的值大于4 并且满足3次(非连续三次)之后返回真。当然这只是表达式，要想真实起作用其实你要这样写。

```java
   String code = "count(>4,3)";【1】
   Executor exe = Compiler.compile(code);【2】
   SensorEvent p1 = Util.genPointData("11", "1", new Timestamp(System.currentTimeMillis()));
   SensorEvent p2 = Util.genPointData("12", "11", new Timestamp(System.currentTimeMillis()+100));
   SensorEvent p3 = Util.genPointData("13", "9", new Timestamp(System.currentTimeMillis()+200));
   boolean execute = exe.execute(p1, p2);
```

第【1】【2】其实就是编译表达式，他会给你返回一个执行器接着你  调用` exe.execute(p1, p2);`即可execute 方法可以传入一个或多个参数，意味着你也可以这样写  `exe.execute(p1）`  。其中p1，p2就是传感器数据也就是 `SensorEvent`实例。

```java
public class SensorEvent {
    private String id;
    private String name;
    private String value;
    private Timestamp ts;
    private SensorEventValueType valueType;
}
```

