"""
String movement animation for documentation.
Demonstrates text moving from left to center, with a new string appearing on the right.
"""

from manim import *
import sys
import os
from dataclasses import dataclass
from typing import List, Optional
from src.animation_base import DocAnimation

sys.path.append(os.path.dirname(os.path.dirname(__file__)))


@dataclass
class StringPair:
    """Represents a pair of strings for the animation."""
    left: str
    right: str = 'False'
    recording_tag: bool = False
    left_color: Optional[str] = 'blue'
    right_color: Optional[str] = 'red'
    delay: float = 0.1
    movement_duration: float = 0.3

    def __post_init__(self):
        # Set default colors if not provided
        if self.left_color is None:
            self.left_color = "secondary"
        if self.right_color is None:
            self.right_color = "accent"

    def get_status_indicator(self) -> str:
        """
        Get the Unicode status indicator based on the recording_tag value.

        Returns:
            "√" (U+221A) for true values (recording_tag=True)
            "×" (U+00D7) for false values (recording_tag=False)
        """
        return "√" if self.recording_tag else "×"

    def get_combined_text(self) -> str:
        """
        Get the combined text with status indicator.

        Returns:
            "left string √" for recording_tag=True
            "left string ×" for recording_tag=False
        """
        return f"{self.left} {self.get_status_indicator()}"


def create_string_pairs(*args, **kwargs) -> List[StringPair]:
    """
    Helper function to create StringPair objects from various input formats.

    Usage:
    - create_string_pairs([['1', 'false'], ['2', 'true']])  # From 2D list
    - create_string_pairs('1', 'false', '2', 'true')       # From flat args
    - create_string_pairs(StringPair('1', 'false'), ...)   # From existing objects
    """
    if len(args) == 1 and isinstance(args[0], (list, tuple)):
        # Handle 2D list input: [['1', 'false'], ['2', 'true']]
        return [StringPair(left, right, **kwargs) for left, right in args[0]]
    elif len(args) % 2 == 0:
        # Handle flat args: '1', 'false', '2', 'true'
        pairs = []
        for i in range(0, len(args), 2):
            pairs.append(StringPair(args[i], args[i + 1], **kwargs))
        return pairs
    else:
        # Handle mixed or StringPair objects
        return list(args)


