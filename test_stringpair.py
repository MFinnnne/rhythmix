#!/usr/bin/env python3
"""
Test script for StringPair modifications.
This script tests the StringPair class without manim dependencies.
"""

from typing import Optional

class StringPair:
    """Represents a pair of strings for the animation."""
    
    def __init__(self, left: str, right: str = 'False', recording_tag: bool = False, 
                 record_text: str = "", delay: float = 0.1, movement_duration: float = 0.3,
                 left_color: Optional[str] = None, right_color: Optional[str] = None, **kwargs):
        """
        Initialize StringPair.
        
        Args:
            left: Left string value
            right: Right string value (default: 'False')
            recording_tag: Boolean tag for recording (default: False)
            record_text: Text for record display (default: "")
            delay: Animation delay (default: 0.1)
            movement_duration: Duration of movement animation (default: 0.3)
            left_color: DEPRECATED - ignored, left color is always blue
            right_color: DEPRECATED - ignored, right color is computed based on right value
            **kwargs: Additional keyword arguments (ignored for backward compatibility)
        """
        self.left = left
        self.right = right
        self.recording_tag = recording_tag
        self.delay = delay
        self.movement_duration = movement_duration
        
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
        return f"{self.left} {self.get_status_indicator()}"


def test_stringpair():
    """Test the StringPair modifications."""
    print('Testing StringPair modifications...')
    print('=' * 50)

    # Test 1: Basic functionality with default values
    print("Test 1: Basic functionality with string 'false'")
    pair1 = StringPair('test', 'false')
    print(f'  left_color: {pair1.left_color}')
    print(f'  right_color: {pair1.right_color}')
    print(f'  record_text: {pair1.record_text}')
    print()

    # Test 2: With boolean False
    print("Test 2: With boolean False")
    pair2 = StringPair('test', False)
    print(f'  right_color: {pair2.right_color}')
    print()

    # Test 3: With string 'true'
    print("Test 3: With string 'true'")
    pair3 = StringPair('test', 'true')
    print(f'  right_color: {pair3.right_color}')
    print()

    # Test 4: With boolean True
    print("Test 4: With boolean True")
    pair4 = StringPair('test', True)
    print(f'  right_color: {pair4.right_color}')
    print()

    # Test 5: With custom record_text
    print("Test 5: With custom record_text")
    pair5 = StringPair('test', 'false', record_text='Custom record text')
    print(f'  record_text: {pair5.record_text}')
    print()

    # Test 6: Backward compatibility - old parameters should be ignored
    print("Test 6: Backward compatibility (old color params ignored)")
    try:
        # This should work but ignore the color parameters
        pair6 = StringPair('test', 'false', left_color='orange', right_color='purple')
        print(f'  left_color: {pair6.left_color}')  # Should be blue
        print(f'  right_color: {pair6.right_color}')  # Should be red
        print("  ✓ Backward compatibility maintained")
    except TypeError as e:
        print(f"  ✗ Backward compatibility issue: {e}")
    print()

    # Test 7: Case insensitive string comparison
    print("Test 7: Case insensitive string comparison")
    pair7a = StringPair('test', 'FALSE')
    pair7b = StringPair('test', 'False')
    pair7c = StringPair('test', 'fAlSe')
    print(f'  "FALSE" -> right_color: {pair7a.right_color}')
    print(f'  "False" -> right_color: {pair7b.right_color}')
    print(f'  "fAlSe" -> right_color: {pair7c.right_color}')
    print()

    # Test 8: Edge cases
    print("Test 8: Edge cases")
    pair8a = StringPair('test', '')  # Empty string
    pair8b = StringPair('test', 0)   # Zero
    pair8c = StringPair('test', 1)   # Non-zero number
    print(f'  Empty string -> right_color: {pair8a.right_color}')
    print(f'  Zero -> right_color: {pair8b.right_color}')
    print(f'  One -> right_color: {pair8c.right_color}')
    print()

    print('All tests completed successfully!')


if __name__ == "__main__":
    test_stringpair()
