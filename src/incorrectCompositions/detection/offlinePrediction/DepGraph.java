package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.AbstractNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;


public class DepGraph {
	int										cs;
	private LinkedList<LinkedList<DGNode>>	data				= new LinkedList<LinkedList<DGNode>>();

	HashMap<DGNode, DGNode>					edgemap				= new HashMap<DGNode, DGNode>();
	int										initedgesize;
	int										initnodesize;

	HashMap<AbstractNode, DGNode>			nodetodgmap			= new HashMap<AbstractNode, DGNode>();
	LinkedList<DGNode>						ordereditems;

	boolean[][]								rrmatrix;
	private HashMap<Long, Integer>			threadIdIndexMap	= new HashMap<Long, Integer>();

	HashSet<DGNode>							visitedItems;

	public DepGraph(Vector<AbstractNode> trace) {
		HashMap<Long, DGNode> currentNodeMap = new HashMap<Long, DGNode>();
		initnodesize = 0;
		long lastThreadId = 1;

		for (int k = 0; k < trace.size(); k++) {
			AbstractNode node = trace.get(k);
			long threadId = node.getTId();
			LinkedList<DGNode> dgnodelist;

			DGNode dgnode = currentNodeMap.get(threadId);
			AbstractNode depnode = node.getDepNode();

			if (dgnode == null) {
				dgnode = new DGNode(node);
				dgnode.setIndex(initnodesize++);

				if (depnode != null) {
					DGNode depdgnode = nodetodgmap.get(depnode);
					dgnode.addRemoteInNode(depdgnode);
				}

				currentNodeMap.put(threadId, dgnode);
				dgnodelist = new LinkedList<DGNode>();
				data.add(dgnodelist);
				int index = data.size() - 1;
				threadIdIndexMap.put(threadId, index);

				dgnodelist.add(dgnode);
			} else {
				if (depnode == null) {
					dgnode.addNode(node);
				} else {

					if (threadId == lastThreadId) {
						dgnode.addNode(node);

					} else {
						dgnode = new DGNode(node);
						dgnode.setIndex(initnodesize++);

						int index = threadIdIndexMap.get(threadId);
						dgnodelist = data.get(index);
						dgnode.setPrevLocalNode(dgnodelist.getLast());

						dgnodelist.add(dgnode);
						currentNodeMap.put(threadId, dgnode);
					}

					DGNode depdgnode = nodetodgmap.get(depnode);
					dgnode.addRemoteInNode(depdgnode);
				}

			}

			// DON'T ADD LOCAL EDGE
			// edgemap.put(dgnodelist.getLast(),dgnode);
			lastThreadId = threadId;
			nodetodgmap.put(node, dgnode);

		}
	}

	private boolean canReach(DGNode leftnode, DGNode rightnode) {

		HashSet<DGNode> nodes = rightnode.getRemoteInNodes();
		if (nodes == null)
			return false;

		HashSet<DGNode> ins = new HashSet<DGNode>();
		LinkedList<DGNode> st = new LinkedList<DGNode>();
		st.addAll(nodes);
		while (!st.isEmpty()) // until stack empty,
		{
			DGNode item = st.removeFirst();

			if (item == leftnode)
				return true;

			if (!ins.contains(item)) {
				ins.add(item);
				DGNode item_prev = item.getPrevLocalNode();

				if (item_prev != null)
					st.add(item_prev);

				HashSet<DGNode> remoteins = item.getRemoteInNodes();

				if (remoteins != null)
					st.addAll(st.size(), remoteins);
			}
			// ...//BUG HERE CYCLE
		}

		return false;
	}

	public LinkedList<AbstractNode> computeTransformedTrace() {

		LinkedList<AbstractNode> transtrace = new LinkedList<AbstractNode>();
		for (int k = 0; k < ordereditems.size(); k++) {
			transtrace.addAll(transtrace.size(),
					getNodesOf(ordereditems.get(k)));
		}
		return transtrace;
	}

	public int getCS() {
		return cs;
	}

	public int getInitEdgeSize() {
		return initedgesize;
	}

	public int getInitNodeSize() {
		return initnodesize;
	}

	private LinkedList<AbstractNode> getNodesOf(DGNode node) {
		LinkedList<AbstractNode> nodes = node.getNodes();
		DGNode nextnode = node.getNextNode();
		if (nextnode != null)
			nodes.addAll(nodes.size(), getNodesOf(nextnode));

		return nodes;
	}

	/*
	 * Graph merging algorithm
	 */
	public void merge() {

		// Initialize the reachability relation
		// initializeReachabilityRelation();

		// merge graph iteratively
		for (int k = 0; k < data.size(); k++) {
			LinkedList<DGNode> dgnodelist = data.get(k);
			for (int i = dgnodelist.size() - 2; i >= 0; i--) {
				DGNode leftnode = dgnodelist.get(i);
				DGNode rightnode = dgnodelist.get(i + 1);

				// int leftIndex=leftnode.getIndex();
				// int rightIndex=rightnode.getIndex();

				// DON'T USE LOCAL EDGE
				if (!canReach(leftnode, rightnode)) {

					leftnode.setNextNode(rightnode);
					// updateReachabilityRelation(leftIndex,rightIndex);
					leftnode.addRemoteInNodes(rightnode.getRemoteInNodes());
					dgnodelist.remove(i + 1);
					if (dgnodelist.size() > i + 1)
						dgnodelist.get(i + 1).setPrevLocalNode(leftnode);
				}
			}
		}
	}

	public void topSort() {

		ordereditems = new LinkedList<DGNode>();
		visitedItems = new HashSet<DGNode>();

		for (int k = 0; k < data.size(); k++) {
			DGNode item = data.get(k).getLast();
			visit(item);
		}

		cs = ordereditems.size() - 1;

		// System.out.println("DEBUG");

	}

	private void visit(DGNode item) {
		if (!visitedItems.contains(item)) {
			visitedItems.add(item);
			DGNode item_prev = item.getPrevLocalNode();
			HashSet<DGNode> ins = item.getRemoteInNodes();

			if (ins == null)
				ins = new HashSet<DGNode>();

			if (item_prev != null)
				ins.add(item_prev);

			Iterator<DGNode> insIt = ins.iterator();
			while (insIt.hasNext()) {
				DGNode initem = insIt.next();
				visit(initem);
			}

			ordereditems.add(item);

		}

	}

}
