import sys
from pathlib import Path
from typing import List

from manim import config

from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FORMAT, GIF_QUALITY, DEFAULT_FRAME_RATE
from src.string_movement_animation import StringMovementAnimation, StringPair
from src.state_transition_animation import StateTransitionAnimation, StateTransitionPair

# Add the doc directory to Python path
doc_dir = Path(__file__).parent
sys.path.insert(0, str(doc_dir))

"""
Main entry point for generating documentation animations.
"""


def create_gif_animation(pairs: List[StringPair] = None, center_text: str = None, output_name="StringAnimation",
                         speed_multiplier=2.0):
    """
    Create a GIF animation with specified quality and custom output name.

    Args:
        pairs: List of StringPair objects for the animation
        center_text: The static center text
        output_name: Name for the output file (without extension)
        speed_multiplier: Speed control (1.0=normal, 0.5=half speed, 2.0=double speed)
    """

    # Set up manim config for GIF output
    if pairs is None:
        pairs = []
    config.pixel_width = DEFAULT_WIDTH
    config.pixel_height = DEFAULT_HEIGHT
    config.frame_rate = DEFAULT_FRAME_RATE
    config.format = DEFAULT_FORMAT
    config.quality = GIF_QUALITY

    # Set custom output filename
    config.output_file = output_name

    # Create animation class
    class GifStringAnimation(StringMovementAnimation):
        def __init__(self, **kwargs):
            super().__init__(string_pairs=pairs, center_text=center_text, speed_multiplier=speed_multiplier, **kwargs)

    # Render the animation
    scene = GifStringAnimation()
    scene.render()

    print(f"✓ GIF created: {output_name}.{DEFAULT_FORMAT}")
    print(f"✓ Quality: {config.quality}")
    return True


def main():
    # Original count example
    count_pairs = [
        StringPair("1", "false", "1>4   +0"),
        StringPair("5", "false", "5>4    +1", True),
        StringPair("2", "false", "2>4   +1"),
        StringPair("6", "false", "6>4    +2", True),
        StringPair("2", "false", "2>4   +2"),
        StringPair("6", "true", "6>4     +3", True)
    ]
    create_gif_animation(count_pairs, 'count(>4,3)', 'count1', speed_multiplier=2.0)  # Faster

def create_state_transition_demo():
    """
    Create a GIF demonstrating the {==0}->{==1} state transition expression.
    This shows how the expression detects a change from value 0 to value 1.
    """
    # Demonstrate the {==0}->{==1} expression with forward-only state progression
    # current_state represents the state AFTER processing each input
    state_pairs = [
        # Sequence 1: Input '2' (doesn't match first state {==0}, stays in state 0)
        StateTransitionPair("2", "false", "2≠0", False, current_state=0),

        # Sequence 2: Input '0' -> '2' (first state satisfied, moves to state 1, then stays in state 1)
        StateTransitionPair("0", "false", "0==0", True, current_state=1),
        StateTransitionPair("2", "false", "2≠1", False, current_state=1),

        # Sequence 3: Input '0' -> '1' (complete successful transition - SUCCESS!)
        StateTransitionPair("0", "false", "0==0", True, current_state=1),
        StateTransitionPair("1", "true", "1==1 SUCCESS!", True, current_state=0),  # Resets after success

        # Sequence 4: Input '1' (doesn't match first state, stays in state 0)
        StateTransitionPair("1", "false", "1≠0", False, current_state=0),

        # Sequence 5: Input '0' (matches first state, moves to state 1)
        StateTransitionPair("0", "false", "0==0", True, current_state=1),

        # Sequence 6: Input '0' -> '0' -> '1' (forward-only: moves to state 1, stays in state 1, then succeeds)
        StateTransitionPair("0", "false", "0!=1", False, current_state=1),
        StateTransitionPair("0", "false", "waiting for 1", False, current_state=1),
        StateTransitionPair("1", "true", "1==1 SUCCESS!", True, current_state=0)  # Resets after success
    ]

    create_state_transition_gif(state_pairs, 'state_transition_enhanced', speed_multiplier=1.5)


