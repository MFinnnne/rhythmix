'''
Author: MFine
Date: 2025-10-05 14:56:34
LastEditTime: 2025-10-05 15:21:24
LastEditors: MFine
Description: 
'''
#!/usr/bin/env python3
"""
Test script for the new filter(>0).window(3).avg().meet(>50) animation.
"""

import sys
from pathlib import Path

# Add the doc directory to the path
doc_dir = Path(__file__).parent
sys.path.insert(0, str(doc_dir))

from demos.chain_expression_demos import create_filter_window_avg_meet_demo

if __name__ == "__main__":
    print("Creating filter(>0).window(3).avg().meet(>50) animation...")
    create_filter_window_avg_meet_demo()
    print("Animation created successfully!")
