import sys
from pathlib import Path

doc_dir = Path(__file__).parent.parent
sys.path.insert(0, str(doc_dir))

from src.state_transition_animation import StateTransitionPair
from src.utils import create_state_transition_gif


def create_chain_expression_demo():
    """
    Create a GIF demonstrating the filter((-5,5)).limit(5).take(0,2).sum().meet(>1) chain expression.
    This shows a complete data processing pipeline with filtering, limiting, selection, calculation, and condition checking.

    Chain Expression Processing:
    1. filter((-5,5)): Only accept values in range (-5, 5)
    2. limit(5): Keep at most 5 values in queue (FIFO)
    3. take(0,2): Take first two elements (indices 0,1)
    4. sum(): Calculate sum of taken elements
    5. meet(>1): Check if sum > 1
    """
    state_pairs = [
        StateTransitionPair("6", "false", "6∉(-5,5)|FILTERED OUT", False),
        StateTransitionPair("-6", "false", "-6∉(-5,5)|FILTERED OUT", False),

        StateTransitionPair("2", "false", "2∈(-5,5)|Queue:[2]|Need 2 elements", False),
        StateTransitionPair("1", "true", "1∈(-5,5)|Queue:[2,1]|Take:[2,1]|Sum:3|3>1✓", True),

        StateTransitionPair("-3", "true", "-3∈(-5,5)|Queue:[2,1,-3]|Take:[2,1]|Sum:3|3>1✓", True),
        StateTransitionPair("0", "true", "0∈(-5,5)|Queue:[2,1,-3,0]|Take:[2,1]|Sum:3|3>1✓", True),
        StateTransitionPair("4", "true", "4∈(-5,5)|Queue:[2,1,-3,0,4]|Take:[2,1]|Sum:3|3>1✓", True),

        StateTransitionPair("-1", "false", "-1∈(-5,5)|Queue:[1,-3,0,4,-1]|Take:[1,-3]|Sum:-2|-2>1✗", False),
        StateTransitionPair("-2", "false", "-2∈(-5,5)|Queue:[-3,0,4,-1,-2]|Take:[-3,0]|Sum:-3|-3>1✗", False),

        StateTransitionPair("3", "true", "3∈(-5,5)|Queue:[0,4,-1,-2,3]|Take:[0,4]|Sum:4|4>1✓", True),

        StateTransitionPair("-4", "true", "-4∈(-5,5)|Queue:[4,-1,-2,3,-4]|Take:[4,-1]|Sum:3|3>1✓", True),
        StateTransitionPair("1", "false", "1∈(-5,5)|Queue:[-1,-2,3,-4,1]|Take:[-1,-2]|Sum:-3|-3>1✗", False),
        StateTransitionPair("2", "false", "2∈(-5,5)|Queue:[-2,3,-4,1,2]|Take:[-2,3]|Sum:1|1>1✗", False),
        StateTransitionPair("4", "false", "4∈(-5,5)|Queue:[3,-4,1,2,4]|Take:[3,-4]|Sum:-1|-1>1✗", False),
        StateTransitionPair("-1", "false", "-1∈(-5,5)|Queue:[-4,1,2,4,-1]|Take:[-4,1]|Sum:-3|-3>1✗", False),
        StateTransitionPair("3", "true", "3∈(-5,5)|Queue:[1,2,4,-1,3]|Take:[1,2]|Sum:3|3>1✓", True),
    ]

    create_state_transition_gif(
        state_pairs,
        'chain_expression_demo',
        speed_multiplier=1.0,
        expression_parts=["filter((-5,5))", ".", "limit(5)", ".", "take(0,2)", ".", "sum()", ".", "meet(>1)"],
        line_spacing=0.3,
        column_spacing=3.8
    )


