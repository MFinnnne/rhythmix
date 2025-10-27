# Count Equal Chain Demo

## Expression
`{count!(==1,5)}->{==0}->{count!(==1,5)}`

## Description
This animation demonstrates a cyclic state machine expression that:

1. **{count!(==1,5)}** (State 0): Counts exactly 5 consecutive occurrences of value 1
2. **->**: Transitions to the next state when the count requirement is met
3. **{==0}** (State 1): Waits for a value that equals 0
4. **->**: Transitions back to State 0
5. **{count!(==1,5)}** (State 0): Repeats the counting cycle

## Key Features

### State Machine Behavior
- **State 0**: Counting consecutive 1's
  - Increments counter when value equals 1
  - Resets counter to 0 when value is not 1
  - Transitions to State 1 when count reaches 5/5
  
- **State 1**: Waiting for 0
  - Stays in State 1 for any non-zero value
  - Transitions back to State 0 when value equals 0
  - Restarts the counting cycle

### Visual Elements
- **Subtitle Display**: Shows current state information
  - "Count:X/5" when in counting state
  - "Waiting for 0" when in waiting state
  - "Got 0✓" when transition condition is met
  - "Count:5/5✓" when count requirement is satisfied

- **Expression Highlighting**: Visual indication of which state is active
  - State 0 highlights `{count!(==1,5)}`
  - State 1 highlights `{==0}`

- **Recording Area**: Shows history of evaluations with success/failure indicators

## Animation Sequence

The animation includes 30 state transitions demonstrating:

1. **First Cycle**:
   - Initial interruptions (values 2, 0) that reset the count
   - Successful count buildup: 1→1→1→1→1 (reaches 5/5)
   - Transition to waiting state
   - Values 1, 2 rejected while waiting
   - Value 0 triggers transition back to State 0

2. **Second Cycle**:
   - New count attempt with interruption at count 2/5 (value 3)
   - Successful count buildup: 1→1→1→1→1 (reaches 5/5)
   - Transition to waiting state
   - Values 5, 1 rejected while waiting
   - Value 0 triggers SUCCESS and transition

3. **Third Cycle**:
   - Demonstrates the cycle continues
   - Successful count buildup: 1→1→1→1→1 (reaches 5/5)
   - Value 0 completes the cycle

## Technical Details

### Function Implementation
- **File**: `doc/demos/chain_expression_demos.py`
- **Function**: `create_count_equal_chain_demo()`
- **State Pairs**: 30 StateTransitionPair objects
- **Animation Parameters**:
  - `speed_multiplier`: 1.5 (faster playback)
  - `width`: 1100 pixels
  - `height`: 450 pixels
  - `line_spacing`: 0.4
  - `column_spacing`: 3.0

### Expression Parts
```python
expression_parts = ["{count!(==1,5)}", "->", "{==0}", "->", "{count!(==1,5)}"]
```

### Output
- **File**: `doc/media/videos/450p30/count_equal_chain_demo.gif`
- **Format**: GIF animation
- **Total Animations**: 216 frames (including transitions)

## Usage

Run the demo using:
```bash
python run_demo.py count_equal_chain
```

Or directly:
```python
from demos.chain_expression_demos import create_count_equal_chain_demo
create_count_equal_chain_demo()
```

## Example Data Flow

```
Input: 2
State: 0 (Count:0/5)
Result: false (2≠1, RESET count to 0/5)

Input: 1
State: 0 (Count:1/5)
Result: false (1==1, Count: 1/5)

Input: 1
State: 0 (Count:2/5)
Result: false (1==1, Count: 2/5)

Input: 0
State: 0 (Count:0/5)
Result: false (0≠1, RESET count to 0/5)

Input: 1, 1, 1, 1, 1
State: 0 → 1 (Count:5/5✓)
Result: false → true (Count complete, transition to State 1)

Input: 1
State: 1 (Waiting for 0)
Result: false (1≠0, Stay in State 1)

Input: 0
State: 1 → 0 (Got 0✓)
Result: false → true (Transition back to State 0, RESTART)
```

## Related Demos
- `multi_count_state_transition`: Similar state machine with different count conditions
- `count_constraint`: Single count constraint demonstration
- `equal_chain_state_transition`: Simple equality-based state transitions

