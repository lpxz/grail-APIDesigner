package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

public class TypeFourPattern extends PatternII {

	TypeFourPattern(RWNode nodeI, RWNode nodeK, RWNode nodeR, RWNode nodeJ) {
		super(nodeI, nodeK, nodeR, nodeJ);
	}

	@Override
	public String toString() {
		return "*** PATTERN FOUR --- " + nodeI + " * " + nodeK + " * " + nodeR
				+ " * " + nodeJ + " ***";
	}
}
