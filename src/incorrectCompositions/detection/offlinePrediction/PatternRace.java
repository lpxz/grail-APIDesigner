package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

public class PatternRace implements Pattern {
	protected String	mem;
	protected RWNode	nodeI;
	protected RWNode	nodeII;

	public PatternRace(RWNode nodeI, RWNode nodeII) {
		this.nodeI = nodeI;
		this.nodeII = nodeII;
		this.mem = nodeI.getMemString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PatternRace) {
			PatternRace pattern = (PatternRace) o;
			RWNode I = pattern.getNodeI();
			RWNode II = pattern.getNodeII();

			if (nodeI.getLine() == I.getLine()
					&& nodeI.getMemString() == I.getMemString()
					&& nodeII.getLine() == II.getLine()
					&& nodeII.getMemString() == II.getMemString())
				return true;
			else
				return false;
		} else
			return super.equals(o);
	}

	@Override
	public String getAnormalMem() {
		return mem;
	}

	public RWNode getNodeI() {
		return nodeI;
	}

	public RWNode getNodeII() {
		return nodeII;
	}

	@Override
	public int hashCode() {
		return nodeI.getLine() << 8 + nodeII.getLine();
	}

	@Override
	public String printToString() {
		return "*** RACE --- " + mem + " --- " + nodeI.printToString() + " * "
				+ nodeII.printToString() + " ***";
	}

	public void setNodeI(RWNode nodeI) {
		this.nodeI = nodeI;
	}

	public void setNodeII(RWNode nodeII) {
		this.nodeII = nodeII;
	}

	@Override
	public String toString() {
		return "*** RACE *** " + nodeI + " * " + nodeII + " ***";
	}
}
