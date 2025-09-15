"""
State transition animation for demonstrating {==0}->{==1} expressions.
Shows visual highlighting of which state is currently being evaluated.
"""

from manim import *
import sys
import os
from dataclasses import dataclass
from typing import List, Optional
from src.animation_base import DocAnimation
from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_DURATION, COLORS

sys.path.append(os.path.dirname(os.path.dirname(__file__)))


class StateTransitionPair:
    """Represents a data pair for state transition animation with state information."""

    def __init__(self, left: str, right: str = 'False', record_text: str = "",
                 recording_tag: bool = False, current_state: int = 0,
                 subtitle: Optional[str] = None,
                 delay: float = 0.1, movement_duration: float = 0.3,
                 left_color: Optional[str] = None, right_color: Optional[str] = None, **kwargs):
        """
        Initialize StateTransitionPair.

        Args:
            left: Left string value (input data)
            right: Right string value (result: 'true'/'false')
            recording_tag: Boolean tag for recording (default: False)
            record_text: Text for record display (default: "")
            current_state: Current state of the expression (0=waiting for first state, 1=waiting for second state)
            delay: Animation delay (default: 0.1)
            movement_duration: Duration of movement animation (default: 0.3)
            left_color: DEPRECATED - ignored, left color is always blue
            right_color: DEPRECATED - ignored, right color is computed based on right value
            **kwargs: Additional keyword arguments (ignored for backward compatibility)
        """
        self.left = left
        self.right = right
        self.recording_tag = recording_tag
        self.current_state = current_state  # 0 = waiting for {==0}, 1 = waiting for {==1}
        self.delay = delay
        self.movement_duration = movement_duration
        # Optional subtitle shown under the expression (e.g., saved queue data)
        self.subtitle = subtitle

        # Set default record_text if not provided
        if not record_text:
            self.record_text = self.get_combined_text()
        else:
            self.record_text = record_text

    @property
    def left_color(self) -> str:
        """Left color is always blue."""
        return 'blue'

    @property
    def right_color(self) -> str:
        """Right color is red if right is False/false, otherwise green."""
        # Check if right is boolean False or string 'false' (case insensitive)
        if isinstance(self.right, bool):
            return 'red' if not self.right else 'green'
        elif isinstance(self.right, str):
            return 'red' if self.right.lower() == 'false' else 'green'
        else:
            # For any other value, treat as truthy/falsy
            return 'red' if not self.right else 'green'

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
        return f"{self.record_text} {self.get_status_indicator()}"


