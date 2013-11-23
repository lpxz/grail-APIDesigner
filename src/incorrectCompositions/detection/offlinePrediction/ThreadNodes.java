package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.AbstractNode.TYPE;

import java.util.HashMap;
import java.util.LinkedList;


public class ThreadNodes {
	LinkedList<RWNode>				allnodes	= new LinkedList<RWNode>();
	LinkedList<RWNode>				allrnodes	= new LinkedList<RWNode>();
	LinkedList<RWNode>				allwnodes	= new LinkedList<RWNode>();

	private int						atomIndex;
	private int						currentAtomIndex;
	HashMap<Integer, Integer>		map			= new HashMap<Integer, Integer>();

	LinkedList<LinkedList<RWNode>>	readnodes	= new LinkedList<LinkedList<RWNode>>();

	LinkedList<LinkedList<RWNode>>	rwnodes		= new LinkedList<LinkedList<RWNode>>();
	LinkedList<LinkedList<RWNode>>	writenodes	= new LinkedList<LinkedList<RWNode>>();

	public ThreadNodes(LinkedList<RWNode> nodes) {
		for (int k = 0; k < nodes.size(); k++) {
			RWNode node = nodes.get(k);

			addrwnode(node);
			if (node.getType() == TYPE.READ) {
				addreadnode(node);
			} else if (node.getType() == TYPE.WRITE) {
				addwritenode(node);
			}
		}
	}

	public void addreadnode(RWNode node) {
		allrnodes.add(node);
		if (node.getAtomIndex() >= 0) {
			readnodes.get(atomIndex).add(node);
		}
	}

	/**
	 * To overcome the trace inaccuracy, we need to do a bit trick here
	 */
	public void addrwnode(RWNode node) {
		allnodes.add(node);
		if (node.getAtomIndex() >= 0) {
			Integer index = map.get(node.getAtomIndex());
			if (index == null) {
				rwnodes.add(new LinkedList<RWNode>());
				writenodes.add(new LinkedList<RWNode>());
				readnodes.add(new LinkedList<RWNode>());

				index = currentAtomIndex;
				map.put(node.getAtomIndex(), index);
				currentAtomIndex++;
			}
			atomIndex = index;

			rwnodes.get(atomIndex).add(node);
		}
	}

	public void addwritenode(RWNode node) {
		allwnodes.add(node);

		if (node.getAtomIndex() >= 0) {
			writenodes.get(atomIndex).add(node);
		}
	}

	public LinkedList<LinkedList<RWNode>> getAllNodes() {
		return rwnodes;
	}

	public LinkedList<RWNode> getAllRNodes() {
		return allrnodes;
	}

	public LinkedList<RWNode> getAllWNodes() {
		return allwnodes;
	}

	public LinkedList<RWNode> getAllWRNodes() {
		return allnodes;
	}

	public LinkedList<RWNode> getreadnodes(int index) {
		return readnodes.get(index);
	}

	public LinkedList<RWNode> getrwnodes(int index) {
		return rwnodes.get(index);
	}

	public int getSize() {
		return rwnodes.size();
	}

	public LinkedList<RWNode> getwritenodes(int index) {
		return writenodes.get(index);
	}
}
