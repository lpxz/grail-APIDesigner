package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

public class TypeFivePattern extends PatternII {

	TypeFivePattern(RWNode nodeI, RWNode nodeK, RWNode nodeR, RWNode nodeJ) {
		super(nodeI, nodeK, nodeR, nodeJ);
	}

	@Override
	public String toString() {
		return "*** PATTERN FIVE --- " + nodeI + " * " + nodeK + " * " + nodeR
				+ " * " + nodeJ + " ***";
	}
}
