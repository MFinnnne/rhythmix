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
    pair = [
        StringPair("1", "false", recording_tag=False, left_color="orange", right_color="red"),
        StringPair("5", "false", recording_tag=True, left_color="orange", right_color="red"),
        StringPair("2", "false", recording_tag=False, left_color="orange", right_color="red"),
        StringPair("6", "false", recording_tag=True, left_color="orange", right_color="red"),
        StringPair("2", "false", recording_tag=False, left_color="orange", right_color="red"),
        StringPair("6", "true", recording_tag=True, left_color="orange", right_color="green")
    ]
    create_gif_animation(pair, 'count(>4,1)', 'count1', speed_multiplier=2.0)  # Faster


if __name__ == "__main__":
    main()
