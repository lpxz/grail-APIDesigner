package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

public class TypeThreePattern extends PatternII {
	TypeThreePattern(RWNode nodeI, RWNode nodeK, RWNode nodeR, RWNode nodeJ) {
		super(nodeI, nodeK, nodeR, nodeJ);
	}

	@Override
	public String toString() {
		return "*** AV-III --- " + nodeI + " * " + nodeK + " * " + nodeR
				+ " * " + nodeJ + " ***";
	}
}
