import sys
from pathlib import Path
from typing import List
from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_DURATION, COLORS

from manim import config, RIGHT
from manim import FadeIn, FadeOut, Transform, LEFT, DOWN

from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FORMAT, GIF_QUALITY, DEFAULT_FRAME_RATE
from src.string_movement_animation import StringMovementAnimation, StringPair
from src.state_transition_animation import StateTransitionAnimation, StateTransitionPair

from demos.chain_expression_demos import create_chain_expression_demo_with_queue_line
from demos.count_demos import create_count_constraint_demo, create_multi_count_state_transition_demo, create_count_demo
from demos.state_transition_demos import create_state_transition_demo, create_complex_state_transition_demo, \
    create_equal_chain_state_transition_demo, create_logical_and_state_transition_demo, \
    create_interval_state_transition_demo, create_range_state_transition_demo

# Add the doc directory to Python path
doc_dir = Path(__file__).parent
sys.path.insert(0, str(doc_dir))

"""
Main entry point for generating documentation animations.
"""


def create_gif_animation(pairs: List[StringPair] = None, center_text: str = None, output_name="StringAnimation",
                         speed_multiplier=2.0, width: int = None, height: int = None):
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
    # 配置输出参数：若传入自定义宽高，则不使用质量预设（避免被强制改为 1920x1080）
    config.format = DEFAULT_FORMAT
    config.frame_rate = DEFAULT_FRAME_RATE
    if width is not None or height is not None:
        config.pixel_width = width if width is not None else DEFAULT_WIDTH
        config.pixel_height = height if height is not None else DEFAULT_HEIGHT
    else:
        config.quality = GIF_QUALITY
        config.pixel_width = DEFAULT_WIDTH
        config.pixel_height = DEFAULT_HEIGHT

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


if __name__ == "__main__":
    create_chain_expression_demo_with_queue_line()