def create_state_transition_gif(pairs: List[StateTransitionPair] = None, output_name="StateTransitionAnimation",
                               speed_multiplier=1.0, expression_parts=None,
                               line_spacing=0.1, column_spacing=3.5):
    """
    Create a GIF animation with state transition highlighting.

    Args:
        pairs: List of StateTransitionPair objects for the animation
        output_name: Name for the output file (without extension)
        speed_multiplier: Speed control (1.0=normal, 0.5=half speed, 2.0=double speed)
        expression_parts: List of expression parts (defaults to ["{==0}", "->", "{==1}"])
        line_spacing: Spacing between wrapped lines within a single text entry (default: 0.1)
        column_spacing: Width spacing between columns in multi-column layout (default: 3.5)
    """
    # Set up manim config for GIF output
    if pairs is None:
        pairs = []
    if expression_parts is None:
        expression_parts = ["{==0}", "->", "{==1}"]

    config.pixel_width = DEFAULT_WIDTH
    config.pixel_height = DEFAULT_HEIGHT
    config.frame_rate = DEFAULT_FRAME_RATE
    config.format = DEFAULT_FORMAT
    config.quality = GIF_QUALITY

    # Set custom output filename
    config.output_file = output_name

    # Create animation class
    class GifStateTransitionAnimation(StateTransitionAnimation):
        def __init__(self, **kwargs):
            super().__init__(state_pairs=pairs, expression_parts=expression_parts,
                           speed_multiplier=speed_multiplier, line_spacing=line_spacing,
                           column_spacing=column_spacing, **kwargs)

    # Render the animation
    scene = GifStateTransitionAnimation()
    scene.render()

    print(f"✓ Enhanced State Transition GIF created: {output_name}.{DEFAULT_FORMAT}")
    print(f"✓ Quality: {config.quality}")
    return True

def create_complex_state_transition_demo():
    """
    Create a GIF demonstrating the {>1}->{count(<1,3)}->{==3} state transition expression.
    This shows a complex multi-state transition with counting logic.

    States:
    0: Waiting for {>1} (value greater than 1)
    1: Waiting for {count(<1,3)} (counting values less than 1, need exactly 3)
    2: Waiting for {==3} (value equal to 3)
    """
    # Demonstrate the {>1}->{count(<1,3)}->{==3} expression
    # current_state: 0=waiting for >1, 1=counting <1 values (need 3), 2=waiting for ==3
    state_pairs = [
        # Sequence 1: Input '0' (doesn't match first state {>1}, stays in state 0)
        StateTransitionPair("0", "false", "0≤1", False, current_state=0),

        # Sequence 2: Input '2' (matches {>1}, moves to state 1 - start counting)
        StateTransitionPair("2", "false", "2>1 ✓", True, current_state=1),

        # Sequence 3: Count values < 1 (need 3 total)
        StateTransitionPair("0", "false", "0<1 (1/3)", True, current_state=1),
        StateTransitionPair("-1", "false", "-1<1 (2/3)", True, current_state=1),
        StateTransitionPair("0.5", "false", "0.5<1 (3/3)", True, current_state=2),  # Count complete, move to state 2

        # Sequence 4: Input '2' (doesn't match {==3}, stays in state 2)
        StateTransitionPair("2", "false", "2≠3", False, current_state=2),

        # Sequence 5: Input '3' (matches {==3} - SUCCESS!)
        StateTransitionPair("3", "true", "3==3 SUCCESS!", True, current_state=0),  # Reset after success

        # Sequence 6: Show another complete cycle
        StateTransitionPair("5", "false", "5>1 ✓", True, current_state=1),  # Start new cycle
        StateTransitionPair("0", "false", "0<1 (1/3)", True, current_state=1),
        StateTransitionPair("-0.5", "false", "-0.5<1 (2/3)", True, current_state=1),
        StateTransitionPair("0.1", "false", "0.1<1 (3/3)", True, current_state=2),
        StateTransitionPair("3", "true", "3==3 SUCCESS!", True, current_state=0),  # Complete success

        # Sequence 7: Show failure case - wrong count
        StateTransitionPair("10", "false", "10>1 ✓", True, current_state=1),
        StateTransitionPair("0", "false", "0<1 (1/3)", True, current_state=1),
        StateTransitionPair("2", "false", "2≥1, reset", False, current_state=0),  # Reset on invalid input
    ]

    create_state_transition_gif(state_pairs, 'complex_state_transition', speed_multiplier=1.2,
                                expression_parts=["{>1}", "->", "{count(<1,3)}", "->", "{==3}"])



