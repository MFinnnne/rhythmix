# Filter UDF Implementation with Auto-Import

This document describes the implementation of Filter UDF (User-Defined Function) functionality in Rhythmix, including the advanced auto-import feature that automatically discovers and registers FilterUDF implementations at runtime.

## Overview

The Filter UDF feature allows users to create custom Java-based filter functions that determine whether to keep or discard EventData objects in Rhythmix expressions. The implementation includes:

1. **Manual Registration**: Explicit FilterUDF registration through Compiler methods
2. **Auto-Import**: Automatic discovery and registration of FilterUDF implementations at startup
3. **AviatorScript Integration**: Direct integration with AviatorScript's instance function mechanism
4. **Full Backward Compatibility**: Seamless coexistence with existing comparison expressions

## Implementation Components

### 1. FilterUDF Interface

**Location**: `src/main/java/com/df/rhythmix/udf/FilterUDF.java`

```java
public interface FilterUDF {
    /**
     * Returns a unique name for the filter UDF that users can reference in expressions
     */
    String getName();
    
    /**
     * Determines whether to keep or discard the given EventData
     * @param event The EventData object to be filtered
     * @return true to keep the data, false to discard it
     */
    boolean filter(EventData event);
}
```

### 2. FilterUDFRegistry with Auto-Import

**Location**: `src/main/java/com/df/rhythmix/udf/FilterUDFRegistry.java`

**Key Features**:
- **Classpath Scanning**: Uses Hutool's `ClassUtil.scanPackageBySuper()` to find all FilterUDF implementations
- **Automatic Instantiation**: Creates instances of FilterUDF classes with default constructors
- **AviatorScript Integration**: Registers FilterUDFs using `AviatorEvaluator.addInstanceFunctions()`
- **Thread Safety**: Ensures auto-import runs only once with proper synchronization
- **Error Handling**: Gracefully handles instantiation failures and duplicate names
- **Manual Registration**: Supports additional manual FilterUDF registration

**Auto-Import Process**:
```java
public static void autoImportFilterUDFs() {
    // Thread-safe singleton pattern
    if (autoImportCompleted) return;

    synchronized (IMPORT_LOCK) {
        // Scan entire classpath for FilterUDF implementations
        Set<Class<?>> filterUDFClasses = ClassUtil.scanPackageBySuper("", FilterUDF.class);

        for (Class<?> clazz : filterUDFClasses) {
            // Skip interfaces and abstract classes
            // Instantiate FilterUDF
            FilterUDF filterUDF = (FilterUDF) clazz.getDeclaredConstructor().newInstance();

            // Register with AviatorScript
            AviatorEvaluator.addInstanceFunctions(filterUDF.getName(), filterUDF);
        }
    }
}
```

### 3. Enhanced System Registration

**Location**: `src/main/java/com/df/rhythmix/lib/Register.java`

**Integration**:
```java
public static void importFunction() {
    try {
        // Import existing static function classes
        AviatorEvaluator.importFunctions(Time.class);
        AviatorEvaluator.importFunctions(AviatorQueue.class);
        AviatorEvaluator.importFunctions(AviatorMath.class);

        // Auto-import FilterUDF instances
        FilterUDFRegistry.autoImportFilterUDFs();

    } catch (IllegalAccessException | NoSuchMethodException e) {
        throw new RuntimeException(e);
    }
}
```

### 4. Enhanced Filter Translator

**Location**: `src/main/java/com/df/rhythmix/translate/chain/Filter.java`

**Key Features**:
- **AST-Based UDF Detection**: Analyzes the Abstract Syntax Tree to identify filter function calls
- **Name Matching Logic**: Determines if parsed function names correspond to registered FilterUDFs
- **Dual Code Path Generation**: Generates appropriate code for both UDF execution and traditional comparison expressions

**Implementation Details**:
```java
private static boolean isFilterUDFCall(ASTNode state, EnvProxy env) {
    // Check if it's a variable with function call syntax (has CALL_STMT child)
    if (state.getType() == ASTNodeTypes.VARIABLE && 
        !state.getChildren().isEmpty() && 
        state.getChildren().get(0).getType() == ASTNodeTypes.CALL_STMT) {
        
        String functionName = state.getLabel();
        
        // Check if this function name corresponds to a registered FilterUDF
        Object udfObject = env.rawGet(functionName);
        return udfObject instanceof FilterUDF;
    }
    return false;
}
```

### 3. Enhanced Pebble Template

**Location**: `src/main/resources/expr/chain/filter.peb`

**Template Logic**:
```pebble
let chain_{{ funcName }} = lambda()->
    {% if isUDF is not null and isUDF == true %}
    ## UDF filter logic
    let res = {{ udfName }}.filter(event);
    {% else %}
    ## Traditional comparison expression logic
    let res = {{ stateCode }};
    {% endif %}
    if(res){
        {{ debug("filter() 接收数据：{}","event.value") }}
        return true;
    }
    {% if strict is not null and  strict == true %}
    else {
        {% var chainResult  %} = false;
        queue.clear({% var rawChainQueue %});
        {% if processedChainQueue != nil %}
        queue.clear({% var processedChainQueue %});
        {% endif %}
    }
    {% endif %}
    return false;
end;
```

### 4. Enhanced Compiler

**Location**: `src/main/java/com/df/rhythmix/execute/Compiler.java`

**New Methods**:
```java
// Compile with filter UDFs only
public static Executor compile(String code, Map<String, FilterUDF> filterUDFs)

// Compile with both regular UDFs and filter UDFs
public static Executor compile(String code, HashMap<String,Object> udfEnv, Map<String, FilterUDF> filterUDFs)
```

