# 🎵 Rhythmix

![Version](https://img.shields.io/badge/版本-1.0.0-blue)
![License](https://img.shields.io/badge/许可证-MIT-green)

> **Rhythmix**（可以理解为"节奏混合"）是一个简单而强大的流数据处理规则表达式引擎。它能够在流数据中找出符合特定规律（节奏）的数据，就像按照某种节奏运作一样。

## 📑 目录

## 🚀 项目简介

Rhythmix 是一个专为流数据处理设计的规则表达式引擎。它可以应用于多种场景，特别适合：

- 生产环境监控
- 实时数据分析
- 异常检测与报警
- 传感器数据处理

通过简洁而强大的表达式语法，Rhythmix 使复杂的数据处理规则变得简单易用。

## 🏁 快速开始

### 基本示例

以下是一个简单的示例，展示如何使用 Rhythmix：

```
{>1}->{count(<1,3)}->{==3}
```

> 由动图可知表达式用来表达式某个数值依次要满足 **大于1然后连续三次大于1最后等于3**

![](./doc/media/videos/300p60/complex_state_transition.gif)

在 Java 代码中使用该表达式：

```java
// 编译表达式
String code = "count(>4,3)";
Executor exe = Compiler.compile(code);

// 构造测试数据
RhythmixEventData p1 = new RhythmixEventData("11", "event1", "1", new Timestamp(System.currentTimeMillis()));
RhythmixEventData p2 = new RhythmixEventData("12", "event2", "5", new Timestamp(System.currentTimeMillis() + 100));
RhythmixEventData p3 = new RhythmixEventData("13", "event3", "2", new Timestamp(System.currentTimeMillis() + 200));

// 执行表达式
boolean res = exe.execute(p1,p2,p3);
//或者
boolean res = exe.execute(p1);
res = exe.execute(p2);
res = exe.execute(p3);
```

> RhythmixEventData 对象是表达式执行所需的对象，你需要将你的数据转换为此类型。
>
> 📖 **详细文档**: [RhythmixEventData 数据对象详解](##RhythmixEventData)

## 表达式构成

表达式构成如下：



```
{状态单元A}->{状态单元B}->{状态单元C}
```

花括号 `{}` 内是所要表述的状态单元，箭头 `->` 用于连接前后状态单元，前一个状态单元满足之后就会迁移到下一个状态单元，直到最后一个状态单元也满足之后就返回true。例如： **{>1}->{count(<1,3)}->{==3}**

> 针对简单状态比如： **0到1**，可以写成: **<0,1>**这就等于 **{==0}->{==1}**
>
> ![](./doc/media/videos/300p60/state_transition_enhanced.gif)

每个状态单元可以使用如下表达式：

- 比较表达式
- 区间表达式
- 逻辑符合表达式
- 函数调用
- 链式表达式

### 比较表达式

用于表示数据值之间的大小比较：

| 表达式 | 描述 | 示例 |
|-------|------|------|
| `>` | 大于 | `count(>4,3)` 表示若干数据中有三个大于 4, `count(>4.5,3)` 表示若干浮点数中有三个大于 4.5 |
| `<` | 小于 | `count(<4,3)` 表示若干数据中有三个小于 4, `count(<4.2,3)` 表示若干浮点数中有三个小于 4.2 |
| `>=` | 大于等于 | `{>=0.5}->{<=0.8}` 表示数据需要先满足大于等于 0.5 然后满足小于等于 0.8 |
| `<=` | 小于等于 | `{<=5.5}->{>=2.1}` 表示数据需要先满足小于等于 5.5 然后满足大于等于 2.1 |
| `==` | 等于 | `{==3.14}->{==0.0}` 表示数据需要先等于 3.14 然后等于 0.0 |
| `!=` | 不等于 | `{!=0.0}->{!=5.5}` 表示数据需要先不等于 0.0 然后不等于 5.5 |

![](./doc/media/videos/300p60/range_state_transition.gif)

> 📖 **更多示例**: [比较表达式实际应用示例](#比较表达式实际应用示例)

### 区间表达式

用于表示数据处于某个范围内，语法借鉴了数学中的区间表示法：

| 表达式 | 描述 | 示例 |
|-------|------|------|
| `(a,b)` | 大于 a 且小于 b | `(1,3)` 表示数据需满足大于 1 且小于 3, `(1.5,3.5)` 表示数据需满足大于 1.5 且小于 3.5 |
| `(a,b]` | 大于 a 且小于等于 b | `{(1,3]}->{(4,7)}` 表示数据需先满足大于 1 且小于等于 3，然后满足大于 4 小于 7 |
| `[a,b)` | 大于等于 a 且小于 b | `[1,3)` 表示数据需满足大于等于 1 且小于 3, `[1.2,3.8)` 表示数据需满足大于等于 1.2 且小于 3.8 |
| `[a,b]` | 大于等于 a 且小于等于 b | `[1,3]` 表示数据需满足大于等于 1 且小于等于 3, `[0.5,2.5]` 表示数据需满足大于等于 0.5 且小于等于 2.5 |

![](./doc/media/videos/300p60/interval_state_transition.gif)

> 📌 **提示**：左右小括号（`()`）表示大于/小于，左右中括号（`[]`）表示大于等于/小于等于。

> 📖 **更多示例**: [区间表达式实际应用示例](#区间表达式实际应用示例)

### 逻辑复合表达式

用于组合多个条件，包括 `||`（或）、`&&`（与）和 `!`（非）：

- `||` 或操作：`{==0||!=2}->{==1}` 表示数据先满足"等于 0 或不等于 2"，然后满足"等于 1"

- `&&` 且操作：`{!=0&&!=2}->{==1}` 表示数据先满足"不等于 0 且不等于 2"，然后满足"等于 1"

- `!` 非操作：主要用于不等于表达式（`!=`）中

![](./doc/media/videos/300p60/logical_and_state_transition.gif)

> 📖 **更多示例**: [逻辑复合表达式实际应用示例](#逻辑复合表达式实际应用示例)

### 函数调用

- #### count(条件, 次数)

  **基本语法**：

  ```js
  count!(条件, 次数)
  ```

  **工作原理**：

  - 当数据满足条件时，计数器递增
  - 当数据**不满足条件**时，计数器**不变**
  - 只有非连续满足条件达到指定次数时，函数才返回 true

  **使用示例**：

  ![](./doc/media/videos/300p60/count1.gif)

  **count(条件, n)**: 统计 **n 个非连续** 满足条件的数据

  ```js
  // 普通 count 函数（非严格模式）
  count(>4, 3)
  // 数据 5: >4 ✓ (计数=1)
  // 数据 2: ≤4 ✗ (计数保持=1)
  // 数据 6: >4 ✓ (计数=2)
  // 数据 1: ≤4 ✗ (计数保持=2)
  // 数据 7: >4 ✓ (计数=3) → 返回 true
  ```

- #### **count! (条件, 次数)** 🎯

  `count!` 是 `count` 函数的严格模式版本，用于检测**连续满足条件**的数据个数。与普通 `count` 函数的区别在于：

  - **count!(条件, n)**: 统计 **n 个连续** 满足条件的数据（严格模式）
  
  ![](./doc/media/videos/300p60/count_constraint_demo.gif)
  
  **基本语法**：
  
    ```js
  count!(条件, 次数)
    ```
  
    **工作原理**：
  
    - 当数据满足条件时，计数器递增
    - 当数据**不满足条件**时，计数器**立即重置为 0**
    - 只有连续满足条件达到指定次数时，函数才返回 true
  
    **使用示例**：
  
    ```js
  // 检测连续 3 次大于 4 的数据
  count!(>4, 3)
  
  // 检测连续 2 次小于 0 的数据
  count!(<0, 2)
  
  // 检测连续 5 次在范围 [10,20] 内的数据
  count!([10,20], 5)
    ```
  
    **对比示例**：
  
    假设数据序列为：`5, 2, 6, 1, 7, 8, 9`
  
    ```js
  // 严格模式 count! 函数
  count!(>4, 3)
  // 数据 5: >4 ✓ (计数=1)
  // 数据 2: ≤4 ✗ (计数重置=0) ⚠️
  // 数据 6: >4 ✓ (计数=1)
  // 数据 1: ≤4 ✗ (计数重置=0) ⚠️
  // 数据 7: >4 ✓ (计数=1)
  // 数据 8: >4 ✓ (计数=2)
  // 数据 9: >4 ✓ (计数=3) → 返回 true
    ```
  
    **实际应用场景**：
  
    ```js
    // 监控场景：检测连续 3 次温度超标
    count!(>80, 3)
    
  // 质量控制：检测连续 5 个产品合格
    count!([95,100], 5)
  
    // 网络监控：检测连续 2 次响应时间过长
    count!(>1000, 2)
    
    // 组合使用：连续异常或单次严重异常
    count!(>50, 3) || count(>100, 1)
    ```
  
    **与状态表达式结合**：
  
    ```js
    // 状态转换：正常 → 连续异常 → 恢复
    {count!(<10, 3)}->{count!(>50, 2)}->{<5}
    
  // 复杂状态检测
    {==0}->{count!(>4, 3)}->{count!(<2, 2)}
    ```
  
  ![](./doc/media/videos/300p60/multi_count_state_transition.gif)

### 链式表达式

链式表达式是对其他几种表达式的补充，它可以实现对数据进行过滤，限制，计算，验证四个步骤，由此实现多数据的处理，例如：

```
filter((-5,5)).limit(5).take(0,2).sum().meet(>1)
```

![](./doc/media/videos/400p30/chain_expression_demo_with_queue.gif)

这个表达式的功能如下：

1. 当数据处于 -5 到 5 之间会被收集到一个集合中
2. 集合最大只能容纳 5 个数据，新的会替换旧的（队列特性）
3. 每次取前两个数据（索引不包含 2，数据不满足两个则不继续后续操作）
4. 对取出的数据求和
5. 当结果大于 1 时表达式成立，返回 true

> 🔍 **链式表达式的编写可以分为四个部分**：
> 1. 过滤出想要的数据
> 2. 限制参与计算的数据的数量（数据个数或时间）
> 3. 运算规则
> 4. 符合条件

#### 数据过滤

- **filter**

  filter 函数放在链式表达式的最前面，用于过滤出想要的数据。如果不需要过滤数据则可以不写。filter 函数可以传入比较表达式、区间表达式、逻辑表达式。例如：

  ```
  filter(((1,7]||>10)&&!=5) // 可以使用小括号()来指定运算优先级
  filter(<=3)
  ```

> 提示：不使用filter时默认不过滤任何数据

- **自定义filter** 🔧

  除了使用比较表达式、区间表达式和逻辑表达式进行过滤外，Rhythmix 还支持自定义过滤器函数（FilterUDF），让您可以实现复杂的自定义过滤逻辑。

  **创建自定义过滤器**：

  实现 `FilterUDF` 接口来创建自定义过滤器：

  ```java
  public class TemperatureFilterUDF implements ChainFilterUDF {
      @Override
      public String getName() {
          return "tempFilter"; // 过滤器名称，用于表达式中调用
      }
  
      @Override
      public boolean filter(EventData event) {
          try {
              double temp = Double.parseDouble(event.getValue());
              return temp >= 20.0 && temp <= 80.0; // 保留20-80度的温度数据
          } catch (NumberFormatException e) {
              return false; // 丢弃无效数据
          }
      }
  }
  ```

  **在表达式中使用**：

  ```js
  // 使用自定义温度过滤器
  tempFilter().sum().meet(>100)
  ```
  
  **内置示例过滤器**：
  
  Rhythmix 提供了一些示例内置过滤器,你可以参考这个来编写自己的过滤器：
  
  | 过滤器名称 | 功能描述 | 使用示例 |
  |-----------|----------|----------|
  | `numericFilter()` | 🔢 只保留数值类型的数据 | `filter(numericFilter()).avg().meet(>10)` |
  | `positiveFilter()` | ➕ 只保留正数值的数据 | `filter(positiveFilter()).sum().meet(>0)` |
  
  **高级功能 - 批量过滤**：
  
  对于需要对整个数据列表进行处理的场景，可以重写 `filter(List<EventData>)` 方法：
  
  ```java
  public class ArrayFilterUDF implements FilterUDF {
      @Override
      public String getName() {
          return "arrayFilter";
      }
  
      @Override
      public List<EventData> filter(List<EventData> events) {
          // 只保留最后3个数据
          if (events.size() >= 3) {
              return events.subList(events.size() - 3, events.size());
          }
          return events;
      }
  }
  ```
  


#### 数据限制

- **limit** ⏱️

  limit 是对当前表达式最多存储数据数量的限制，可以填写时间或者个数。如果超出限制数量则会按照先进先出的原则删除最旧的数据

  ```
  limit(100ms) // ⚡ 限制 100 毫秒内的数据
  limit(10s)   // 🕐 限制 10 秒内的数据  
  limit(1m)    // 🕒 限制 1 分钟内的数据
  limit(1h)    // 🕕 限制 1 小时内的数据
  limit(1d)    // 📅 限制 1 天内的数据
  limit(10)    // 📊 限制 10 个数据
  ```

  > 💡 **提示**: 该功能的设计是为了确保在极端情况下表达式成立导致数据积累过多引起不必要的内存占用
  >
  > ⚠️ **与 window 函数的使用限制**: 不建议同时使用 limit 和 window 函数，详见 [window 函数说明](#数据限制)

- **window** 🪟

  window 函数用于创建滑动窗口，限制参与计算的数据范围。它支持两种模式：数量窗口和时间窗口。

  **数量窗口**：
  ```
  window(2)    // 🔢 滑动窗口大小为 2 个数据
  window(5)    // 🔢 滑动窗口大小为 5 个数据
  window(10)   // 🔢 滑动窗口大小为 10 个数据
  ```

  **时间窗口**：

  ```
  window(100ms) // ⚡ 100 毫秒时间窗口
  window(10s)   // 🕐 10 秒时间窗口
  window(1m)    // 🕒 1 分钟时间窗口
  window(1h)    // 🕕 1 小时时间窗口
  window(1d)    // 📅 1 天时间窗口
  ```

  **使用示例**：

  ```js
  // 数量窗口示例：只对最近 2 个数据求和
  filter((-5,5)).window(2).sum().meet(>1)
  
  // 时间窗口示例：只对 100 毫秒内的数据求和
  filter((-5,5)).window(100ms).sum().meet(>=7)
  ```

  > 💡 **重要说明**:
  > - window 参数必须大于 0，否则会抛出异常
  > - 数量窗口：当数据量达到指定数量时，保留最新的 N 个数据
  > - 时间窗口：只保留指定时间范围内的数据，基于数据的时间戳计算
  >
  > 🔄 **自动 limit 函数添加**:
  > - 当表达式中**只有 window 函数而没有 limit 函数**时，系统会自动添加一个与 window 参数相同的 limit 函数
  > - 这确保了数据队列管理的一致性和内存使用的优化
  >
  > **自动添加示例**：
  > ```js
  > // 用户编写的表达式
  > filter(>0).window(5).sum().meet(>10)
  >
  > // 系统自动转换为
  > filter(>0).limit(5).window(5).sum().meet(>10)
  > ```

  > ⚠️ **limit 和 window 函数使用限制**:
  > - **不建议手动同时使用** limit 和 window 函数，这可能导致意外的行为
  > - 如果必须同时使用，两者的参数类型和数值必须完全一致：
  >   - ✅ 正确：`limit(5).window(5)` 或 `limit(100ms).window(100ms)`
  >   - ❌ 错误：`limit(5).window(3)` 或 `limit(100ms).window(200ms)`
  >   - ❌ 错误：`limit(5).window(100ms)` （类型不匹配）
  > - 推荐做法：只使用 window 函数，让系统自动添加匹配的 limit 函数

- **take**

  take 函数有两个参数：第一个参数表示起始索引（包含），第二个参数表示结束索引（不包含）。如果第二个参数为空则默认取到最后一个元素。

  假设存在以下序列：`[0,1,2,3,4,5]`

  | 表达式 | 结果 |
  |-------|------|
  | `take(0,1)` | `[0]` |
  | `take(0,2)` | `[0,1]` |
  | `take(0,-1)` | `[0,1,2,3,4]` |
  | `take(-3,-1)` | `[3,4]` |
  | `take(-3)` | `[3,4,5]` |
  | `take(0)` | `[0,1,2,3,4,5]` |
#### 数据计算
Rhythmix 提供了多种数据计算函数,用于对数据进行统计分析:

- **sum**

  计算数据序列的总和。支持整数和浮点数的计算。

  ```js
  // 整数序列 [10, 7, 10], sum() 将返回 27
  filter(>0).sum()
  
  // 浮点数序列 [10.5, 7.3, 10.2], sum() 将返回 28.0
  filter(>0).sum()
  ```

- **avg** 

  计算数据序列的平均值。

  ```js
  // 整数序列 [10, 7, 10], avg() 将返回 9.0
  filter(>0).avg()
  
  // 浮点数序列 [10.5, 7.3, 10.2], avg() 将返回 9.33
  filter(>0).avg()
  ```

- **count**

  统计数据序列中元素的个数。

  ```js
  // 整数序列 [10, 7, 10], count() 将返回 3
  filter(>0).count()
  
  // 浮点数序列 [10.5, 7.3, 10.2], count() 将返回 3
  filter(>0).count()
  ```


- **stddev**

  计算数据序列的标准差。需要至少两个数据点才能计算。结果保留3位小数。

  ```js
  // 整数序列 [10, 7, 10], stddev() 将返回 1.414
  filter(>0).stddev()
  
  // 浮点数序列 [10.5, 7.3, 10.2], stddev() 将返回 1.473
  filter(>0).stddev()
  ```

> 💡 **注意**:
>
> - 所有计算函数都会自动忽略空值
> - 对于非数值类型的数据将抛出计算异常
> - 标准差计算至少需要2个数据点
> - 当输入序列包含浮点数时,计算结果会自动转换为浮点数类型

- **自定义计算函数** 🧮

  除了使用内置的计算函数外，Rhythmix 还支持自定义计算器函数（CalculatorUDF），让您可以实现复杂的自定义计算逻辑。

  **创建自定义计算器**：

  实现 `CalculatorUDF` 接口来创建自定义计算器：

  ```java
  public class MyMaxCalculator implements ChainCalculatorUDF {
      @Override
      public String getName() {
          return "myMax"; // 计算器名称，用于表达式中调用
      }
  
      @Override
      public Number calculate(List<EventData> values) {
          // 自定义计算逻辑：找出最大值
          if (values == null || values.isEmpty()) {
              return 0;
          }
  
          double max = Double.NEGATIVE_INFINITY;
          boolean hasValidNumber = false;
  
          for (EventData rhythmixEventData : values) {
              if (rhythmixEventData == null || rhythmixEventData.getValue() == null) {
                  continue;
              }
  
              try {
                  double num;
                  Object value = rhythmixEventData.getValue();
                  if (value instanceof Number) {
                      num = ((Number) value).doubleValue();
                  } else {
                      num = Double.parseDouble(value.toString());
                  }
  
                  if (!Double.isNaN(num) && num > max) {
                      max = num;
                  }
                  hasValidNumber = true;
              } catch (NumberFormatException e) {
                  // 跳过非数值类型的数据
                  continue;
              }
          }
  
          return hasValidNumber ? (max == Math.floor(max) ? (long) max : max) : 0;
      }
  }
  ```

  **在表达式中使用**：

  ```js
  // 使用自定义最大值计算器
  filter(>0).limit(5).myMax().meet(>10)
  
  // 复杂链式表达式示例
  filter((-100,100)).window(10).myMax().meet(>=50)
  ```

  **内置示例计算器**：

  Rhythmix 提供了一些示例内置计算器，你可以参考这些来编写自己的计算器：

  | 计算器名称 | 功能描述 | 使用示例 |
  |-----------|----------|----------|
  | `maxCalc()` | 📈 找出数据序列的最大值 | `filter(>0).maxCalc().meet(>100)` |
  | `minCalc()` | 📉 找出数据序列的最小值 | `filter(>0).minCalc().meet(<10)` |


> 💡 **注意**:
> - **自定义计算器**会自动被系统发现和注册，无需手动注册
> - 计算器名称必须唯一，重复名称会导致注册失败
> - 自定义计算器应该处理异常情况，避免影响整个表达式的执行

#### 条件判断

条件判断函数用于对计算结果进行最终的条件验证，通常位于链式表达式的末尾。

- **内置条件判断函数** 🎯

  Rhythmix 提供了多种内置的条件判断函数，用于验证计算结果是否满足特定条件：

  | 函数名称 | 功能描述 | 判断条件 | 使用示例 |
  |---------|----------|----------|----------|
  | `meet(条件)` | 🎯 基础条件判断 | 支持各种比较操作符 | `filter(>0).sum().meet(>10)` |
  | `thresholdMeet()` | 📊 阈值判断 | 值 >= 10 | `filter(>0).avg().thresholdMeet()` |
  | `rangeMeet()` | 📏 范围判断 | 5 <= 值 <= 50 | `filter(>0).count().rangeMeet()` |
  | `positiveMeet()` | ➕ 正数判断 | 值 > 0 | `filter(>-100).sum().positiveMeet()` |
  | `evenMeet()` | 🔢 偶数判断 | 值为偶数整数 | `filter(>0).count().evenMeet()` |

- **基础 meet() 函数** 🎯

  `meet()` 函数是最基础和灵活的条件判断函数，支持多种比较操作：

  ```js
  // 大于判断
  filter(>0).sum().meet(>100)

  // 小于判断
  filter(>0).avg().meet(<50)

  // 等于判断
  filter(>0).count().meet(==5)

  // 范围判断
  filter(>0).sum().meet([10,100])

  // 不等于判断
  filter(>0).avg().meet(!=0)

  // 大于等于判断
  filter(>0).sum().meet(>=10)

  // 小于等于判断
  filter(>0).avg().meet(<=50)
  ```

- **自定义条件判断函数** 🧩

  除了使用内置的条件判断函数外，Rhythmix 还支持自定义条件判断函数（MeetUDF），让您可以实现复杂的自定义判断逻辑。

  **创建自定义条件判断函数**：

  实现 `MeetUDF` 接口来创建自定义条件判断函数：

  ```java
  public class CustomThresholdMeetUDF implements ChainMeetUDF {
      @Override
      public String getName() {
          return "customThresholdMeet"; // 函数名称，用于表达式中调用
      }
  
      @Override
      public boolean meet(Number calculatedValue) {
          // 自定义判断逻辑：检查值是否大于 15
          if (calculatedValue == null) {
              return false;
          }
  
          try {
              double numericValue = calculatedValue.doubleValue();
              return numericValue > 15.0; // 自定义阈值条件
          } catch (Exception e) {
              return false;
          }
      }
  
      @Override
      public String getDescription() {
          return "检查计算值是否大于 15";
      }
  }
  ```

  **在表达式中使用**：

  ```js
  // 使用自定义阈值判断函数
  filter(>0).sum().customThresholdMeet()
  
  // 复杂链式表达式示例
  filter((-100,100)).window(10).avg().customThresholdMeet()
  ```

  **内置示例条件判断函数**：

  Rhythmix 提供了一些示例内置条件判断函数，你可以参考这些来编写自己的判断函数：

  | 函数名称 | 功能描述 | 判断条件 | 使用示例 |
  |---------|----------|----------|----------|
  | `thresholdMeet()` | 📊 阈值判断 | 值 >= 10.0 | `filter(>0).sum().thresholdMeet()` |
  | `rangeMeet()` | 📏 范围判断 | 5.0 <= 值 <= 50.0 | `filter(>0).avg().rangeMeet()` |
  | `positiveMeet()` | ➕ 正数判断 | 值 > 0.0 | `filter(>-100).sum().positiveMeet()` |
  | `evenMeet()` | 🔢 偶数判断 | 值为偶数整数 | `filter(>0).count().evenMeet()` |

  **高级功能 - 复杂判断逻辑**：

  自定义条件判断函数可以实现任意复杂的判断逻辑：

  ```java
  public class MultiConditionMeetUDF implements ChainMeetUDF {
      @Override
      public String getName() {
          return "multiConditionMeet";
      }
  
      @Override
      public boolean meet(Number calculatedValue) {
          if (calculatedValue == null) {
              return false;
          }
  
          try {
              double value = calculatedValue.doubleValue();
  
              // 复杂的多条件判断逻辑
              if (value >= 0 && value <= 10) {
                  return value % 2 == 0; // 0-10范围内必须是偶数
              } else if (value > 10 && value <= 100) {
                  return value > 50; // 10-100范围内必须大于50
              } else {
                  return value < 0; // 其他情况必须是负数
              }
          } catch (Exception e) {
              return false;
          }
      }
  }
  ```

  **使用示例**：

  ```js
  // 多条件判断示例
  filter(>0).limit(10).sum().multiConditionMeet()
  
  // 与其他函数组合使用
  filter([0,200]).window(5).avg().multiConditionMeet()
  
  // 复杂链式表达式
  filter(>0).limit(20).window(10).sum().multiConditionMeet()
  ```

> 💡 **重要说明**:
> - **自定义条件判断函数**会自动被系统发现和注册，无需手动注册
> - 函数名称必须唯一，重复名称会导致注册失败
> - 条件判断函数应该处理异常情况，避免影响整个表达式的执行
> - 条件判断函数通常位于链式表达式的末尾，用于最终的结果验证
> - 内置函数提供了常用的判断逻辑，可以直接使用无需额外配置

---

## 附录

### 比较表达式实际应用示例

比较表达式用于表示数据值之间的大小比较，是最基础的表达式类型。

#### 温度监控场景

```js
// 检测温度超过 30 度
>30

// 检测温度低于 10 度
<10

// 检测温度等于 25 度（精确匹配）
==25

// 检测温度不等于 0 度（排除异常读数）
!=0
```

#### 生产线质量控制

```js
// 产品重量大于等于标准重量 100g
>=100

// 产品尺寸小于等于最大允许值 50mm
<=50

// 检测连续3个产品重量都大于 95g
count(>95, 3)

// 检测连续2个产品尺寸都小于 45mm
count!(<45, 2)
```

#### 网络延迟监控

```js
// 响应时间大于 1000ms（1秒）
>1000

// 检测连续5次响应时间都小于 100ms
count!(<100, 5)

// 状态转换：正常响应 → 高延迟 → 恢复正常
{<100}->{>1000}->{<200}
```

#### 传感器数据处理

```js
// 压力传感器读数等于 0（可能故障）
==0

// 湿度传感器读数不等于 -1（排除错误值）
!=(-1)

// 检测压力值连续3次大于等于 50
count!(>=50, 3)

// 复合条件：温度不等于 0 且大于 10（排除异常值并检测正常范围）
!=0&&>10

// 状态转换：温度从正常升高到过热状态
{<=40}->{>80}
```

### 区间表达式实际应用示例

区间表达式用于表示数据处于某个范围内，语法借鉴了数学中的区间表示法。

#### 温度控制系统

```js
// 正常工作温度范围：20-25度（不包含边界）
(20,25)

// 安全温度范围：0-100度（包含边界）
[0,100]

// 预警温度范围：大于80度但不超过90度
(80,90]

// 危险温度范围：小于0度或大于等于100度（使用状态转换）
{<0}->{>=100}
```

#### 生产质量控制

```js
// 产品重量合格范围：95-105克（包含边界）
[95,105]

// 产品尺寸精度范围：49.5-50.5mm（不包含边界）
(49.5,50.5)

// 检测连续5个产品都在合格重量范围内
count!([95,105], 5)

// 状态转换：从合格范围到不合格范围
{[95,105]}->{(0,95)||(105,200)}
```

#### 网络性能监控

```js
// 正常响应时间：0-500ms（包含0，不包含500）
[0,500)

// 可接受响应时间：500-1000ms（不包含边界）
(500,1000)

// 超时响应时间：大于1000ms且小于等于5000ms
(1000,5000]

// 检测连续3次响应时间都在正常范围
count!([0,500), 3)
```

#### 传感器数据验证

```js
// 湿度传感器有效范围：0-100%（包含边界）
[0,100]

// 压力传感器正常范围：大于0但小于1000（不包含边界）
(0,1000)

// 电压传感器工作范围：3.0-3.6V（包含下界，不包含上界）
[3.0,3.6)

// 复合检测：温度在正常范围且湿度在安全范围
[20,30]&&[40,60]
```

#### 金融风控系统

```js
// 正常交易金额范围：100-10000元（包含边界）
[100,10000]

// 可疑交易金额：大于10000但不超过50000元
(10000,50000]

// 高风险交易：超过50000元（开区间表示）
(50000,999999)

// 检测连续3笔交易都在正常范围内
count!([100,10000], 3)
```

#### 设备运行监控

```js
// CPU使用率正常范围：0-80%（包含0，不包含80）
[0,80)

// 内存使用率警告范围：80-95%（不包含边界）
(80,95)

// 磁盘使用率危险范围：95-100%（包含边界）
[95,100]

// 状态转换：从正常到警告再到危险
{[0,80)}->{(80,95)}->{[95,100]}
```

### 逻辑复合表达式实际应用示例

逻辑复合表达式通过 `||`（或）、`&&`（与）和 `!`（非）操作符组合多个条件，实现复杂的业务逻辑判断。

#### 基础逻辑操作符

**OR 操作符 (`||`)**：

```js
// 温度异常检测：过热或过冷
<10||>40

// 网络状态异常：超时或错误
>5000||==(-1)

// 设备故障检测：电压过低或过高
<3.0||>5.0
```

**AND 操作符 (`&&`)**：

```js
// 正常工作条件：温度和湿度都在范围内
>=20&&<=30&&>=40&&<=60

// 安全运行状态：压力和温度都正常
[0,100]&&[20,80]

// 合格产品：重量和尺寸都达标
[95,105]&&[49,51]
```

**NOT 操作符 (`!`)**：

```js
// 排除异常值：不等于错误代码
!=(-1)&&!=0

// 有效数据：不是空值且不是默认值
!=null&&!=0
```

#### 实际应用场景

**智能家居系统**：

```js
// 自动空调控制：温度过高或湿度过大时启动
>26||>70

// 安全报警：门窗异常或烟雾检测
==1||==1  // 门开(1)或烟雾检测(1)

// 节能模式：温度适宜且湿度正常且无人在家
[22,26]&&[40,60]&&==0

// 状态转换：从正常到报警
{[20,30]&&[40,70]}->{<15||>35||<30||>80}
```

**工业监控系统**：

```js
// 设备预警：温度高或压力大或振动异常
>80||>150||>5.0

// 生产线正常：所有参数都在正常范围
[70,90]&&[100,200]&&[0,2.0]

// 紧急停机条件：任一关键参数异常
>100||>300||>10.0

// 复合状态检测：正常运行然后出现异常
{[70,90]&&[100,200]}->{>100||>300}
```

**金融风控系统**：

```js
// 可疑交易：金额异常或时间异常
>50000||<1||>23

// 正常用户行为：金额合理且时间正常且地区正常
[100,10000]&&[6,22]&&==1

// 高风险账户：多个风险指标同时触发
>100000&&<6&&==0

// 风险升级：从正常到可疑再到高风险
{[100,5000]&&[8,20]}->{>10000||<8||>20}->{>50000&&<6}
```

**网络安全监控**：

```js
// 异常访问：频率过高或来源可疑
>1000||==0

// 正常流量：请求数适中且响应正常且无错误
[10,500]&&<1000&&==0

// DDoS攻击检测：请求激增且来源分散
>5000&&>100

// 安全事件升级：正常流量突然异常
{[10,500]&&<1000}->{>2000||>5000}
```

**医疗设备监控**：

```js
// 患者生命体征异常：心率或血压异常
<60||>100||<90||>140

// 设备正常工作：所有指标都在正常范围
[60,100]&&[90,140]&&[36,37.5]

// 紧急报警：多个生命体征同时异常
<50||>120&&<80||>160

// 病情变化：从稳定到异常
{[70,90]&&[100,130]}->{<60||>100||<90||>140}
```

**环境监测系统**：

```js
// 空气质量警报：PM2.5高或臭氧浓度高
>75||>160

// 舒适环境：温湿度适宜且空气质量好
[20,26]&&[40,60]&&<35

// 污染事件：多项指标同时超标
>150&&>200&&>100

// 环境恶化：从良好到污染
{<35&&<100}->{>75||>160}
```

#### 复杂组合示例

**多层嵌套逻辑**：

```js
// 复杂业务规则：(条件A或条件B) 且 (条件C或条件D)
(>30||<10)&&(>80||<20)

// 设备故障诊断：温度异常且(压力异常或振动异常)
(>90||<10)&&(>200||>5.0)

// 用户行为分析：(大额交易或频繁交易) 且 (异常时间或异常地点)
(>10000||>50)&&(<6||>22||==0)
```

**状态转换中的逻辑组合**：

```js
// 系统健康度监控：正常→警告→严重
{[0,30]&&[0,50]}->{(>30&&<60)||(>50&&<80)}->{>60||>80}

// 网络质量监控：优秀→良好→差
{<100&&>95}->{(>=100&&<500)||(>=80&&<95)}->{>=500||<80}

// 设备生命周期：新设备→老化→故障
{<1000&&==0}->{(>=1000&&<5000)||(>0&&<3)}->{>=5000||>=3}
```

#### 最佳实践

> 💡 **使用建议**：
> - 使用括号 `()` 明确运算优先级，避免歧义

### RhythmixEventData

`RhythmixEventData` 是 Rhythmix 引擎处理数据的核心对象，所有输入数据都需要转换为此类型。

#### 类结构

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RhythmixEventData {
    private String id;           // 数据唯一标识符
    private String code;         // 数据编码
    private String serialNumber; // 序列号
    private String name;         // 数据名称/事件名称
    private String value;        // 数据值（核心字段）
    private Timestamp ts;        // 时间戳
    private String[] args;       // 扩展字段数组
}
```

#### 构造方法

**完整构造方法**：

```java
// 手动指定所有字段
RhythmixEventData data = new RhythmixEventData("id001", "event1", "25.5", new Timestamp(System.currentTimeMillis()));
```

**自动生成ID构造方法**：

```java
// 自动生成唯一ID
RhythmixEventData data = new RhythmixEventData("temperature", "25.5", new Timestamp(System.currentTimeMillis()));
```

**Builder模式构造**：

```java
RhythmixEventData data = RhythmixEventData.builder()
    .id("sensor001")
    .name("temperature")
    .value("25.5")
    .ts(new Timestamp(System.currentTimeMillis()))
    .code("TEMP_001")
    .serialNumber("SN123456")
    .build();
```

#### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | String | 否 | 数据唯一标识符，未指定时自动生成 |
| `name` | String | 是 | 事件/数据名称，用于标识数据类型 |
| `value` | String | 是 | **核心字段**，表达式计算的主要数据值 |
| `ts` | Timestamp | 是 | 时间戳，用于时间窗口和状态转换 |
| `code` | String | 否 | 数据编码，可用于分类或标识 |
| `serialNumber` | String | 否 | 序列号，可用于设备标识 |
| `args` | String[] | 否 | 扩展字段，用于存储额外信息 |

#### 使用示例

**温度传感器数据**：

```java
// 创建温度数据
RhythmixEventData tempData = new RhythmixEventData(
    "temp_sensor_01",
    "temperature",
    "28.5",
    new Timestamp(System.currentTimeMillis())
);

// 使用表达式检测温度
String expression = ">30";
Executor executor = Compiler.compile(expression);
boolean isHot = executor.execute(tempData); // false，因为 28.5 不大于 30
```

**生产线数据监控**：

```java
// 创建多个产品重量数据
List<RhythmixEventData> products = Arrays.asList(
    new RhythmixEventData("product1", "weight", "95.2", new Timestamp(System.currentTimeMillis())),
    new RhythmixEventData("product2", "weight", "98.7", new Timestamp(System.currentTimeMillis() + 1000)),
    new RhythmixEventData("product3", "weight", "97.1", new Timestamp(System.currentTimeMillis() + 2000))
);

// 检测连续3个产品重量都大于95g
String expression = "count!(>95, 3)";
Executor executor = Compiler.compile(expression);
boolean allQualified = executor.execute(products.toArray(new RhythmixEventData[0])); // true
```

**网络延迟监控**：

```java
// 创建响应时间数据
RhythmixEventData[] responses = {
    RhythmixEventData.builder()
        .name("api_response")
        .value("150")  // 150ms
        .ts(new Timestamp(System.currentTimeMillis()))
        .code("API_CALL")
        .build(),
    RhythmixEventData.builder()
        .name("api_response")
        .value("1200") // 1200ms
        .ts(new Timestamp(System.currentTimeMillis() + 1000))
        .code("API_CALL")
        .build()
};

// 检测状态转换：正常响应 → 高延迟
String expression = "{<500}->{>1000}";
Executor executor = Compiler.compile(expression);
boolean hasLatencySpike = executor.execute(responses); // true
```

#### 重要提示

> 💡 **关键要点**：
>
> - `value` 字段是表达式计算的核心，必须是可转换为数值的字符串
> - `ts` 时间戳用于时间窗口计算和状态转换的时序判断
> - 使用 Builder 模式可以更灵活地创建复杂的数据对象
> - ID 字段如果不指定，系统会自动生成唯一标识符
> - 所有必填字段（`name`、`value`、`ts`）都必须提供，否则表达式执行可能出错