class StateTransitionAnimation(DocAnimation):
    """
    Animation showing state transition expressions with dynamic highlighting.
    
    Features:
    1. Center expression with parts that can be highlighted individually
    2. Visual indication of which state is currently being evaluated
    3. Dynamic highlighting that changes as the state machine progresses
    4. Recording area showing the evaluation history
    """

    def __init__(self, state_pairs=None, expression_parts=None, speed_multiplier=1.0,
                 line_spacing=0.1, column_spacing=3.5, **kwargs):
        """
        Initialize StateTransitionAnimation.

        Args:
            state_pairs: List of StateTransitionPair objects
            expression_parts: List of expression parts (e.g., ["{==0}", "->", "{==1}"])
            speed_multiplier: Speed control (1.0=normal, 0.5=half speed, 2.0=double speed)
            line_spacing: Spacing between wrapped lines within a single text entry (default: 0.1)
            column_spacing: Width spacing between columns in multi-column layout (default: 3.5)
        """
        if state_pairs is None:
            state_pairs = [StateTransitionPair('0', 'false')]

        if expression_parts is None:
            expression_parts = ["{==0}", "->", "{==1}"]

        self.state_pairs = state_pairs
        self.expression_parts = expression_parts
        self.speed_multiplier = speed_multiplier

        # Layout configuration parameters
        self.line_spacing = line_spacing  # Spacing between wrapped lines
        self.column_spacing = column_spacing  # Width between columns

        super().__init__(**kwargs)

    def _get_runtime(self, base_time):
        """Apply speed multiplier to runtime values."""
        return base_time / self.speed_multiplier

    def construct(self):
        """Main animation construction method."""
        self.setup_scene()

        # Setup the main components
        expression_group, expression_texts = self._setup_expression()

        # Setup subtitle line under the expression
        self._subtitle_value = self._setup_subtitle(expression_group)

        # Position the recording area under the subtitle line (or expression if subtitle absent)
        anchor = self._subtitle_value if self._subtitle_value is not None else expression_group
        recording_texts, recording_group, recording_title = self._setup_record_area(anchor)

        self._animate_state_transitions(expression_group, expression_texts, recording_texts, recording_group,
                                        recording_title)
        self.wait(1)

    def _setup_expression(self):
        """
        Create and position the expression with individual highlightable parts.
        
        Returns:
            Tuple of (expression_group, expression_texts)
        """
        # Create individual text objects for each part of the expression
        expression_texts = []
        for part in self.expression_parts:
            text = self.create_title(part, scale=0.8)
            expression_texts.append(text)

        # Position the parts horizontally
        expression_group = VGroup(*expression_texts)
        expression_group.arrange(RIGHT, buff=0.2)
        expression_group.move_to(UP * 2.0)

        # Show the expression
        self.play(FadeIn(expression_group), run_time=self._get_runtime(0.5))

        # Initially highlight the first state (waiting for {==0}) with neutral color
        self._highlight_expression_part(expression_texts, 0, "#FF0000")  # Yellow for initial state

        return expression_group, expression_texts

    def _setup_subtitle(self, expression_group):
        """
        Create a subtitle text placeholder shown under the expression.

        Returns:
            The subtitle Text mobject (placeholder) or None
        """
        # Start with empty subtitle; will update per pair
        subtitle_text = self.create_subtitle("", scale=0.6)
        # Place closer to the expression
        subtitle_text.next_to(expression_group, DOWN, buff=0.12)
        self.play(FadeIn(subtitle_text), run_time=self._get_runtime(0.2))
        return subtitle_text

    def _highlight_expression_part(self, expression_texts, part_index, highlight_color="#FFFF00"):
        """
        Highlight a specific part of the expression with enhanced visual feedback.

        Args:
            expression_texts: List of text objects for each expression part
            part_index: Index of the part to highlight (0 for first state, 2 for second state)
            highlight_color: Color for highlighting (green for satisfied, red for unsatisfied)
        """
        # Remove any existing highlights from all parts
        for text in expression_texts:
            if hasattr(text, 'highlight_rect'):
                self.remove(text.highlight_rect)
                delattr(text, 'highlight_rect')
            # Reset text color to white
            text.set_color(WHITE)

        # Add highlight to the specified part
        if 0 <= part_index < len(expression_texts):
            text = expression_texts[part_index]

            # Set text color to contrast with highlight
            if highlight_color == "#00FF00":  # Green
                text.set_color(WHITE)  # White text on green background
            elif highlight_color == "#FF0000":  # Red
                text.set_color(WHITE)  # White text on red background
            else:
                text.set_color(BLACK)  # Black text for other colors

            # Create a prominent background rectangle
            text.highlight_rect = SurroundingRectangle(text, color=highlight_color, buff=0.15)
            text.highlight_rect.set_fill(highlight_color, opacity=0.7)  # More opaque for better visibility
            text.highlight_rect.set_stroke(highlight_color, width=3)  # Add border
            self.add(text.highlight_rect)

            # Bring text to front
            text.z_index = 1

    def _setup_record_area(self, anchor_element):
        """
        Setup recording area components under the anchor element.
        
        Args:
            anchor_element: The element for positioning reference (expression or subtitle).
            
        Returns:
            Tuple of (recording_texts list, recording_group, recording_title).
        """
        recording_texts = []
        recording_group = VGroup()

        # Create a title for the recording area
        recording_title = self.create_subtitle("Evaluation Steps:", scale=0.6)
        recording_title.set_color("#888888")
        # Keep close to the anchor (expression or subtitle)
        recording_title.next_to(anchor_element, DOWN, buff=0.25)

        return recording_texts, recording_group, recording_title

    def _animate_state_transitions(self, expression_group, expression_texts, recording_texts, recording_group,
                                   recording_title):
        """
        Animate the state transitions with dynamic highlighting.

        Args:
            expression_group: The expression group
            expression_texts: List of expression text objects
            recording_texts: List for recording text objects
            recording_group: VGroup for recording elements
            recording_title: The recording title object
        """
        current_state = 0  # 0 = waiting for {==0}, 1 = waiting for {==1}

        for i, pair in enumerate(self.state_pairs):
            # Create and animate the input value
            left_string = self.create_subtitle(pair.left, scale=0.6)
            left_string.next_to(expression_group, LEFT * 20)
            left_string.set_color('blue')

            # Create the result value
            right_string = self.create_subtitle(pair.right, scale=0.7)
            right_string.next_to(expression_group, RIGHT, buff=1)

            # Set color based on result
            from config import COLORS
            color_value = COLORS.get(pair.right_color, pair.right_color)
            right_string.set_color(color_value)

            # Show the input value
            self.play(FadeIn(left_string), run_time=self._get_runtime(0.2))

            # Move input to evaluation position
            target_position = expression_group.get_left() + LEFT * 1.5
            self.play(
                left_string.animate.move_to(target_position),
                run_time=self._get_runtime(pair.movement_duration)
            )

            # Update highlighting based on current state and input
            # Use the current state (before processing this input) to determine which part to highlight
            self._update_state_highlighting(expression_texts, pair, current_state)

            # Show the result
            self.play(FadeIn(right_string), run_time=self._get_runtime(0.2))

            # Update subtitle line under the expression based on pair.subtitle
            subtitle_text = self._get_pair_subtitle(pair)
            if subtitle_text is not None:
                new_subtitle = self.create_subtitle(subtitle_text, scale=0.6)
                # FIX: Recalculate the position relative to the expression group.
                new_subtitle.next_to(expression_group, DOWN, buff=0.05)
                self.play(Transform(self._subtitle_value, new_subtitle), run_time=self._get_runtime(0.2))
            # Add to recording
            self._add_record_entry(pair, recording_texts, recording_group, recording_title)

            # Update current state to the predefined state from the pair
            # This represents the state AFTER processing this input
            current_state = pair.current_state

            # Clean up for next iteration (except for the last pair)
            if i < len(self.state_pairs) - 1:
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

    def _update_state_highlighting(self, expression_texts, pair, current_state):
        """
        Update the expression highlighting based on current state and input.
        Green for satisfied states, red for unsatisfied states.

        Args:
            expression_texts: List of expression text objects
            pair: Current StateTransitionPair
            current_state: Current state (0, 1, 2, etc.)
        """
        # Get the index of the state part to highlight based on expression structure
        state_part_index = self._get_state_part_index(current_state)

        # Determine highlighting color based on whether the current input satisfies the expected state
        if pair.recording_tag:
            # Input satisfies the current state - GREEN
            self._highlight_expression_part(expression_texts, state_part_index, "#00FF00")  # Green highlight
        else:
            # Input does not satisfy the current state - RED
            self._highlight_expression_part(expression_texts, state_part_index, "#FF0000")  # Red highlight

        # Brief pause to show the highlighting before evaluation
        self.wait(self._get_runtime(0.15))

    def _get_state_part_index(self, current_state):
        """
        Get the index of the expression part to highlight based on current state.

        Args:
            current_state: Current state number

        Returns:
            Index of the expression part to highlight
        """
        # For expressions like ["{>1}", "->", "{count(<1,3)}", "->", "{==3}"]
        # State 0 -> index 0 ({>1})
        # State 1 -> index 2 ({count(<1,3)})
        # State 2 -> index 4 ({==3})

        # For expressions like ["{==0}", "->", "{==1}"]
        # State 0 -> index 0 ({==0})
        # State 1 -> index 2 ({==1})

        if len(self.expression_parts) == 3:
            # Simple two-state expression: {==0} -> {==1}
            return current_state * 2  # 0->0, 1->2
        elif len(self.expression_parts) == 5:
            # Three-state expression: {>1} -> {count(<1,3)} -> {==3}
            return current_state * 2  # 0->0, 1->2, 2->4
        else:
            # Default fallback
            return min(current_state * 2, len(self.expression_parts) - 1)

    def _update_current_state(self, current_state, pair):
        """
        Update the current state based on the evaluation result.
        Forward-only progression: 0 -> 1 -> complete/reset, never backward.

        Args:
            current_state: Current state (0 or 1)
            pair: Current StateTransitionPair

        Returns:
            New current state
        """
        # State transition logic for {==0}->{==1} with forward-only progression
        if current_state == 0:  # Waiting for first state {==0}
            if pair.left == "0" and pair.recording_tag:
                # First state satisfied, move forward to second state
                return 1
            else:
                # First state not satisfied, stay in state 0 (waiting for {==0})
                return 0

        elif current_state == 1:  # Waiting for second state {==1}
            if pair.left == "1" and pair.right.lower() == "true":
                # Second state satisfied, expression complete - reset to beginning
                return 0
            else:
                # Second state not satisfied, but we NEVER go backward
                # Stay in state 1 (still waiting for {==1}) or reset if sequence is invalid
                if pair.left == "0":
                    # If we get another '0', we stay in state 1 (still waiting for {==1})
                    # This represents the case where we had 0->0->1
                    return 1
                else:
                    # Any other value that's not '1' means the sequence failed
                    # Reset to state 0 to start over
                    return 0

        return current_state

    def _get_pair_subtitle(self, pair: StateTransitionPair) -> Optional[str]:
        """
        Determine the subtitle string to show under the expression for this pair.

        Priority:
        1) pair.subtitle if provided
        2) Fallback: try to extract a concise segment (e.g., 'Queue:[...]') from record_text
        """
        if getattr(pair, 'subtitle', None):
            return pair.subtitle

        # Fallback extraction from record_text (search first segment before '|')
        text = getattr(pair, 'record_text', "") or ""
        if not text:
            return None
        # Split on '|' and find a token that contains 'Queue:'
        parts = [p.strip() for p in text.split('|') if p.strip()]
        for p in parts:
            if p.startswith("Queue:"):
                return p
        return None

    def _add_record_entry(self, pair, recording_texts, recording_group, recording_title):
        """
        Add a single record entry to the recording area.
        
        Args:
            pair: The StateTransitionPair object to record
            recording_texts: List to store recording text objects
            recording_group: VGroup for recording elements
            recording_title: The recording title object
        """
        # Add this pair to the recording with text wrapping
        record_text = self._create_wrapped_subtitle(pair.get_combined_text(), scale=0.7, max_width=25)

        # Set color based on status
        status_color = "secondary" if pair.recording_tag else "accent"
        from config import COLORS
        color_value = COLORS.get(status_color, status_color)
        record_text.set_color(color_value)

        recording_texts.append(record_text)

        # Show recording title on first entry
        if len(recording_texts) == 1:
            self.play(FadeIn(recording_title), run_time=self._get_runtime(0.2))

        # Position recording texts with multi-column layout to ensure all text can be seen
        # Use horizontal layout if we have many entries to save vertical space
        max_vertical_entries = 3  # Maximum entries in a single column (reduced for wrapped text)

        if len(recording_texts) == 1:
            record_text.next_to(recording_title, DOWN, buff=0.3)
        elif len(recording_texts) <= max_vertical_entries:
            # Vertical layout for first few entries
            prev_text = recording_texts[-2]
            # Calculate spacing based on whether previous text was wrapped
            prev_height = self._get_text_height(prev_text)
            spacing = 0.4
            # spacing = max(0.4, prev_height + 0.2)  # Dynamic spacing based on text height
            record_text.next_to(prev_text, DOWN, buff=spacing)
        else:
            # Horizontal layout for additional entries
            row = (len(recording_texts) - 1) % max_vertical_entries

            if row == 0:
                # Starting a new column - need to reposition all texts to keep them centered
                self._reposition_recording_texts_centered(recording_texts, recording_title, max_vertical_entries)
            else:
                # Continue in current column
                prev_text = recording_texts[-2]
                prev_height = self._get_text_height(prev_text)
                spacing = 0.4
                record_text.next_to(prev_text, DOWN, buff=spacing)

        # Add to recording group and show
        recording_group.add(record_text)
        self.play(FadeIn(record_text), run_time=self._get_runtime(0.2))

    def _reposition_recording_texts_centered(self, recording_texts, recording_title, max_vertical_entries):
        """
        Reposition recording texts with scrolling behavior - old texts slide left and disappear,
        new texts always remain visible and readable.

        Args:
            recording_texts: List of recording text mobjects.
            recording_title: The recording title mobject for positioning reference.
            max_vertical_entries: Maximum entries per column.
        """
        total_entries = len(recording_texts)
        total_columns = (total_entries - 1) // max_vertical_entries + 1

        # Screen boundary constants (Manim coordinate system)
        SCREEN_LEFT_BOUNDARY = -7.0   # Left edge of visible screen
        SCREEN_RIGHT_BOUNDARY = 7.0   # Right edge of visible screen
        USABLE_WIDTH = SCREEN_RIGHT_BOUNDARY - SCREEN_LEFT_BOUNDARY  # Total usable width
        MARGIN = 0.5  # Safety margin from screen edges

        # Calculate effective usable width with margins
        effective_width = USABLE_WIDTH - (2 * MARGIN)

        # Always use full column width - no compression!
        column_width = self.column_spacing

        # Calculate how many columns can fit within screen boundaries with full width
        max_visible_columns = int(effective_width / column_width)

        # Check if we need scrolling (sliding) behavior
        needs_scrolling = total_columns > max_visible_columns

        if needs_scrolling:
            # SLIDING MODE: All texts shift left, new text appears on the right

            # Define the rightmost visible position
            rightmost_x = SCREEN_RIGHT_BOUNDARY - MARGIN - (column_width / 2)

            # Shift all existing texts left by one column width
            texts_to_animate = []
            for text in recording_texts[:-1]:  # Exclude the new text being added
                current_pos = text.get_center()
                new_x = current_pos[0] - column_width  # Shift left by one column
                new_pos = [new_x, current_pos[1], current_pos[2]]
                texts_to_animate.append((text, new_pos))

            # Position new text at the rightmost visible position
            new_text = recording_texts[-1]
            new_row = (total_entries - 1) % max_vertical_entries

            # Calculate y position for new text
            if new_row == 0:
                new_y_pos = recording_title.get_bottom()[1] - 0.4
            else:
                # Find a text in the same row from a previous column to get proper spacing
                text_height = self._get_text_height(new_text)
                row_spacing = max(1.1, text_height + 0.3)
                new_y_pos = recording_title.get_bottom()[1] - 0.4 - (new_row * row_spacing)

            new_text.move_to([rightmost_x, new_y_pos, 0])

        else:
            # NORMAL MODE: All columns fit, center them normally
            total_width = total_columns * column_width
            center_x = recording_title.get_center()[0]
            start_x = center_x - (total_width / 2) + (column_width / 2)

            texts_to_animate = []
            for i, text in enumerate(recording_texts[:-1]):  # Exclude the current text being added
                column = i // max_vertical_entries
                row = i % max_vertical_entries

                x_pos = start_x + column * column_width

                # Increased vertical spacing for wrapped text
                text_height = self._get_text_height(text)
                base_offset = 0.4
                row_spacing = max(1.1, text_height + 0.3)
                y_pos = recording_title.get_bottom()[1] - base_offset - (row * row_spacing)

                # Store current position for smooth animation
                current_pos = text.get_center()
                new_pos = [x_pos, y_pos, 0]

                # Only animate if position actually changed
                if abs(current_pos[0] - new_pos[0]) > 0.1 or abs(current_pos[1] - new_pos[1]) > 0.1:
                    texts_to_animate.append((text, new_pos))
                else:
                    text.move_to(new_pos)

            # Position the new text normally
            new_text = recording_texts[-1]
            new_column = (total_entries - 1) // max_vertical_entries
            new_row = (total_entries - 1) % max_vertical_entries
            new_x_pos = start_x + new_column * column_width

            # Calculate y position for new text
            if new_row == 0:
                new_y_pos = recording_title.get_bottom()[1] - 0.4
            else:
                prev_text_in_column_index = new_column * max_vertical_entries + (new_row - 1)
                if prev_text_in_column_index < len(recording_texts) - 1:
                    prev_text = recording_texts[prev_text_in_column_index]
                    prev_height = self._get_text_height(prev_text)
                    spacing = max(1.1, prev_height + 0.3)
                    new_y_pos = prev_text.get_bottom()[1] - spacing
                else:
                    new_y_pos = recording_title.get_bottom()[1] - 0.4 - (new_row * 1.1)

            new_text.move_to([new_x_pos, new_y_pos, 0])



        # Perform smooth sliding animation for texts that moved
        if texts_to_animate:
            animations = []
            for text, new_pos in texts_to_animate:
                animations.append(text.animate.move_to(new_pos))

            # Play all sliding animations simultaneously
            if animations:
                self.play(*animations, run_time=self._get_runtime(0.3))

        # Optional: Print debug info about scrolling behavior
        if needs_scrolling:
            hidden_columns = total_columns - max_visible_columns
            print(f"📜 Sliding: All texts shifted left, {hidden_columns} oldest column(s) off-screen, new text appears on right")

    def _create_wrapped_subtitle(self, text, scale=0.7, max_width=25):
        """
        Create a subtitle with automatic text wrapping using '|' as line break indicator.
        The second line will be smaller than the first line.

        Args:
            text: The text to display (may contain '|' for manual line breaks)
            scale: Scale factor for the first line text
            max_width: Maximum characters per line before automatic wrapping (ignored if '|' is present)

        Returns:
            Text or VGroup: Single Text object for short text, VGroup for wrapped text
        """
        # Check if text contains manual line break indicator '|'
        if '|' in text:
            # Manual line break using '|'
            lines = text.split('|')
            lines = [line.strip() for line in lines if line.strip()]  # Remove empty lines and whitespace
        else:
            # Automatic wrapping for long text
            if len(text) <= max_width:
                # Text is short enough, no wrapping needed
                return self.create_subtitle(text, scale=scale)

            # Text is too long, need to wrap it automatically
            lines = self._wrap_text(text, max_width)

        # Create Text objects for each line with different scales
        text_objects = []
        for i, line in enumerate(lines):
            if i == 0:
                # First line: use original scale
                line_text = self.create_subtitle(line, scale=scale)
            else:
                # Second and subsequent lines: use smaller scale (80% of original)
                smaller_scale = scale * 0.7
                line_text = Text(line, color=COLORS["subtext"]).scale(smaller_scale)
            text_objects.append(line_text)

        # Arrange lines vertically
        if len(text_objects) > 1:
            # Create a VGroup to hold all lines
            wrapped_group = VGroup(*text_objects)
            wrapped_group.arrange(DOWN * 0.3, buff=self.line_spacing)  # Configurable buffer between lines
            return wrapped_group
        else:
            return text_objects[0]

    def _wrap_text(self, text, max_width):
        """
        Wrap text to multiple lines at word boundaries.

        Args:
            text: The text to wrap
            max_width: Maximum characters per line

        Returns:
            List[str]: List of wrapped lines
        """
        if len(text) <= max_width:
            return [text]

        words = text.split(' ')
        lines = []
        current_line = ""

        for word in words:
            # Check if adding this word would exceed the max width
            test_line = current_line + (" " if current_line else "") + word

            if len(test_line) <= max_width:
                current_line = test_line
            else:
                # Adding this word would exceed max width
                if current_line:
                    # Save the current line and start a new one
                    lines.append(current_line)
                    current_line = word
                else:
                    # Single word is longer than max_width, force break it
                    if len(word) > max_width:
                        # Break the long word
                        while len(word) > max_width:
                            lines.append(word[:max_width])
                            word = word[max_width:]
                        current_line = word
                    else:
                        current_line = word

        # Add the last line if it has content
        if current_line:
            lines.append(current_line)

        return lines

    def _get_text_height(self, text_obj):
        """
        Get the height of a text object, handling both Text and VGroup objects.

        Args:
            text_obj: Text or VGroup object

        Returns:
            float: Height of the text object
        """
        if hasattr(text_obj, 'height'):
            return text_obj.height
        else:
            # Fallback for objects without height attribute
            return 0.5