## Usage Examples

### Auto-Import Usage (Recommended)

With auto-import, FilterUDFs are automatically available without manual registration:

```java
// 1. Create a FilterUDF implementation anywhere in your classpath
package com.mycompany.filters;

public class TemperatureFilterUDF implements FilterUDF {
    @Override
    public String getName() {
        return "tempFilter";
    }

    @Override
    public boolean filter(EventData event) {
        try {
            double temp = Double.parseDouble(event.getValue());
            return temp >= 20.0 && temp <= 80.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

// 2. Initialize the system (triggers auto-import)
Register.importFunction();

// 3. Use directly in expressions - no manual registration needed!
String code = "filter(tempFilter()).collect().sum().meet(>100)";
Executor executor = Compiler.compile(code);

// 4. Execute with EventData
EventData event = new EventData("sensor1", "temp", "25.5", timestamp, EventValueType.FLOAT);
boolean result = executor.execute(event);
```

### Built-in Auto-Imported FilterUDFs

The system includes several built-in FilterUDFs that are automatically available:

- **`tempFilter()`**: Keeps temperatures between -50°C and 100°C
- **`numericFilter()`**: Keeps only numeric values (filters out non-numeric strings)
- **`positiveFilter()`**: Keeps only positive numeric values

```java
// Use built-in filters without any setup
String code1 = "filter(numericFilter()).collect().count().meet(>5)";
String code2 = "filter(positiveFilter()).limit(10).avg().meet([20,30])";
String code3 = "filter(tempFilter()).window(100).stddev().meet(<5.0)";
```

### Manual Registration (Legacy/Additional)

For additional FilterUDFs or when you need more control:

```java
// 1. Create a custom filter UDF
class TemperatureFilterUDF implements FilterUDF {
    @Override
    public String getName() {
        return "tempFilter";
    }

    @Override
    public boolean filter(EventData event) {
        try {
            double temp = Double.parseDouble(event.getValue());
            return temp >= 20.0 && temp <= 80.0;
        } catch (NumberFormatException e) {
            return false; // Discard invalid data
        }
    }
}

// 2. Register the UDF
Map<String, FilterUDF> filterUDFs = new HashMap<>();
filterUDFs.put("tempFilter", new TemperatureFilterUDF());

// 3. Use in expressions
String code = "filter(tempFilter()).collect().sum().meet(>100)";
Executor executor = Compiler.compile(code, filterUDFs);

// 4. Execute with EventData
EventData event = new EventData("sensor1", "temp", "25.5", timestamp, EventValueType.FLOAT);
boolean result = executor.execute(event);
```

### Advanced Usage

```java
// Combine with regular UDFs
HashMap<String, Object> udfEnv = new HashMap<>();
udfEnv.put("threshold", 50.0);

Map<String, FilterUDF> filterUDFs = new HashMap<>();
filterUDFs.put("customFilter", new MyCustomFilter());

String code = "filter(customFilter()).limit(10).avg().meet(>threshold)";
Executor executor = Compiler.compile(code, udfEnv, filterUDFs);
```

## Key Features

### 1. AST-Based Detection
- Analyzes the Abstract Syntax Tree to distinguish between UDF calls and comparison expressions
- Checks for VARIABLE nodes with CALL_STMT children
- Validates against registered FilterUDF instances in the environment

### 2. Seamless Integration
- Works with existing chain expressions (`filter().collect().sum().meet()`)
- Maintains backward compatibility with traditional comparison operators
- Integrates with the existing UDF registration mechanism

### 3. Template Modification Constraint Compliance
- **No new template files created** - only modified existing `filter.peb`
- Uses conditional logic in the template to handle both UDF and comparison cases
- Maintains the same template structure and variable names

### 4. Type Safety
- Direct Java execution without interpretation overhead
- Proper EventData object handling
- Exception-safe filter execution (returns false for errors)

## Testing

### Test Cases Included

1. **Simple Temperature Filter**: Tests basic UDF functionality
2. **Sensor ID Filter**: Tests string-based filtering logic
3. **Backward Compatibility**: Ensures traditional expressions still work
4. **Combined UDF Usage**: Tests integration with regular UDFs

### Example Test

```java
@Test
void testSimpleTemperatureFilterUDF() throws TranslatorException {
    Map<String, FilterUDF> filterUDFs = new HashMap<>();
    filterUDFs.put("tempFilter", new TemperatureFilterUDF());
    
    String code = "filter(tempFilter()).collect().count().meet(==2)";
    Executor executor = Compiler.compile(code, filterUDFs);
    
    // Test with temperature data...
    // Assertions verify correct filtering behavior
}
```

## Architecture Benefits

1. **Performance**: Direct Java execution, no interpretation overhead
2. **Flexibility**: Full access to EventData properties (id, name, value, timestamp, type)
3. **Safety**: Exception handling prevents filter failures from breaking execution
4. **Maintainability**: Clean separation between UDF and comparison logic
5. **Extensibility**: Easy to add new filter types without modifying core logic

## Integration Points

The implementation integrates with existing Rhythmix components:

- **ChainExpr**: Recognizes filter UDF calls in chain expressions
- **EnvProxy**: Manages UDF registration and lookup
- **AviatorScript**: Executes generated filter code
- **Pebble Templates**: Generates appropriate code for both UDF and comparison cases

## Backward Compatibility

All existing filter expressions continue to work unchanged:
- `filter(>20)` - comparison expressions
- `filter(>=10.5)` - numeric comparisons  
- `filter(=="active")` - string comparisons
- `filter([10,20])` - range expressions

The implementation automatically detects the expression type and generates appropriate code.
