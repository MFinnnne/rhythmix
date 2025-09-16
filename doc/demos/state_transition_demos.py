import sys
from pathlib import Path

doc_dir = Path(__file__).parent.parent
sys.path.insert(0, str(doc_dir))

from src.state_transition_animation import StateTransitionPair
from src.utils import create_state_transition_gif


def create_state_transition_demo():
    """
    Create a GIF demonstrating the {==0}->{==1} state transition expression.
    This shows how the expression detects a change from value 0 to value 1.
    """
    state_pairs = [
        StateTransitionPair("2", "false", "2≠0", False, current_state=0),
        StateTransitionPair("0", "false", "0==0", True, current_state=1),
        StateTransitionPair("2", "false", "2≠1", False, current_state=1),
        StateTransitionPair("0", "false", "0==0", True, current_state=1),
        StateTransitionPair("1", "true", "1==1 SUCCESS!", True, current_state=0),
        StateTransitionPair("1", "false", "1≠0", False, current_state=0),
        StateTransitionPair("0", "false", "0==0", True, current_state=1),
        StateTransitionPair("0", "false", "0!=1", False, current_state=1),
        StateTransitionPair("0", "false", "waiting for 1", False, current_state=1),
        StateTransitionPair("1", "true", "1==1 SUCCESS!", True, current_state=0)
    ]

    create_state_transition_gif(state_pairs, 'state_transition_enhanced', speed_multiplier=1.5)


def create_complex_state_transition_demo():
    """
    Create a GIF demonstrating the {>1}->{count(<1,3)}->{==3} state transition expression.
    This shows a complex multi-state transition with counting logic.
    """
    state_pairs = [
        StateTransitionPair("0", "false", "0≤1", False, current_state=0),
        StateTransitionPair("2", "false", "2>1 ✓", True, current_state=1),
        StateTransitionPair("0", "false", "0<1 (1/3)", True, current_state=1),
        StateTransitionPair("-1", "false", "-1<1 (2/3)", True, current_state=1),
        StateTransitionPair("0.5", "false", "0.5<1 (3/3)", True, current_state=2),
        StateTransitionPair("2", "false", "2≠3", False, current_state=2),
        StateTransitionPair("3", "true", "3==3 SUCCESS!", True, current_state=0),
        StateTransitionPair("5", "false", "5>1 ✓", True, current_state=1),
        StateTransitionPair("0", "false", "0<1 (1/3)", True, current_state=1),
        StateTransitionPair("-0.5", "false", "-0.5<1 (2/3)", True, current_state=1),
        StateTransitionPair("0.1", "false", "0.1<1 (3/3)", True, current_state=2),
        StateTransitionPair("3", "true", "3==3 SUCCESS!", True, current_state=0),
        StateTransitionPair("10", "false", "10>1 ✓", True, current_state=1),
        StateTransitionPair("0", "false", "0<1 (1/3)", True, current_state=1),
        StateTransitionPair("2", "false", "2≥1, reset", False, current_state=0),
    ]

    create_state_transition_gif(state_pairs, 'complex_state_transition', speed_multiplier=1.2,
                                expression_parts=["{>1}", "->", "{count(<1,3)}", "->", "{==3}"])


def create_equal_chain_state_transition_demo():
    """
    Create a GIF demonstrating the {==0}=>{==1}=>{==0} state transition expression.
    """
    state_pairs = [
        StateTransitionPair("2", "false", "2≠0", False, current_state=0),
        StateTransitionPair("0", "false", "0==0 ✓", True, current_state=1),
        StateTransitionPair("2", "false", "2≠1", False, current_state=1),
        StateTransitionPair("1", "false", "1==1 ✓", True, current_state=2),
        StateTransitionPair("3", "false", "3≠0", False, current_state=2),
        StateTransitionPair("0", "true", "0==0 SUCCESS!", True, current_state=0),
        StateTransitionPair("0", "false", "0==0 ✓", True, current_state=1),
        StateTransitionPair("1", "false", "1==1 ✓", True, current_state=2),
        StateTransitionPair("0", "true", "0==0 SUCCESS!", True, current_state=0),
        StateTransitionPair("0", "false", "0==0 ✓ (back to state 1)", True, current_state=1),
        StateTransitionPair("0", "false", "0≠1 (still waiting for 1)", False, current_state=1),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='equal_chain_state_transition',
        speed_multiplier=1.2,
        expression_parts=["{==0}", "=>", "{==1}", "=>", "{==0}"]
    )


