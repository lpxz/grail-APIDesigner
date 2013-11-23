package incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class RWNode extends AbstractNode {

	private int					atom_index	= -1;	// leave this field alone

	private LinkedList<Integer>	context;			// leave this field alone
	Vector						intContext;		// lpxz: I use this one for the context. not the above field.
													// main/run comes at the last entry.

	public String				jcode		= null; // which jimple IR (soot IR)
													// represents the access?
	private int					line;				// which line does the
	public Set					lockset;			// leave this field alone
	private String				mem_str;			// which memory location is

	// field.
	public String				msig		= null; // which method does the

	// accessed?
	private int					newmem;			// identical as the above

	private int					t_index;			// leave this field alone

	public RWNode(int line, int mem, long tid, TYPE type) {
		super(mem, tid, type);

		this.line = line;
	}

	public RWNode(int mem, long tid, TYPE type) {
		super(mem, tid, type);
	}

	public int getAtomIndex() {
		return atom_index;
	}

	public LinkedList<Integer> getContext() {
		return context;
	}

	public Vector getIntContext() {
		return intContext;
	}

	public String getJcode() {
		return jcode;
	}

	public int getLine() {
		return line;
	}

	public Set getLockSet() {
		return lockset;
	}

	public String getMemString() {
		if (mem_str != null)
			return mem_str;
		else
			return "";
	}

	public String getMsig() {
		return msig;
	}

	public int getNewMem() {
		return newmem;
	}

	public int getTIndex() {
		return t_index;
	}

	public String printToString() {
		String ctxt_lp = "";

		return ID + " " + line + " " + tid + " " + type + " msig:" + msig
				+ " jcode:" + jcode + " ctxt:\n" + ctxt_lp; // ctxt_lp
	}

	public String printToString(List steVector) {
		String ctxt_lp = "";

		for (int i = steVector.size() - 1; i >= 0; i--) {
			String newCtxt = steVector.get(i).toString();

			ctxt_lp = ctxt_lp + newCtxt + "\n";

		}

		return ID + " " + line + " " + tid + " " + type + " msig:" + msig
				+ " jcode:" + jcode + " ctxt:\n" + ctxt_lp; // ctxt_lp
	}

	public void setAtomIndex(int atom_index) {
		this.atom_index = atom_index;
	}

	public void setContext(LinkedList<Integer> context) {
		this.context = context;
	}

	public void setIntContext(Vector contextarg) {
		this.intContext = contextarg;
	}

	public void setJcode(String jcode) {
		this.jcode = jcode;
	}

	public void setLockSet(Set<Integer> lockset) {
		this.lockset = lockset;
	}

	public void setMemString(String mem_str) {
		this.mem_str = mem_str;
	}

	public void setMsig(String msig) {
		this.msig = msig;
	}

	public void setNewMem(int mem) {
		this.newmem = mem;
	}

	public void setTIndex(int t_index) {
		this.t_index = t_index;
	}

	@Override
	public String toString() {
		if (mem_str == null)
			return ID + " " + mem + " " + line + " " + tid + " " + type + " "
					+ getJcode();
		else
			return ID + " " + mem_str + " " + line + " " + tid + " " + type
					+ " " + getJcode();
	}

}