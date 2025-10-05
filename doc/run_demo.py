import sys
from demos.chain_expression_demos import create_chain_expression_demo, create_chain_expression_demo_with_queue_line, create_filter_window_avg_meet_demo
from demos.count_demos import create_count_constraint_demo, create_multi_count_state_transition_demo, create_count_demo
from demos.state_transition_demos import create_state_transition_demo, create_complex_state_transition_demo, \
    create_equal_chain_state_transition_demo, create_logical_and_state_transition_demo, \
    create_interval_state_transition_demo, create_range_state_transition_demo

# A dictionary mapping demo names to functions
AVAILABLE_DEMOS = {
    "chain_expression": create_chain_expression_demo,
    "chain_expression_with_queue": create_chain_expression_demo_with_queue_line,
    "filter_window_avg_meet": create_filter_window_avg_meet_demo,
    "count": create_count_demo,
    "count_constraint": create_count_constraint_demo,
    "multi_count_state_transition": create_multi_count_state_transition_demo,
    "state_transition": create_state_transition_demo,
    "complex_state_transition": create_complex_state_transition_demo,
    "equal_chain_state_transition": create_equal_chain_state_transition_demo,
    "logical_and_state_transition": create_logical_and_state_transition_demo,
    "interval_state_transition": create_interval_state_transition_demo,
    "range_state_transition": create_range_state_transition_demo,
}

def run_demo(name):
    """Runs a demo by its name."""
    demo_func = AVAILABLE_DEMOS.get(name)
    if demo_func:
        print(f"Running demo: {name}")
        demo_func()
        print(f"Finished demo: {name}")
    else:
        print(f"Error: Demo '{name}' not found.")
        print("Available demos are:")
        for demo_name in AVAILABLE_DEMOS:
            print(f"  - {demo_name}")
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python run_demo.py <demo_name>")
        print("Available demos are:")
        for demo_name in AVAILABLE_DEMOS:
            print(f"  - {demo_name}")
        sys.exit(1)
    
    demo_name_to_run = sys.argv[1]
    run_demo(demo_name_to_run)
