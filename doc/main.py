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
                               speed_multiplier=1.0, expression_parts=None):
    """
    Create a GIF animation with state transition highlighting.

    Args:
        pairs: List of StateTransitionPair objects for the animation
        output_name: Name for the output file (without extension)
        speed_multiplier: Speed control (1.0=normal, 0.5=half speed, 2.0=double speed)
        expression_parts: List of expression parts (defaults to ["{==0}", "->", "{==1}"])
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
                           speed_multiplier=speed_multiplier, **kwargs)

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


if __name__ == "__main__":
    # Create the complex state transition demonstration
    create_complex_state_transition_demo()

    # Create the basic state transition demonstration
    create_state_transition_demo()

    # Optionally also create the original count example
    # main()
