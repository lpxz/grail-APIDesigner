package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.AbstractNode;

import java.util.HashSet;
import java.util.LinkedList;

public class DGNode {

	private int					index;
	DGNode						nextNode;
	LinkedList<AbstractNode>	nodes;
	DGNode						prev_node;

	HashSet<DGNode>				remoteIns;

	DGNode(AbstractNode node) {
		nodes = new LinkedList<AbstractNode>();
		nodes.add(node);

	}

	public void addNode(AbstractNode node) {
		nodes.add(node);
	}

	public void addRemoteInNode(DGNode node) {
		if (remoteIns == null) {
			remoteIns = new HashSet<DGNode>();
		}
		remoteIns.add(node);
	}

	public void addRemoteInNodes(HashSet<DGNode> nodes) {
		if (remoteIns == null) {
			remoteIns = new HashSet<DGNode>();
		}
		remoteIns.addAll(nodes);
	}

	public int getIndex() {
		return index;
	}

	public DGNode getNextNode() {
		return nextNode;
	}

	public LinkedList<AbstractNode> getNodes() {
		// TODO Auto-generated method stub
		return nodes;
	}

	public DGNode getPrevLocalNode() {
		return prev_node;
	}

	public HashSet<DGNode> getRemoteInNodes() {
		return remoteIns;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setNextNode(DGNode node) {
		this.nextNode = node;

	}

	public void setPrevLocalNode(DGNode node) {
		this.prev_node = node;
	}
}
