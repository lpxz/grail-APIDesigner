package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

import java.io.Serializable;


public class TypeOnePattern extends PatternI implements Serializable {

	public TypeOnePattern(RWNode nodeI, RWNode nodeK, RWNode nodeJ) {
		super(nodeI, nodeK, nodeJ);
	}

	@Override
	public String printToString() {
		return "*** AV-I --- " + nodeI.getMemString() + " --- "
				+ nodeI.printToString() + " * " + nodeK.printToString() + " * "
				+ nodeJ.printToString() + " ***";
	}

	@Override
	public String toString() {
		return "*** AV-I --- " + nodeI + " * " + nodeK + " * " + nodeJ + " ***";
	}
}
