package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.AbstractNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.LockNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class LockHistory {
	Vector<Object>			lock_index	= new Vector<Object>();
	Map<Integer, Integer>	lock_types	= new HashMap<Integer, Integer>();
	/**
	 * This is the input trace containing all the events of a thread PLEASE
	 * DON'T MODIFY IT!!!
	 */
	Vector<LockNode>		locktrace;
	int						N			= 0;
	int						n_type		= 0;

	Map<Integer, Integer>	type_locks	= new HashMap<Integer, Integer>();

	public LockHistory(Vector<LockNode> trace) {
		int i;

		this.locktrace = trace;
		N = trace.size();
		// System.err.println( "Start building index.... The trace length is : "
		// + N );

		// We scan the whole trace to build up a list for each type of lock
		for (i = 0; i < N; ++i) {
			LockNode n = trace.get(i);

			int lt = n.getMem();
			Object obj = null;

			if (lock_types.containsKey(lt)) {
				obj = lock_index.get(lock_types.get(lt).intValue());
			} else {
				lock_types.put(n.getMem(), n_type);
				type_locks.put(n_type, n.getMem());

				++n_type;
				obj = new Vector<Integer>();
				lock_index.add(obj);
			}

			// Now we push the item into the corresponding vector
			Vector<Integer> vec = (Vector<Integer>) obj;
			vec.add(n.getID());
		}

		// System.err.println( "We have " + lock_types.size() +
		// " different types of locks." );
	}

	/**
	 * 
	 * @param node1
	 * @param node2
	 * @pre-condition: node1 and node2 are from the same thread --
	 *                 node1.getTid() = node2.getTid() node1 happens before
	 *                 node2 -- node1.getID() < node2.getID()
	 * @return the lock acquire history of the same thread between the events
	 *         node1 and node2
	 */
	public Set<Integer> getLockHistory(AbstractNode node1, AbstractNode node2) {
		int i;
		int n1, n2;
		int inx1, inx2;
		Set<Integer> lockhistory = new HashSet<Integer>();

		// n1 < n2, which is guaranteed by the input
		n1 = node1.getID();
		n2 = node2.getID();

		for (i = 0; i < n_type; ++i) {
			Vector<Integer> vec = (Vector<Integer>) lock_index.get(i);
			inx1 = upper_bound(vec, n1);
			inx2 = lower_bound(vec, n2);
			if (inx2 >= inx1)
				lockhistory.add(type_locks.get(i));
		}

		return lockhistory;
	}

	private int lower_bound(Vector<Integer> vec, int x) {
		int s, e, mid;

		s = 0;
		e = vec.size() - 1;
		mid = (s + e) / 2;
		while (s <= e) {
			mid = (s + e) / 2;
			int value = vec.get(mid).intValue();

			if (value < x)
				s = mid + 1;
			else if (value > x)
				e = mid - 1;
			else
				return mid;

		}

		return e;
	}

	private int upper_bound(Vector<Integer> vec, int x) {
		int s, e, mid;

		s = 0;
		e = vec.size() - 1;
		mid = (s + e) / 2;
		while (s <= e) {
			mid = (s + e) / 2;
			int value = vec.get(mid).intValue();

			if (value > x)
				e = mid - 1;
			else if (value < x)
				s = mid + 1;
			else
				return mid;

		}

		return s;
	}
}