def create_chain_expression_demo_with_queue_line():
    """
    生成展示链式表达式（filter((-5,5)).limit(5).take(0,2).sum().meet(>1)）且在表达式下方新增
    “Queue: [...]” 独立行的 GIF。
    """

    state_pairs = [
        StateTransitionPair("6", "false", "6∉(-5,5)|FILTERED OUT", False, subtitle="Queue:[]"),
        StateTransitionPair("-6", "false", "-6∉(-5,5)|FILTERED OUT", False, subtitle="Queue:[]"),

        StateTransitionPair("2", "false", "2∈(-5,5)|Need 2 elements", False, subtitle="Queue:[2]"),
        StateTransitionPair("1", "true", "1∈(-5,5)|2+1=3,RESTART", True, subtitle="Queue:[2,1]"),

        StateTransitionPair("-3", "true", "-3∈(-5,5)|Need 2 elements", True, subtitle="Queue:[-3]"),
        StateTransitionPair("0", "true", "0∈(-5,5)|-3+0=-3", True, subtitle="Queue:[-3,0]"),
        StateTransitionPair("4", "true", "4∈(-5,5)|0+4=4,RESTART", True, subtitle="Queue:[-3,0,4]"),

        StateTransitionPair("-1", "false", "-1∈(-5,5)|Need 2 elements", False, subtitle="Queue:[-1]"),
        StateTransitionPair("-2", "false", "-2∈(-5,5)|-2-1=-3", False, subtitle="Queue:[-1,-2]"),

        StateTransitionPair("3", "true", "3∈(-5,5)|-2+3=1", True, subtitle="Queue:[-1,-2,3]"),

        StateTransitionPair("-4", "true", "-4∈(-5,5)|3-4=-1", True, subtitle="Queue:[-1,-2,3,-4]"),
        StateTransitionPair("3", "true", "-4∈(-5,5)|-4+3=-1", True, subtitle="Queue:[-1,-2,3,-4,3]"),
        StateTransitionPair("-2", "false", "-6∈(-5,5)|3-2=1,delete first element", False, subtitle="Queue:[-2,3,-4,3,-2]"),
        StateTransitionPair("4", "false", "8∈(-5,5)|-2+4=2,RESTART", False, subtitle="Queue:[3,-4,3,-2,4]"),
    ]

    return create_state_transition_gif(
        pairs=state_pairs,
        output_name='chain_expression_demo_with_queue',
        speed_multiplier=1.5,
        expression_parts=["filter((-5,5))", ".", "limit(5)", ".", "take(0,2)", ".", "sum()", ".", "meet(>1)"],
        line_spacing=0.3,
        column_spacing=3.8,
        width=900,
        height=400
    )


def create_filter_window_avg_meet_demo():
    """
    Create a GIF demonstrating the filter(>0).window(3).avg().meet(>50) chain expression.
    This shows a data processing pipeline with:
    1. filter(>0): Only accept positive values
    2. window(3): Maintain a sliding window of 3 values
    3. avg(): Calculate the average of the window
    4. meet(>50): Check if the average is greater than 50
    """
    state_pairs = [
        # Initial negative values - filtered out
        StateTransitionPair("-5", "false", "-5≤0|FILTERED OUT", False, subtitle="Window:[]"),
        StateTransitionPair("-2", "false", "-2≤0|FILTERED OUT", False, subtitle="Window:[]"),

        # Building up the window - need 3 values for average
        StateTransitionPair("10", "false", "10>0|Need 3 values", False, subtitle="Window:[10]"),
        StateTransitionPair("20", "false", "20>0|Need 3 values", False, subtitle="Window:[10,20]"),
        StateTransitionPair("30", "false", "30>0|(10+20+30)/3=20", False, subtitle="Window:[10,20,30]"),

        # Sliding window - first success
        StateTransitionPair("40", "false", "40>0|(20+30+40)/3=30", False, subtitle="Window:[20,30,40]"),
        StateTransitionPair("80", "false", "80>0|(30+40+80)/3=50", False, subtitle="Window:[30,40,80]"),
        StateTransitionPair("90", "true", "90>0|(40+80+90)/3=70", True, subtitle="Window:[40,80,90]"),

    ]
# I only need one document, whose content merely includes the explanation of the expression and the corresponding reference
    return create_state_transition_gif(
        pairs=state_pairs,
        output_name='filter_window_avg_meet_demo',
        speed_multiplier=0.9,
        expression_parts=["filter(>0)", ".", "window(3)", ".", "avg()", ".", "meet(>50)"],
        line_spacing=0.3,
        column_spacing=3.5,
        width=1000,
        height=500
    )
