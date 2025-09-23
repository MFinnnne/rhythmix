import sys
from pathlib import Path
from typing import List

from manim import config

doc_dir = Path(__file__).parent.parent
sys.path.insert(0, str(doc_dir))

from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FORMAT, GIF_QUALITY, DEFAULT_FRAME_RATE
from src.string_movement_animation import StringMovementAnimation, StringPair
from src.state_transition_animation import StateTransitionAnimation, StateTransitionPair


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

    if pairs is None:
        pairs = []
    config.format = DEFAULT_FORMAT
    config.frame_rate = DEFAULT_FRAME_RATE
    if width is not None or height is not None:
        config.pixel_width = width if width is not None else DEFAULT_WIDTH
        config.pixel_height = height if height is not None else DEFAULT_HEIGHT
    else:
        config.quality = GIF_QUALITY
        config.pixel_width = DEFAULT_WIDTH
        config.pixel_height = DEFAULT_HEIGHT

    config.output_file = output_name

    class GifStringAnimation(StringMovementAnimation):
        def __init__(self, **kwargs):
            super().__init__(string_pairs=pairs, center_text=center_text, speed_multiplier=speed_multiplier, **kwargs)

    scene = GifStringAnimation()
    scene.render()

    print(f"✓ GIF created: {output_name}.{DEFAULT_FORMAT}")
    print(f"✓ Quality: {config.quality}")
    return True


def create_state_transition_gif(pairs: List[StateTransitionPair] = None, output_name="StateTransitionAnimation",
                                speed_multiplier=1.0, expression_parts=None,
                                line_spacing=0.1, column_spacing=3.5,
                                width: int = None, height: int = None):
    """
    Create a GIF animation with state transition highlighting.

    Args:
        width:
        height:
        pairs: List of StateTransitionPair objects for the animation
        output_name: Name for the output file (without extension)
        speed_multiplier: Speed control (1.0=normal, 0.5=half speed, 2.0=double speed)
        expression_parts: List of expression parts (defaults to ["{==0}", "->", "{==1}"])
        line_spacing: Spacing between wrapped lines within a single text entry (default: 0.1)
        column_spacing: Width spacing between columns in multi-column layout (default: 3.5)
    """
    if pairs is None:
        pairs = []
    if expression_parts is None:
        expression_parts = ["{==0}", "->", "{==1}"]

    config.format = DEFAULT_FORMAT
    config.frame_rate = DEFAULT_FRAME_RATE
    if width is not None or height is not None:
        config.pixel_width = width if width is not None else DEFAULT_WIDTH
        config.pixel_height = height if height is not None else DEFAULT_HEIGHT
    else:
        config.quality = GIF_QUALITY
        config.pixel_width = DEFAULT_WIDTH
        config.pixel_height = DEFAULT_HEIGHT

    config.output_file = output_name

    class GifStateTransitionAnimation(StateTransitionAnimation):
        def __init__(self, **kwargs):
            super().__init__(state_pairs=pairs, expression_parts=expression_parts,
                             speed_multiplier=speed_multiplier, line_spacing=line_spacing,
                             column_spacing=column_spacing, **kwargs)

    scene = GifStateTransitionAnimation()
    scene.render()

    print(f"✓ Enhanced State Transition GIF created: {output_name}.{DEFAULT_FORMAT}")
    print(f"✓ Quality: {config.quality}")
    return True
