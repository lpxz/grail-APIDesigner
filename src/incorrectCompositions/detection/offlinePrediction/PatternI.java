package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;

import java.io.Serializable;


public class PatternI implements Pattern, Serializable {
	protected String	mem;
	protected RWNode	nodeI;
	protected RWNode	nodeJ;
	protected RWNode	nodeK;

	PatternI(RWNode nodeI, RWNode nodeK, RWNode nodeJ) {
		this.nodeI = nodeI;
		this.nodeJ = nodeJ;
		this.nodeK = nodeK;
		this.mem = nodeI.getMemString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PatternI) {
			PatternI pattern = (PatternI) o;
			RWNode I = pattern.getNodeI();
			RWNode J = pattern.getNodeJ();
			RWNode K = pattern.getNodeK();

			if (nodeI.getLine() == I.getLine()
					&& nodeI.getMemString() == I.getMemString()
					&& nodeJ.getLine() == J.getLine()
					&& nodeJ.getMemString() == J.getMemString()
					&& nodeK.getLine() == K.getLine()
					&& nodeK.getMemString() == K.getMemString())
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

	public RWNode getNodeJ() {
		return nodeJ;
	}

	public RWNode getNodeK() {
		return nodeK;
	}

	@Override
	public int hashCode() {
		return nodeI.getLine() << 8 + nodeJ.getLine() << 8 + nodeK.getLine();
	}

	@Override
	public String printToString() {
		// TODO Auto-generated method stub
		return "";
	}

}