def create_equal_chain_state_transition_demo():
    """
    Create a GIF demonstrating the {==0}=>{==1}=>{==0} state transition expression.
    This shows a three-state transition: wait for 0, then 1, then 0 (success), then reset.

    States:
    0: Waiting for {==0}
    1: Waiting for {==1}
    2: Waiting for {==0}
    """
    # Demonstration sequences for {==0}=>{==1}=>{==0}
    # current_state denotes the state AFTER processing the input
    state_pairs = [
        # Start: state 0 expects 0
        StateTransitionPair("2", "false", "2≠0", False, current_state=0),
        StateTransitionPair("0", "false", "0==0 ✓", True, current_state=1),

        # Now in state 1 expects 1
        StateTransitionPair("2", "false", "2≠1", False, current_state=1),
        StateTransitionPair("1", "false", "1==1 ✓", True, current_state=2),

        # Now in state 2 expects 0
        StateTransitionPair("3", "false", "3≠0", False, current_state=2),
        StateTransitionPair("0", "true", "0==0 SUCCESS!", True, current_state=0),  # Reset after success

        # Show a full quick successful cycle
        StateTransitionPair("0", "false", "0==0 ✓", True, current_state=1),
        StateTransitionPair("1", "false", "1==1 ✓", True, current_state=2),
        StateTransitionPair("0", "true", "0==0 SUCCESS!", True, current_state=0),

        # Failure example in state 1, stays awaiting 1
        StateTransitionPair("0", "false", "0==0 ✓ (back to state 1)", True, current_state=1),
        StateTransitionPair("0", "false", "0≠1 (still waiting for 1)", False, current_state=1),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='equal_chain_state_transition',
        speed_multiplier=1.2,
        expression_parts=["{==0}", "=>", "{==1}", "=>", "{==0}"]
    )


