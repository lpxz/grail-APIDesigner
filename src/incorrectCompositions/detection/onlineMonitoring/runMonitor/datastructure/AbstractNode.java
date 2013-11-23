package incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure;

import java.io.Serializable;

public class AbstractNode implements Serializable {
	public enum TYPE {
		ENTRY, EXIT, LOCK, READ, RECEIVE, SEND, UNLOCK, WRITE
	}

	/**
	 * There are three kinds of mems: SPE, thread object id, ordinary object id
	 */

	protected static final long	serialVersionUID	= 1L;

	private AbstractNode		depnode;
	protected int				ID;						// leave it alone,
															// set internally by
															// pecan
	protected int				mem;						// for locknode, it
															// is the runtime id
															// of the lock
	// for messagenode, it is the runtime id of the mutex. for example in
	// o.wait(), mem is o's runtime id.
	// for RWnode, it is the runtime id of the accessed location.
	// for methodnode, it is not important, you can set it as the method id.

	protected long				tid;						// / thread id

	protected TYPE				type;						// enum TYPE
															// READ,WRITE,LOCK,UNLOCK,SEND,RECEIVE,ENTRY,EXIT

	public AbstractNode(int mem, long tid, TYPE type) {
		this.mem = mem;
		this.tid = tid;
		this.type = type;

		if (Math.random() > 0.5)
			Thread.yield();
	}

	public boolean equals(AbstractNode node) {
		if (this.ID == node.getID()) {
			return true;
		} else
			return false;
	}

	public AbstractNode getDepNode() {
		return depnode;
	}

	public int getID() {
		return ID;
	}

	public int getMem() {
		return mem;
	}

	public long getTId() {
		return tid;
	}

	public TYPE getType() {
		return type;
	}

	public void setDepNode(AbstractNode node) {
		this.depnode = node;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	@Override
	public String toString() {
		return ID + " " + mem + " " + tid + " " + type;
	}
}