def create_logical_and_state_transition_demo():
    """
    Create a GIF demonstrating the {!=0&&!=2}->{==1} state transition expression.
    """
    state_pairs = [
        StateTransitionPair("0", "false", "0==0|first condition fails", False, current_state=0),
        StateTransitionPair("2", "false", "2==2|second condition fails", False, current_state=0),
        StateTransitionPair("1", "false", "1≠0&&1≠2|both conditions true", True, current_state=1),
        StateTransitionPair("3", "false", "3≠1|wrong value", False, current_state=1),
        StateTransitionPair("0", "false", "0≠1|wrong value", False, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),
        StateTransitionPair("3", "false", "3≠0&&3≠2|both conditions true", True, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),
        StateTransitionPair("-1", "false", "-1≠0&&-1≠2|both conditions true", True, current_state=1),
        StateTransitionPair("2", "false", "2≠1|wrong value", False, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),
        StateTransitionPair("0", "false", "0==0|first condition fails", False, current_state=0),
        StateTransitionPair("2", "false", "2==2|second condition fails", False, current_state=0),
        StateTransitionPair("5", "false", "5≠0&&5≠2|both conditions true", True, current_state=1),
        StateTransitionPair("4", "false", "4≠1|wrong value", False, current_state=1),
        StateTransitionPair("1", "true", "1==1|SUCCESS", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='logical_and_state_transition',
        speed_multiplier=1.0,
        expression_parts=["{!=0&&!=2}", "->", "{==1}"],
        line_spacing=0.4,
        column_spacing=4.0
    )


def create_interval_state_transition_demo():
    """
    Create a GIF demonstrating the {(1,3]}->{[4,7)} state transition expression.
    """
    state_pairs = [
        StateTransitionPair("1.0", "false", "1.0∉(1,3]|1 not included", False, current_state=0),
        StateTransitionPair("0.5", "false", "0.5∉(1,3]|too small", False, current_state=0),
        StateTransitionPair("1.5", "false", "1.5∈(1,3]|1<1.5≤3", True, current_state=1),
        StateTransitionPair("3.5", "false", "3.5∉[4,7)|too small", False, current_state=1),
        StateTransitionPair("7.0", "false", "7.0∉[4,7)|7 not included", False, current_state=1),
        StateTransitionPair("5.0", "true", "5.0∈[4,7]|SUCCESS", True, current_state=0),
        StateTransitionPair("3.0", "false", "3.0∈(1,3]|boundary case", True, current_state=1),
        StateTransitionPair("4.0", "true", "4.0∈[4,7)|SUCCESS", True, current_state=0),
        StateTransitionPair("4.0", "false", "4.0∉(1,3]|too large", False, current_state=0),
        StateTransitionPair("2.5", "false", "2.5∈(1,3]|1<2.5≤3", True, current_state=1),
        StateTransitionPair("8.0", "false", "8.0∉[4,7)|too large", False, current_state=1),
        StateTransitionPair("2.0", "false", "2.0∈(1,3]|restart", True, current_state=1),
        StateTransitionPair("6.5", "true", "6.5∈[4,7)|SUCCESS", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='interval_state_transition',
        speed_multiplier=1.0,
        expression_parts=["{(1,3]}", "->", "{[4,7)}"],
        line_spacing=0.4,
        column_spacing=4.5
    )


def create_range_state_transition_demo():
    """
    Create a GIF demonstrating the {>=0.5}->{<=0.8} state transition expression.
    """
    state_pairs = [
        StateTransitionPair("0.3", "false", "0.3<0.5", False, current_state=0),
        StateTransitionPair("0.2", "false", "0.2<0.5", False, current_state=0),
        StateTransitionPair("0.5", "false", "0.5≥0.5", True, current_state=1),
        StateTransitionPair("0.9", "false", "0.9>0.8", False, current_state=1),
        StateTransitionPair("1.2", "false", "1.2>0.8", False, current_state=1),
        StateTransitionPair("0.8", "true", "0.8≤0.8|completed", True, current_state=0),
        StateTransitionPair("0.7", "false", "0.7≥0.5|restart", True, current_state=1),
        StateTransitionPair("0.6", "true", "0.6≤0.8|completed", True, current_state=0),
        StateTransitionPair("0.5", "false", "0.5≥0.5|restart", True, current_state=1),
        StateTransitionPair("0.8", "true", "0.8≤0.8|completed", True, current_state=0),
        StateTransitionPair("2.0", "false", "2.0≥0.5|restart", True, current_state=1),
        StateTransitionPair("1.5", "false", "1.5>0.8", False, current_state=1),
        StateTransitionPair("0.95", "false", "0.95>0.8", False, current_state=1),
        StateTransitionPair("0.75", "true", "0.75≤0.8|completed", True, current_state=0),
    ]

    create_state_transition_gif(
        state_pairs,
        output_name='range_state_transition',
        speed_multiplier=1.1,
        expression_parts=["{>=0.5}", "->", "{<=0.8}"],
        line_spacing=0.5,
        column_spacing=3.0
    )
