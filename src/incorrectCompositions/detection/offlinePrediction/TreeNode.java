package incorrectCompositions.detection.offlinePrediction;

import java.util.Set;

public class TreeNode {
	private Set<Integer>	childs;

	TreeNode(Integer key) {
	}

	public void addChild(Integer child) {
		childs.add(child);
	}
}
