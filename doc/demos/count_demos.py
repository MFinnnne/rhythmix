import sys
from pathlib import Path

doc_dir = Path(__file__).parent.parent
sys.path.insert(0, str(doc_dir))

from src.string_movement_animation import StringPair
from src.state_transition_animation import StateTransitionPair
from src.utils import create_gif_animation, create_state_transition_gif


def create_count_demo():
    count_pairs = [
        StringPair("1", "false", "1>4   +0"),
        StringPair("5", "false", "5>4    +1", True),
        StringPair("2", "false", "2>4   +1"),
        StringPair("6", "false", "6>4    +2", True),
        StringPair("2", "false", "2>4   +2"),
        StringPair("6", "true", "6>4     +3", True)
    ]
    create_gif_animation(count_pairs, 'count(>4,3)', 'count1', speed_multiplier=2.0)


def create_count_constraint_demo():
    """
    Create a GIF demonstrating the count!([10,20], 5) constraint expression.
    This shows a count constraint that requires exactly 5 CONSECUTIVE values within the range [10,20].
    """
    state_pairs = [
        StateTransitionPair("5", "false", "5∉[10,20]|RESET counter", False, current_state=0),
        StateTransitionPair("25", "false", "25∉[10,20]|RESET counter", False, current_state=0),
        StateTransitionPair("15", "false", "15∈[10,20]|count: 1/5", True, current_state=1),
        StateTransitionPair("12", "false", "12∈[10,20]|count: 2/5", True, current_state=2),
        StateTransitionPair("3", "false", "3∉[10,20]|RESET to 0/5", False, current_state=0),
        StateTransitionPair("18", "false", "18∈[10,20]|count: 1/5", True, current_state=1),
        StateTransitionPair("14", "false", "14∈[10,20]|count: 2/5", True, current_state=2),
        StateTransitionPair("16", "false", "16∈[10,20]|count: 3/5", True, current_state=3),
        StateTransitionPair("30", "false", "30∉[10,20]|RESET to 0/5", False, current_state=0),
        StateTransitionPair("10", "false", "10∈[10,20]|count: 1/5", True, current_state=1),
        StateTransitionPair("11", "false", "11∈[10,20]|count: 2/5", True, current_state=2),
        StateTransitionPair("12", "false", "12∈[10,20]|count: 3/5", True, current_state=3),
        StateTransitionPair("13", "false", "13∈[10,20]|count: 4/5", True, current_state=4),
        StateTransitionPair("14", "true", "14∈[10,20]|count: 5/5 Completed!", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='count_constraint_demo',
        speed_multiplier=2.0,
        expression_parts=["count!([10,20], 5)"],
        line_spacing=0.4,
        column_spacing=4.0
    )


def create_multi_count_state_transition_demo():
    """
    Create a GIF demonstrating the {==0}->{count!(>4, 3)}->{count!(<2, 2)} state transition expression.
    """
    state_pairs = [
        StateTransitionPair("5", "false", "5≠0|Stay in State 0", False, current_state=0),
        StateTransitionPair("2", "false", "2≠0|Stay in State 0", False, current_state=0),
        StateTransitionPair("0", "false", "0==0|To State 1", True, current_state=1),
        StateTransitionPair("6", "false", "6>4|Count: 1/3", True, current_state=1),
        StateTransitionPair("7", "false", "7>4|Count: 2/3", True, current_state=1),
        StateTransitionPair("3", "false", "3≤4|RESET to 0/3", False, current_state=1),
        StateTransitionPair("8", "false", "8>4|Count: 1/3", True, current_state=1),
        StateTransitionPair("5", "false", "5>4|Count: 2/3", True, current_state=1),
        StateTransitionPair("9", "false", "9>4|Count: 3/3 ,to State 2", True, current_state=2),
        StateTransitionPair("1", "false", "1<2|Count: 1/2", True, current_state=2),
        StateTransitionPair("3", "false", "3≥2|RESET to 0/2", False, current_state=2),
        StateTransitionPair("0", "false", "0<2|Count: 1/2", True, current_state=2),
        StateTransitionPair("1", "true", "1<2|Count: 2/2|SUCCESS!", True, current_state=0),
        StateTransitionPair("4", "false", "4≠0|Stay in State 0", False, current_state=0),
    ]

    expression_parts = [
        "{==0}",
        "->",
        "{count!(>4, 3)}",
        "->",
        "{count!(<2, 2)}"
    ]

    return create_state_transition_gif(
        pairs=state_pairs,
        output_name="multi_count_state_transition",
        speed_multiplier=2,
        expression_parts=expression_parts,
        line_spacing=0.5,
        column_spacing=4
    )
