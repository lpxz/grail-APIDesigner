package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

import java.io.Serializable;


public class TypeTwoPattern extends PatternI implements Serializable {

	public TypeTwoPattern(RWNode nodeI, RWNode nodeK, RWNode nodeJ) {
		super(nodeI, nodeK, nodeJ);
	}

	@Override
	public String printToString() {
		return "*** AV-II --- " + nodeI.getMemString() + " --- "
				+ nodeI.printToString() + " * " + nodeK.printToString() + " * "
				+ nodeJ.printToString() + " ***";
	}

	@Override
	public String toString() {
		return "*** AV-II --- " + nodeI + " * " + nodeK + " * " + nodeJ
				+ " ***";
	}
}
