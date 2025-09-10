"""
Configuration settings for documentation animations.
"""

# Animation settings
DEFAULT_WIDTH = 800
DEFAULT_HEIGHT = 300
MAX_DURATION = 10  # seconds
DEFAULT_FRAME_RATE = 60
DEFAULT_FORMAT = "gif"

# Output settings
OUTPUT_DIR = "output"
GIF_QUALITY = "high_quality"  # low_quality, medium_quality, high_quality

# Manim quality settings mapping
QUALITY_SETTINGS = {
    "low_quality": "-ql",
    "medium_quality": "-qm",
    "high_quality": "-qh"
}

# Default colors for documentation themes
COLORS = {
    "primary": "#3498db",
    "secondary": "#2ecc71",
    "accent": "#e74c3c",
    "text": "#2c3e50",
    "subtext": "#21e7f2",
    "background": "#000000"
}
