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
                               speed_multiplier=1.0):
    """
    Create a GIF animation with state transition highlighting.

    Args:
        pairs: List of StateTransitionPair objects for the animation
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
    class GifStateTransitionAnimation(StateTransitionAnimation):
        def __init__(self, **kwargs):
            super().__init__(state_pairs=pairs, expression_parts=["{==0}", "->", "{==1}"],
                           speed_multiplier=speed_multiplier, **kwargs)

    # Render the animation
    scene = GifStateTransitionAnimation()
    scene.render()

    print(f"✓ Enhanced State Transition GIF created: {output_name}.{DEFAULT_FORMAT}")
    print(f"✓ Quality: {config.quality}")
    return True

if __name__ == "__main__":
    # Create the state transition demonstration
    create_state_transition_demo()

    # Optionally also create the original count example
    # main()
