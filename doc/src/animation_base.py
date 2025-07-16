"""
Base class for documentation animations.
"""

from manim import *
from config import DEFAULT_WIDTH, DEFAULT_HEIGHT, MAX_DURATION, COLORS


class DocAnimation(Scene):
    """
    Base class for documentation animations with predefined settings.
    """
    
    def __init__(self, **kwargs):
        # Set custom resolution
        config.pixel_width = DEFAULT_WIDTH
        config.pixel_height = DEFAULT_HEIGHT
        super().__init__(**kwargs)
    
    def setup_scene(self):
        """Setup common scene elements."""
        self.camera.background_color = COLORS["background"]
    
    def create_title(self, text, scale=1.0):
        """Create a styled title text."""
        title = Text(text, color=COLORS["primary"]).scale(scale)
        return title
    
    def create_subtitle(self, text, scale=0.7):
        """Create a styled subtitle text."""
        subtitle = Text(text, color=COLORS["text"]).scale(scale)
        return subtitle
    
    def create_highlight_box(self, mobject, color=None):
        """Create a highlight box around a mobject."""
        if color is None:
            color = COLORS["accent"]
        box = SurroundingRectangle(mobject, color=color, buff=0.1)
        return box
    
    def animate_text_sequence(self, texts, duration_per_text=1.5):
        """Animate a sequence of texts with fade in/out."""
        total_duration = len(texts) * duration_per_text
        if total_duration > MAX_DURATION:
            duration_per_text = MAX_DURATION / len(texts)
        
        for text in texts:
            self.play(FadeIn(text), run_time=duration_per_text/2)
            self.wait(duration_per_text/4)
            self.play(FadeOut(text), run_time=duration_per_text/4)
    
    def construct(self):
        """Override this method to create your animation."""
        self.setup_scene()
        # Subclasses should implement their animation logic here
        pass
