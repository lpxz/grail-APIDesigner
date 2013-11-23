package incorrectCompositions.detection.offlinePrediction;

import java.util.HashSet;
import java.util.LinkedList;

class DGStack {
	private LinkedList<DGNode>	st;

	// ------------------------------------------------------------
	public DGStack() // constructor
	{
		st = new LinkedList<DGNode>(); // make array
	}

	// ------------------------------------------------------------
	public boolean isEmpty() // true if nothing on stack
	{
		return st.isEmpty();
	}

	// ------------------------------------------------------------
	public DGNode peek() // peek at top of stack
	{
		return st.remove();
	}

	// ------------------------------------------------------------
	public DGNode pop() // take item off stack
	{
		return st.removeLast();
	}

	// ------------------------------------------------------------
	public void push(DGNode item) // put item on stack
	{
		st.add(item);
	}

	// ------------------------------------------------------------
	public void pushAll(HashSet<DGNode> items) {
		// TODO Auto-generated method stub
		st.addAll(items);
	}
} // end class StackX