class StringMovementAnimation(DocAnimation):
    """
    Animation showing:
    1. Center string (static)
    2. Multiple pairs of left-right strings moving and appearing in sequence
    3. Recording area under center text showing all processed pairs with status indicators

    The animation displays:
    - Each left string moving from left to center-left
    - Each right string appearing on the right side
    - A recording list under the center text showing: "left string √/×" based on right value evaluation

    Parameters:
    - string_pairs: List of StringPair objects or 2D list for backward compatibility
    - center_text: The static center text (default: "Center")
    """

    def __init__(self, string_pairs=None, center_text="Center", speed_multiplier=1.0, **kwargs):
        if string_pairs is None:
            string_pairs = [StringPair('Moving', 'Appears')]

        # Convert 2D list to StringPair objects for backward compatibility
        if string_pairs and isinstance(string_pairs[0], (list, tuple)):
            string_pairs = [StringPair(left, right) for left, right in string_pairs]

        self.string_pairs = string_pairs
        self.center_text = center_text
        self.speed_multiplier = speed_multiplier  # 1.0 = normal, 0.5 = half speed, 2.0 = double speed
        super().__init__(**kwargs)

    def _get_runtime(self, base_time):
        """Apply speed multiplier to runtime values."""
        return base_time / self.speed_multiplier

    def construct(self):
        """Main animation construction method."""
        self.setup_scene()

        # Setup the three main components of the animation
        center_string = self._setup_center_string()
        recording_texts, recording_group, recording_title = self._setup_record_title(center_string)
        self._setup_side_strings(center_string, recording_texts, recording_group, recording_title)
        self.wait(0.5)

    def _setup_center_string(self):
        """
        Create, position, and animate the center string element.

        Returns:
            The center string mobject after animation.
        """
        # Create the center string (static) - positioned higher to make room for recording
        center_string = self.create_title(self.center_text, scale=0.8)
        center_string.move_to(UP * 1.2)  # Move center text higher

        # 1. Show the center string first
        self.play(FadeIn(center_string), run_time=self._get_runtime(0.3))

        # Optional: Add a highlight effect to show the center
        final_group = VGroup(center_string)
        highlight = self.create_highlight_box(final_group)
        self.play(Create(highlight), run_time=self._get_runtime(0.2))
        self.wait(self._get_runtime(0.2))

        return center_string

    def _setup_record_title(self, center_string):
        """
        Setup recording area components under the center text.

        Args:
            center_string: The center string mobject for positioning reference.

        Returns:
            Tuple of (recording_texts list, recording_group, recording_title).
        """
        # Create recording area under center text with better spacing
        recording_texts = []
        recording_group = VGroup()

        # Create a title for the recording area
        recording_title = self.create_subtitle("Recording:", scale=0.6)
        recording_title.set_color("#888888")  # Gray color for title
        recording_title.next_to(center_string, DOWN * 0.5, buff=0.8)

        return recording_texts, recording_group, recording_title

    def _add_record_entry(self, pair, recording_texts, recording_group, recording_title):
        """
        Add a single record entry to the recording area.

        Args:
            pair: The StringPair object to record.
            recording_texts: List to store recording text mobjects.
            recording_group: VGroup for recording elements.
            recording_title: The recording title mobject.
        """
        # Add this pair to the recording
        record_text = self.create_subtitle(pair.get_combined_text(),
                                           scale=0.8)  # Increased scale for better readability
        # Set color based on status (green for true, red for false)
        status_color = "secondary" if pair.recording_tag else "accent"
        from config import COLORS
        color_value = COLORS.get(status_color, status_color)
        record_text.set_color(color_value)

        recording_texts.append(record_text)

        # Show recording title on first entry
        if len(recording_texts) == 1:
            self.play(FadeIn(recording_title), run_time=self._get_runtime(0.2))

        # Position recording texts with better spacing and layout
        # Use horizontal layout if we have many entries to save vertical space
        max_vertical_entries = 4  # Maximum entries in a single column

        if len(recording_texts) == 1:
            record_text.next_to(recording_title, DOWN, buff=0.3)
        elif len(recording_texts) <= max_vertical_entries:
            # Vertical layout for first few entries
            record_text.next_to(recording_texts[-2], DOWN, buff=0.2)
        else:
            # Horizontal layout for additional entries
            column = (len(recording_texts) - 1) // max_vertical_entries
            row = (len(recording_texts) - 1) % max_vertical_entries

            if row == 0:
                # Starting a new column - need to reposition all texts to keep them centered
                self._reposition_recording_texts_centered(recording_texts, recording_title, max_vertical_entries)
            else:
                # Continue in current column
                record_text.next_to(recording_texts[-2], DOWN, buff=0.2)

        # Add to recording group and show
        recording_group.add(record_text)
        self.play(FadeIn(record_text), run_time=self._get_runtime(0.2))

    def _reposition_recording_texts_centered(self, recording_texts, recording_title, max_vertical_entries):
        """
        Reposition all recording texts to keep them centered when adding new columns.

        Args:
            recording_texts: List of recording text mobjects.
            recording_title: The recording title mobject for positioning reference.
            max_vertical_entries: Maximum entries per column.
        """
        total_entries = len(recording_texts)
        total_columns = (total_entries - 1) // max_vertical_entries + 1

        # Calculate the total width needed for all columns
        column_width = 2.5  # Approximate width per column including spacing
        total_width = total_columns * column_width

        # Calculate starting position to center all columns
        start_x = recording_title.get_center()[0] - (total_width / 2) + (column_width / 2)

        # Reposition all existing texts
        for i, text in enumerate(recording_texts[:-1]):  # Exclude the current text being added
            column = i // max_vertical_entries
            row = i % max_vertical_entries

            # Calculate position for this text
            x_pos = start_x + column * column_width
            y_pos = recording_title.get_bottom()[1] - 0.3 - (row * 0.5)  # 0.3 initial offset, 0.5 per row

            # Move the text to new position
            text.move_to([x_pos, y_pos, 0])

        # Position the new text (last in the list)
        new_text = recording_texts[-1]
        new_column = (total_entries - 1) // max_vertical_entries
        new_row = (total_entries - 1) % max_vertical_entries

        new_x_pos = start_x + new_column * column_width
        new_y_pos = recording_title.get_bottom()[1] - 0.3 - (new_row * 0.5)
        new_text.move_to([new_x_pos, new_y_pos, 0])

    def _setup_side_strings(self, center_string, recording_texts, recording_group, recording_title):
        """
        Create and animate the left and right string pairs.

        Args:
            center_string: The center string mobject for positioning reference.
            recording_texts: List to store recording text mobjects.
            recording_group: VGroup for recording elements.
            recording_title: The recording title mobject.
        """
        # Process each string pair
        for i, pair in enumerate(self.string_pairs):
            # Create the left string (will move from left to center-left)
            left_string = self.create_subtitle(pair.left, scale=0.6)
            # Position it far to the left initially
            left_string.next_to(center_string, LEFT * 20)

            # Set color based on pair configuration
            if hasattr(pair, 'left_color') and pair.left_color:
                from config import COLORS
                color_value = COLORS.get(pair.left_color, pair.left_color)
                left_string.set_color(color_value)

            # Create the right string (will appear after left movement)
            right_string = self.create_subtitle(pair.right, scale=0.7)
            # Position it to the right of center
            right_string.next_to(center_string, RIGHT, buff=1)

            # Set color based on pair configuration
            if hasattr(pair, 'right_color') and pair.right_color:
                from config import COLORS
                color_value = COLORS.get(pair.right_color, pair.right_color)
                right_string.set_color(color_value)

            # Animation sequence for this pair

            # Show the left string at its starting position
            self.play(FadeIn(left_string), run_time=self._get_runtime(0.2))

            # Move the left string from left to the left side of center
            target_position = center_string.get_left() + LEFT * 1.5
            movement_duration = getattr(pair, 'movement_duration', 0.3)
            self.play(
                left_string.animate.move_to(target_position),
                run_time=self._get_runtime(movement_duration)
            )

            # When left movement finishes, show the right string
            self.play(FadeIn(right_string), run_time=self._get_runtime(0.2))

            # Add this pair to the recording
            self._add_record_entry(pair, recording_texts, recording_group, recording_title)

            if i < len(self.string_pairs) - 1:
                self.play(
                    FadeOut(left_string),
                    FadeOut(right_string),
                    run_time=self._get_runtime(0.3)
                )
            else:
                # For the last pair, hold longer then fade out
                self.play(
                    FadeOut(left_string),
                    FadeOut(right_string),
                    run_time=self._get_runtime(0.4)
                )
