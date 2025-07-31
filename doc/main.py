import sys
from pathlib import Path
from typing import List

from manim import config

from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FORMAT, GIF_QUALITY, DEFAULT_FRAME_RATE
from src.string_movement_animation import StringMovementAnimation, StringPair

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
    # Demonstrate the {==0}->{==1} expression with various data sequences
    state_pairs = [
        # First sequence: 2 -> 0 (first state not satisfied, no transition)
        StringPair("2", "false", "2==0  ×", False),

        # Second sequence: 0 -> 2 (first state satisfied, second state not satisfied)
        StringPair("0", "false", "0==0  √", True),
        StringPair("2", "false", "2==1  ×", False),

        # Third sequence: 0 -> 1 (both states satisfied in sequence - SUCCESS!)
        StringPair("0", "false", "0==0  √", True),
        StringPair("1", "true", "1==1  √", True),

        # Fourth sequence: 1 -> 0 (wrong direction, expression resets)
        StringPair("1", "false", "1==0  ×", False),
        StringPair("0", "false", "0==1  ×", False),

        # Fifth sequence: 0 -> 0 -> 1 (first state satisfied, stays in first state, then transitions)
        StringPair("0", "false", "0==0  √", True),
        StringPair("0", "false", "0==0  √", True),
        StringPair("1", "true", "1==1  √", True)
    ]

    create_gif_animation(state_pairs, '{==0}->{==1}', 'state_transition_0_to_1', speed_multiplier=1.5)

if __name__ == "__main__":
    # Create the state transition demonstration
    create_state_transition_demo()

    # Optionally also create the original count example
    # main()
