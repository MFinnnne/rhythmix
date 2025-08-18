# 🎵 Rhythmix

<div align="center">

![Version](https://img.shields.io/badge/版本-1.0.0-blue)
![License](https://img.shields.io/badge/许可证-MIT-green)

</div>

> **Rhythmix**（可以理解为"节奏混合"）是一个简单而强大的流数据处理规则表达式引擎。它能够在流数据中找出符合特定规律（节奏）的数据，就像按照某种节奏运作一样。

## 📑 目录

- [🎵 Rhythmix](#-rhythmix)
  - [📑 目录](#-目录)
  - [🚀 项目简介](#-项目简介)
  - [🏁 快速开始](#-快速开始)
    - [基本示例](#基本示例)
    - [状态转换示例](#状态转换示例)
  - [📝 表达式语法](#-表达式语法)
    - [箭头表达式](#箭头表达式)
    - [状态表达式](#状态表达式)
      - [比较表达式](#比较表达式)
      - [区间表达式](#区间表达式)
      - [逻辑表达式](#逻辑表达式)
    - [链式表达式](#链式表达式)
      - [数据过滤](#数据过滤)
      - [数据限制](#数据限制)
      - [数据计算](#数据计算)

## 🚀 项目简介

Rhythmix 是一个专为流数据处理设计的规则表达式引擎。它可以应用于多种场景，特别适合：

- 生产环境监控
- 实时数据分析
- 异常检测与报警
- 传感器数据处理

通过简洁而强大的表达式语法，Rhythmix 使复杂的数据处理规则变得简单易用。

## 🏁 快速开始

### 基本示例

以下是一个简单的示例，展示如何使用 Rhythmix

```js
count(>4,3)
```

这行表达式的意思是：当值大于 4 并且满足 3 次（非连续三次）之后返回真。

![count1](doc/media/videos/300p60/count1.gif)

在 Java 代码中使用该表达式：

```java
// 编译表达式
String code = "count(>4,3)";
Executor exe = Compiler.compile(code);

// 构造测试数据
SensorEvent p1 = Util.genEventData("11", "1", new Timestamp(System.currentTimeMillis()));
SensorEvent p2 = Util.genEventData("12", "11", new Timestamp(System.currentTimeMillis()+100));
SensorEvent p3 = Util.genEventData("13", "9", new Timestamp(System.currentTimeMillis()+200));

// 执行表达式
boolean res = exe.execute(p1, p2);
```

### 状态转换示例

检测事件值从 0 变化到 1 的情况：

```java
String code = "{==0}->{==1}";
Executor executor = Compiler.compile(code);
boolean res = executor.execute(eventData);
```

对于简单的等于表达式，还可以使用更简洁的语法：

```
<0,1>
```

一对尖括号内的 0 和 1 用逗号隔开，表示从 0 变化到 1。

![state_transition_0_1](doc/media/videos/300p60/state_transition_enhanced.gif)

## 📝 表达式语法

### 箭头表达式

箭头表达式用于表述多种状态的依次校验。语法如下：

```
{状态A}->{状态B}->{状态C}
```

花括号 `{}` 内是所要表述的状态，箭头 `->` 表示状态的迁移顺序。例如：

```
{>1}->{count(<1,3)}->{==3}
```

![](doc/media/videos/300p60/complex_state_transition.gif)

这个表达式表示：当输入的数据依次满足"大于 1"、"三次（非连续）小于 1"、"等于 3"时，表达式成立并返回 true。

只有当前一个状态满足后，才会继续校验下一个状态。

### 状态表达式

花括号内的内容称为状态表达式或状态方法，包括以下几种类型：

#### 比较表达式

用于表示数据值之间的大小比较：

| 表达式 | 描述 | 示例 |
|-------|------|------|
| `>` | 大于 | `count(>4,3)` 表示若干数据中有三个大于 4, `count(>4.5,3)` 表示若干浮点数中有三个大于 4.5 |
| `<` | 小于 | `count(<4,3)` 表示若干数据中有三个小于 4, `count(<4.2,3)` 表示若干浮点数中有三个小于 4.2 |
| `>=` | 大于等于 | `{>=0.5}->{<=0.8}` 表示数据需要先满足大于等于 0.5 然后满足小于等于 0.8 |
| `<=` | 小于等于 | `{<=5.5}->{>=2.1}` 表示数据需要先满足小于等于 5.5 然后满足大于等于 2.1 |
| `==` | 等于 | `{==3.14}->{==0.0}` 表示数据需要先等于 3.14 然后等于 0.0 |
| `!=` | 不等于 | `{!=0.0}->{!=5.5}` 表示数据需要先不等于 0.0 然后不等于 5.5 |

#### 区间表达式

用于表示数据处于某个范围内，语法借鉴了数学中的区间表示法：

| 表达式 | 描述 | 示例 |
|-------|------|------|
| `(a,b)` | 大于 a 且小于 b | `(1,3)` 表示数据需满足大于 1 且小于 3, `(1.5,3.5)` 表示数据需满足大于 1.5 且小于 3.5 |
| `(a,b]` | 大于 a 且小于等于 b | `{(1,3]}->{(4,7)}` 表示数据需先满足大于 1 且小于等于 3，然后满足大于 4 小于 7 |
| `[a,b)` | 大于等于 a 且小于 b | `[1,3)` 表示数据需满足大于等于 1 且小于 3, `[1.2,3.8)` 表示数据需满足大于等于 1.2 且小于 3.8 |
| `[a,b]` | 大于等于 a 且小于等于 b | `[1,3]` 表示数据需满足大于等于 1 且小于等于 3, `[0.5,2.5]` 表示数据需满足大于等于 0.5 且小于等于 2.5 |

> 📌 **提示**：左右小括号（`()`）表示大于/小于，左右中括号（`[]`）表示大于等于/小于等于。

#### 逻辑表达式

用于组合多个条件，包括 `||`（或）、`&&`（与）和 `!`（非）：

- `||` 或操作：`{==0||!=2}->{==1}` 表示数据先满足"等于 0 或不等于 2"，然后满足"等于 1"

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

- `&&` 且操作：`{!=0&&!=2}->{==1}` 表示数据先满足"不等于 0 且不等于 2"，然后满足"等于 1"

- `!` 非操作：主要用于不等于表达式（`!=`）中

### 链式表达式

链式表达式用于对多个数据的集合进行处理。例如：

```
filter((-5,5)).limit(5).take(0,2).sum().meet(>1)
```

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
  public class TemperatureFilterUDF implements FilterUDF {
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

- **自定义计算函数** 🧮

  除了使用内置的计算函数外，Rhythmix 还支持自定义计算器函数（CalculatorUDF），让您可以实现复杂的自定义计算逻辑。

  **创建自定义计算器**：

  实现 `CalculatorUDF` 接口来创建自定义计算器：

  ```java
  public class MyMaxCalculator implements CalculatorUDF {
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

          for (EventData eventData : values) {
              if (eventData == null || eventData.getValue() == null) {
                  continue;
              }

              try {
                  double num;
                  Object value = eventData.getValue();
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
  | `medianCalc()` | 📊 计算数据序列的中位数 | `filter(>0).medianCalc().meet(>5)` |
  | `maxCalc()` | 📈 找出数据序列的最大值 | `filter(>0).maxCalc().meet(>100)` |
  | `minCalc()` | 📉 找出数据序列的最小值 | `filter(>0).minCalc().meet(<10)` |
  | `myMax()` | 🔝 自定义最大值计算器 | `filter(>0).limit(5).myMax().meet(>10)` |

  **高级功能 - 复杂计算逻辑**：

  自定义计算器可以实现任意复杂的计算逻辑：

  ```java
  public class WeightedAverageCalculator implements CalculatorUDF {
      @Override
      public String getName() {
          return "weightedAvg";
      }

      @Override
      public Number calculate(List<EventData> values) {
          // 实现加权平均计算
          double totalWeight = 0;
          double weightedSum = 0;

          for (int i = 0; i < values.size(); i++) {
              EventData data = values.get(i);
              if (data != null && data.getValue() instanceof Number) {
                  double value = ((Number) data.getValue()).doubleValue();
                  double weight = i + 1; // 越新的数据权重越大

                  weightedSum += value * weight;
                  totalWeight += weight;
              }
          }

          return totalWeight > 0 ? weightedSum / totalWeight : 0;
      }
  }
  ```

  **使用示例**：

  ```js
  // 加权平均计算示例
  filter(>0).limit(10).weightedAvg().meet(>15)

  // 与其他函数组合使用
  filter([10,100]).window(5).weightedAvg().meet(>=30)
  ```

> 💡 **注意**:
> - 所有计算函数都会自动忽略空值
> - 对于非数值类型的数据将抛出计算异常
> - 标准差计算至少需要2个数据点
> - 当输入序列包含浮点数时,计算结果会自动转换为浮点数类型
> - **自定义计算器**会自动被系统发现和注册，无需手动注册
> - 计算器名称必须唯一，重复名称会导致注册失败
> - 自定义计算器应该处理异常情况，避免影响整个表达式的执行


---