def create_count_constraint_demo():
    """
    Create a GIF demonstrating the count!([10,20], 5) constraint expression.
    This shows a count constraint that requires exactly 5 CONSECUTIVE values within the range [10,20].

    Count constraint logic (RESET-ON-BREAK behavior):
    - count!([10,20], 5): Collect exactly 5 consecutive values that fall within [10,20]
    - Values outside [10,20] RESET the counter back to 0 (not ignored!)
    - Must have 5 valid values in a row without any breaks
    - Once 5 consecutive valid values are collected, constraint is satisfied
    - Expression completes and resets for the next cycle

    Range notation:
    - [10,20]: closed interval, both 10 and 20 are included (10 ≤ x ≤ 20)

    Key difference: This is CONTINUOUS counting, not cumulative counting!
    """
    # Demonstration sequences for count!([10,20], 5) with RESET-ON-BREAK behavior
    # current_state tracks progress: 0=need 5, 1=need 4, 2=need 3, 3=need 2, 4=need 1, 5=complete
    # IMPORTANT: Invalid values RESET the counter back to 0, not just ignored!
    state_pairs = [
        # Sequence 1: Values outside range (reset counter)
        StateTransitionPair("5", "false", "5∉[10,20]|RESET counter", False, current_state=0),
        StateTransitionPair("25", "false", "25∉[10,20]|RESET counter", False, current_state=0),

        # Sequence 2: Start building count
        StateTransitionPair("15", "false", "15∈[10,20]|count: 1/5", True, current_state=1),
        StateTransitionPair("12", "false", "12∈[10,20]|count: 2/5", True, current_state=2),

        # Sequence 3: Break in sequence - RESET!
        StateTransitionPair("3", "false", "3∉[10,20]|RESET to 0/5", False, current_state=0),

        # Sequence 4: Start over after reset
        StateTransitionPair("18", "false", "18∈[10,20]|count: 1/5", True, current_state=1),
        StateTransitionPair("14", "false", "14∈[10,20]|count: 2/5", True, current_state=2),
        StateTransitionPair("16", "false", "16∈[10,20]|count: 3/5", True, current_state=3),

        # Sequence 5: Another break - RESET again!
        StateTransitionPair("30", "false", "30∉[10,20]|RESET to 0/5", False, current_state=0),

        # Sequence 6: Continuous sequence to completion
        StateTransitionPair("10", "false", "10∈[10,20]|count: 1/5", True, current_state=1),
        StateTransitionPair("11", "false", "11∈[10,20]|count: 2/5", True, current_state=2),
        StateTransitionPair("12", "false", "12∈[10,20]|count: 3/5", True, current_state=3),
        StateTransitionPair("13", "false", "13∈[10,20]|count: 4/5", True, current_state=4),
        StateTransitionPair("14", "true", "14∈[10,20]|count: 5/5 Completed!", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='count_constraint_demo',
        speed_multiplier=2.0,
        expression_parts=["count!([10,20], 5)"],
        line_spacing=0.4,  # Compact spacing for count expressions
        column_spacing=4.0  # Good spacing for count notation
    )


def create_logical_and_state_transition_demo():
    """
    Create a GIF demonstrating the {!=0&&!=2}->{==1} state transition expression.
    This shows a two-state transition with logical AND conditions using inequality and equality operators.

    States:
    0: Waiting for {!=0&&!=2} (value not equal to 0 AND not equal to 2)
    1: Waiting for {==1} (value equal to 1)

    Logical operators:
    - != : not equal to
    - && : logical AND (both conditions must be true)
    - == : equal to
    """
    # Demonstration sequences for {!=0&&!=2}->{==1}
    # current_state denotes the state AFTER processing the input
    state_pairs = [
        # Sequence 1: Input '0' (fails first condition !=0, doesn't satisfy {!=0&&!=2})
        StateTransitionPair("0", "false", "0==0|first condition fails", False, current_state=0),

        # Sequence 2: Input '2' (fails second condition !=2, doesn't satisfy {!=0&&!=2})
        StateTransitionPair("2", "false", "2==2|second condition fails", False, current_state=0),

        # Sequence 3: Input '1' (satisfies both !=0 and !=2, moves to state 1)
        StateTransitionPair("1", "false", "1≠0&&1≠2|both conditions true", True, current_state=1),

        # Sequence 4: Input '3' (doesn't match {==1}, stays in state 1)
        StateTransitionPair("3", "false", "3≠1|wrong value", False, current_state=1),

        # Sequence 5: Input '0' (doesn't match {==1}, stays in state 1)
        StateTransitionPair("0", "false", "0≠1|wrong value", False, current_state=1),

        # Sequence 6: Input '1' (matches {==1} - SUCCESS!)
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),

        # Sequence 7: Show another complete successful cycle
        StateTransitionPair("3", "false", "3≠0&&3≠2|both conditions true", True, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),

        # Sequence 8: Show edge case - negative numbers
        StateTransitionPair("-1", "false", "-1≠0&&-1≠2|both conditions true", True, current_state=1),
        StateTransitionPair("2", "false", "2≠1|wrong value", False, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),

        # Sequence 9: Show multiple failures in first state
        StateTransitionPair("0", "false", "0==0|first condition fails", False, current_state=0),
        StateTransitionPair("2", "false", "2==2|second condition fails", False, current_state=0),
        StateTransitionPair("5", "false", "5≠0&&5≠2|both conditions true", True, current_state=1),

        # Sequence 10: Show state persistence and final success
        StateTransitionPair("4", "false", "4≠1|wrong value", False, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='logical_and_state_transition',
        speed_multiplier=1.0,
        expression_parts=["{!=0&&!=2}", "->", "{==1}"],
        line_spacing=0.4,  # Standard spacing for logical expressions
        column_spacing=4.0  # Good spacing for logical notation
    )


def create_interval_state_transition_demo():
    """
    Create a GIF demonstrating the {(1,3]}->{[4,7)} state transition expression.
    This shows a two-state transition with interval conditions using mathematical interval notation.

    States:
    0: Waiting for {(1,3]} (value in interval (1,3], i.e., 1 < x ≤ 3)
    1: Waiting for {[4,7)} (value in interval [4,7), i.e., 4 ≤ x < 7)

    Interval notation:
    - (1,3]: open at 1 (1 not included), closed at 3 (3 included)
    - [4,7): closed at 4 (4 included), open at 7 (7 not included)
    """
    # Demonstration sequences for {(1,3]}->{[4,7)}
    # current_state denotes the state AFTER processing the input
    state_pairs = [
        # Sequence 1: Input '1.0' (doesn't match (1,3] - boundary case, 1 not included)
        StateTransitionPair("1.0", "false", "1.0∉(1,3]|1 not included", False, current_state=0),

        # Sequence 2: Input '0.5' (doesn't match (1,3] - too small)
        StateTransitionPair("0.5", "false", "0.5∉(1,3]|too small", False, current_state=0),

        # Sequence 3: Input '1.5' (matches (1,3], moves to state 1)
        StateTransitionPair("1.5", "false", "1.5∈(1,3]|1<1.5≤3", True, current_state=1),

        # Sequence 4: Input '3.5' (doesn't match [4,7), too small for second interval)
        StateTransitionPair("3.5", "false", "3.5∉[4,7)|too small", False, current_state=1),

        # Sequence 5: Input '7.0' (doesn't match [4,7) - boundary case, 7 not included)
        StateTransitionPair("7.0", "false", "7.0∉[4,7)|7 not included", False, current_state=1),

        # Sequence 6: Input '5.0' (matches [4,7) - SUCCESS!)
        StateTransitionPair("5.0", "true", "5.0∈[4,7]|SUCCESS", True, current_state=0),

        # Sequence 7: Show another complete successful cycle with boundary values
        StateTransitionPair("3.0", "false", "3.0∈(1,3]|boundary case", True, current_state=1),
        StateTransitionPair("4.0", "true", "4.0∈[4,7)|SUCCESS", True, current_state=0),

        # Sequence 8: Show edge case - values just outside intervals
        StateTransitionPair("4.0", "false", "4.0∉(1,3]|too large", False, current_state=0),
        StateTransitionPair("2.5", "false", "2.5∈(1,3]|1<2.5≤3", True, current_state=1),
        StateTransitionPair("8.0", "false", "8.0∉[4,7)|too large", False, current_state=1),

        # Sequence 9: Show successful completion with mid-range values
        StateTransitionPair("2.0", "false", "2.0∈(1,3]|restart", True, current_state=1),
        StateTransitionPair("6.5", "true", "6.5∈[4,7)|SUCCESS", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='interval_state_transition',
        speed_multiplier=1.0,
        expression_parts=["{(1,3]}", "->", "{[4,7)}"],
        line_spacing=0.4,  # Larger spacing for complex interval notation
        column_spacing=4.5  # Wider columns for mathematical notation
    )


def create_multi_count_state_transition_demo():
    """
    Create a GIF demonstrating the {==0}->{count!(>4, 3)}->{count!(<2, 2)} state transition expression.
    This shows a complex three-state transition with two sequential count constraints.

    States:
    0: Waiting for {==0} (value equal to 0)
    1: Waiting for {count!(>4, 3)} (3 consecutive values > 4, resets on break)
    2: Waiting for {count!(<2, 2)} (2 consecutive values < 2, resets on break)

    Count constraint behavior:
    - count!(>4, 3): Must collect exactly 3 consecutive values > 4
    - count!(<2, 2): Must collect exactly 2 consecutive values < 2
    - Any value that doesn't meet the condition resets the counter to 0
    - Must achieve the full count without breaks to advance to next state
    """
    # Demonstration sequences for {==0}->{count!(>4, 3)}->{count!(<2, 2)}
    # current_state denotes the state AFTER processing the input
    state_pairs = [
        # Sequence 1: Start - need to find {==0} first
        StateTransitionPair("5", "false", "5≠0|Stay in State 0", False, current_state=0),
        StateTransitionPair("2", "false", "2≠0|Stay in State 0", False, current_state=0),
        StateTransitionPair("0", "false", "0==0|To State 1", True, current_state=1),

        # Sequence 2: Now in State 1 - need count!(>4, 3)
        StateTransitionPair("6", "false", "6>4|Count: 1/3", True, current_state=1),
        StateTransitionPair("7", "false", "7>4|Count: 2/3", True, current_state=1),
        StateTransitionPair("3", "false", "3≤4|RESET to 0/3", False, current_state=1),

        # Sequence 3: Try again for count!(>4, 3)
        StateTransitionPair("8", "false", "8>4|Count: 1/3", True, current_state=1),
        StateTransitionPair("5", "false", "5>4|Count: 2/3", True, current_state=1),
        StateTransitionPair("9", "false", "9>4|Count: 3/3 ,to State 2", True, current_state=2),

        # Sequence 4: Now in State 2 - need count!(<2, 2)
        StateTransitionPair("1", "false", "1<2|Count: 1/2", True, current_state=2),
        StateTransitionPair("3", "false", "3≥2|RESET to 0/2", False, current_state=2),

        # Sequence 5: Try again for count!(<2, 2)
        StateTransitionPair("0", "false", "0<2|Count: 1/2", True, current_state=2),
        StateTransitionPair("1", "true", "1<2|Count: 2/2|SUCCESS!", True, current_state=0),  # Complete and reset

        # Sequence 6: Expression completed, back to State 0
        StateTransitionPair("4", "false", "4≠0|Stay in State 0", False, current_state=0),
    ]

    # Create the animation with expression parts for display
    expression_parts = [
        "{==0}",
        "->",
        "{count!(>4, 3)}",
        "->",
        "{count!(<2, 2)}"
    ]

    return create_state_transition_gif(
        pairs=state_pairs,
        output_name="multi_count_state_transition",
        speed_multiplier=2,
        expression_parts=expression_parts,
        line_spacing=0.5,
        column_spacing= 4
    )


def create_range_state_transition_demo():
    """
    Create a GIF demonstrating the {>=0.5}->{<=0.8} state transition expression.
    This shows a two-state transition with range conditions: wait for value >= 0.5, then <= 0.8.

    States:
    0: Waiting for {>=0.5} (value greater than or equal to 0.5)
    1: Waiting for {<=0.8} (value less than or equal to 0.8)
    """
    # Demonstration sequences for {>=0.5}->{<=0.8}
    # current_state denotes the state AFTER processing the input
    state_pairs = [
        # Sequence 1: Input '0.3' (doesn't match first state {>=0.5}, stays in state 0)
        StateTransitionPair("0.3", "false", "0.3<0.5", False, current_state=0),

        # Sequence 2: Input '0.2' (still doesn't match first state)
        StateTransitionPair("0.2", "false", "0.2<0.5", False, current_state=0),

        # Sequence 3: Input '0.5' (matches {>=0.5}, moves to state 1)
        StateTransitionPair("0.5", "false", "0.5≥0.5", True, current_state=1),

        # Sequence 4: Input '0.9' (doesn't match {<=0.8}, stays in state 1)
        StateTransitionPair("0.9", "false", "0.9>0.8", False, current_state=1),

        # Sequence 5: Input '1.2' (still doesn't match {<=0.8})
        StateTransitionPair("1.2", "false", "1.2>0.8", False, current_state=1),

        # Sequence 6: Input '0.8' (matches {<=0.8} - SUCCESS!)
        StateTransitionPair("0.8", "true", "0.8≤0.8|completed", True, current_state=0),

        # Sequence 7: Show another complete successful cycle
        StateTransitionPair("0.7", "false", "0.7≥0.5|restart", True, current_state=1),
        StateTransitionPair("0.6", "true", "0.6≤0.8|completed", True, current_state=0),

        # Sequence 8: Show edge case - exact boundary values
        StateTransitionPair("0.5", "false", "0.5≥0.5|restart", True, current_state=1),
        StateTransitionPair("0.8", "true", "0.8≤0.8|completed", True, current_state=0),

        # Sequence 9: Show failure case - value too high for first state
        StateTransitionPair("2.0", "false", "2.0≥0.5|restart", True, current_state=1),
        StateTransitionPair("1.5", "false", "1.5>0.8", False, current_state=1),

        # Sequence 10: Show state persistence - stays in state 1 until valid input
        StateTransitionPair("0.95", "false", "0.95>0.8", False, current_state=1),
        StateTransitionPair("0.75", "true", "0.75≤0.8|completed", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='range_state_transition',
        speed_multiplier=1.1,
        expression_parts=["{>=0.5}", "->", "{<=0.8}"],
        line_spacing=0.5,  # Slightly larger spacing between wrapped lines
        column_spacing=3.0  # Wider columns for better text visibility
    )


if __name__ == "__main__":
    # Create the multi-count state transition demonstration ({==0}->{count!(>4, 3)}->{count!(<2, 2)})
    create_multi_count_state_transition_demo()

    # Create the count constraint demonstration (count!([10,20], 5))
    # create_count_constraint_demo()

    # Create the logical AND state transition demonstration ({!=0&&!=2}->{==1})
    # create_logical_and_state_transition_demo()

    # Create the interval state transition demonstration ({(1,3]}->{[4,7)})
    # create_interval_state_transition_demo()

    # Create the range state transition demonstration ({>=0.5}->{<=0.8})
    # create_range_state_transition_demo()

    # Create the equal chain state transition demonstration ({==0}=>{==1}=>{==0})
    # create_equal_chain_state_transition_demo()

    # Create the complex state transition demonstration
    # create_complex_state_transition_demo()

    # Create the basic state transition demonstration
    # create_state_transition_demo()

    # Optionally also create the original count example
    # create_equal_chain_state_transition_demo()
    # main()
