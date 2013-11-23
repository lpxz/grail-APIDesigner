package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.AbstractNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.LockNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.MessageNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.MethodNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.RWNode;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.datastructure.AbstractNode.TYPE;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context.PreciseSTEContextAnalyzer;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context.PreciseSTEContextEncoderDecoder;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context.PreciseSTEContextHelper;
import incorrectCompositions.detection.onlineMonitoring.runMonitor.monitor.MonitorData;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.tools.ant.taskdefs.condition.And;

import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

import utils.CentralConstants;
import utils.Options;

public class TraceEngine {
	public static HashSet<String>	invokes		= new HashSet<String>();
	private static String			lineDir;

	public static int				numOfBugs	= 0;
	public static HashSet<String>	pcrStrings	= new HashSet<String>();

	private static String			tidDir;

	public static void addRTid2Vector(
			HashMap<Integer, Vector<RWNode>> rtID2Vector, int rtID, RWNode node) {
		Vector<RWNode> vector = rtID2Vector.get(rtID);
		if (vector == null) {
			vector = new Vector<RWNode>();
			rtID2Vector.put(rtID, vector);
		}

		vector.add(node);

	}

	private static void removeNonReadWriteNodes(Vector<AbstractNode> trace) {

		for (int k = 0; k < trace.size(); k++) {
			AbstractNode node = trace.get(k);
			if (node.getType() != TYPE.READ && node.getType() != TYPE.WRITE) {
				trace.remove(k);
				k--;
			}
		}
	}

	private LinkedList<HashMap<Long, ThreadNodes>>							accessThreadNodes;
	private Vector<RWNode>[]												accessvec;

	private HashMap<Pattern, AbstractNode>									atompatternToNodeMap			= new HashMap<Pattern, AbstractNode>();

	private boolean															atomRegionAll					= false;
	private boolean															checkAtomicity					= false;
	private boolean															checkAtomSet					= false;

	private boolean															checkRace						= false;
	private String															classname;
	int																		count							= 0;
	private int																ctx_size						= -1;
	List																	cv_list							= new ArrayList();
	/**
	 * The next message passing node of the current node from the same thread
	 */
	private HashMap<Integer, Integer>										determinNodeMap					= new HashMap<Integer, Integer>();
	private Vector<AbstractNode>											maintrace;
	private int																MaxPatternPerVar				= 100;
	private int																MaxRedundantLockComputation		= 1000;

	private int																MaxRedundantPatternComputation	= 1000;
	public MonitorData														monitorData;

	private int																numberOfLockNodes;

	private int																numberOfMessageNodes;
	private int																numberOfReadWriteNodes;
	private int																numberOfSharedVariables;

	private int																numberOfThreads;

	private int																numberOfTotalNodes;
	private int																numberOfTotalNonMethodEntryExitNodes;

	private HashMap<String, HashSet<Pattern>>								patterns						= new HashMap<String, HashSet<Pattern>>();
	private PartialOrderRelation											por;

	List																	pv_list							= new ArrayList();

	private boolean															removemain						= false;
	private boolean															removeRedundance				= false;

	// private HashMap<Integer, HashMap<Integer, Vector<RWNode>>> speHashMap;
	private HashMap<Integer, Vector<RWNode>>								rtID2Vector;

	List																	rv_list							= new ArrayList();

	private HashMap<Long, LinkedList<HashMap<Integer, LinkedList<RWNode>>>>	threadAtomAccessList			= new HashMap<Long, LinkedList<HashMap<Integer, LinkedList<RWNode>>>>();

	private HashMap<Long, Integer>											threadAtomIndex					= new HashMap<Long, Integer>();

	// private HashMap<Long,LinkedList<Integer>> threadCurrentContext = new
	// HashMap<Long,LinkedList<Integer>>();
	//	private HashMap<Long, List<ContextMethod>>								threadCurrentContext_lp			= new HashMap<Long, List<ContextMethod>>();
	private LinkedList<Long>												threadIDList					= new LinkedList<Long>();

	private HashMap<Long, LockHistory>										threadLockHistory				= new HashMap<Long, LockHistory>();

	private HashMap<Long, Vector<LockNode>>									threadLockNodes					= new HashMap<Long, Vector<LockNode>>();
	private HashMap<Long, Set<Integer>>										threadLockSet					= new HashMap<Long, Set<Integer>>();
	private HashMap<Long, Vector<RWNode>>									threadRWNodes					= new HashMap<Long, Vector<RWNode>>();
	private int																transPatternNumber;

	public TraceEngine(MonitorData mondata) {
		monitorData = mondata;

		classname = mondata.getClassName();
		maintrace = mondata.getTrace();
		numberOfTotalNodes = maintrace.size();
		rtID2Vector = mondata.getRTid2Vector();
		if (rtID2Vector.isEmpty()) {
			// HK forgets it, I help.
			for (AbstractNode node : maintrace) {
				if (node instanceof RWNode) {
					int rtId = node.getMem();
					addRTid2Vector(rtID2Vector, rtId, (RWNode) node);
				}
			}

		}
		accessvec = computeInstanceAccessVec();

	}

	/**
	 * 
	 * Two nodes by different threads
	 */
	private boolean canNotReach(RWNode node1, RWNode node2) {
		// COMPUTE THE PARTIAL ORDER CONSTAINT ON DEMAND
		AbstractNode nodea = getDetNode(node1);
		if (nodea == null)
			return true;

		int id1 = nodea.getID();

		AbstractNode nodeb = getDetDNode(node2);

		int id2 = nodeb.getID();

		if (id2 == 1)
			return true;

		return por.canNotReach(id1, id2);
	}

	private LinkedList<HashMap<Long, LinkedList<RWNode>>> computeAccessList() {
		LinkedList<HashMap<Long, LinkedList<RWNode>>> acclist = new LinkedList<HashMap<Long, LinkedList<RWNode>>>();

		for (int i = 0; i < accessvec.length; i++) {
			HashMap<Long, LinkedList<RWNode>> map = new HashMap<Long, LinkedList<RWNode>>();
			int size = accessvec[i].size();
			if (size > 0) {
				acclist.add(map);
				for (int j = 0; j < size; j++) {
					RWNode node = accessvec[i].get(j);
					long tid = node.getTId();
					if (map.get(tid) == null) {
						map.put(tid, new LinkedList<RWNode>());
					}

					map.get(tid).add(node);
				}
			}
		}
		return acclist;
	}

	private LinkedList<HashMap<Long, ThreadNodes>> computeAccessThreadNodes(
			LinkedList<HashMap<Long, LinkedList<RWNode>>> acclist) {
		LinkedList<HashMap<Long, ThreadNodes>> accThreadNodes = new LinkedList<HashMap<Long, ThreadNodes>>();

		for (int i = 0; i < acclist.size(); i++) {
			HashMap<Long, ThreadNodes> map = new HashMap<Long, ThreadNodes>();
			accThreadNodes.add(map);

			HashMap<Long, LinkedList<RWNode>> accmap = acclist.get(i);
			Iterator<Long> tidIt = accmap.keySet().iterator();
			while (tidIt.hasNext()) {
				Long threadId = tidIt.next();
				LinkedList<RWNode> nodes = accmap.get(threadId);
				map.put(threadId, new ThreadNodes(nodes));
			}
		}
		return accThreadNodes;
	}

	private Vector<RWNode>[] computeInstanceAccessVec() {
		int numOfVectors = rtID2Vector.values().size();
		Vector<RWNode>[] instanceaccessvec = new Vector[numOfVectors];
		int index = 0;
		for (Vector<RWNode> eachV : rtID2Vector.values()) {
			instanceaccessvec[index] = eachV;
			index++;
		}
		return instanceaccessvec;
	}

	public void computePostStatistics() {
		Iterator<String> keyIt = patterns.keySet().iterator();
		while (keyIt.hasNext()) {
			String mem = keyIt.next();

			Iterator<Pattern> patternsIt = patterns.get(mem).iterator();
			while (patternsIt.hasNext()) {
				Pattern p = patternsIt.next();
				if (p instanceof PatternRace) {
				} else if (p instanceof PatternI) {
					if (p instanceof TypeOnePattern) {
					} else {
					}
				} else {
					if (p instanceof TypeThreePattern) {
					} else if (p instanceof TypeFourPattern) {
					} else {
					}
				}
			}
		}
	}

	private HashMap<Long, LinkedList<HashMap<Integer, LinkedList<RWNode>>>> computeThreadAtomAccessList() {
		HashMap<Long, LinkedList<HashMap<Integer, LinkedList<RWNode>>>> threadAtomAccessList = new HashMap<Long, LinkedList<HashMap<Integer, LinkedList<RWNode>>>>();
		HashMap<Long, HashMap<Integer, Integer>> threadAtomIndexMap = new HashMap<Long, HashMap<Integer, Integer>>();
		for (int mem = 0; mem < accessThreadNodes.size(); mem++) {
			HashMap<Long, ThreadNodes> map = accessThreadNodes.get(mem);
			Set<Long> mapkeyset = map.keySet();
			// if(mapkeyset.size()>1)
			{
				Iterator<Long> mapIt = mapkeyset.iterator();
				while (mapIt.hasNext()) {
					Long tid = mapIt.next();
					LinkedList<HashMap<Integer, LinkedList<RWNode>>> atomAccessList = threadAtomAccessList
							.get(tid);
					if (atomAccessList == null) {
						atomAccessList = new LinkedList<HashMap<Integer, LinkedList<RWNode>>>();
						threadAtomAccessList.put(tid, atomAccessList);
						threadAtomIndexMap.put(tid,
								new HashMap<Integer, Integer>());
					}
					HashMap<Integer, Integer> atomIndexMap = threadAtomIndexMap
							.get(tid);
					LinkedList<LinkedList<RWNode>> listnodes = map.get(tid)
							.getAllNodes();
					for (int k = 0; k < listnodes.size(); k++) {
						LinkedList<RWNode> nodes = listnodes.get(k);
						Integer atomIndex = nodes.getFirst().getAtomIndex();
						Integer pos = atomIndexMap.get(atomIndex);
						if (pos == null) {
							pos = atomAccessList.size();
							atomAccessList
									.add(new HashMap<Integer, LinkedList<RWNode>>());
							atomIndexMap.put(atomIndex, pos);
						}
						HashMap<Integer, LinkedList<RWNode>> memlistmap = atomAccessList
								.get(pos);
						LinkedList<RWNode> list = memlistmap.get(mem);
						if (list == null) {
							list = new LinkedList<RWNode>();
							memlistmap.put(mem, list);
						}
						list.addAll(nodes);

					}
				}
			}
		}
		return threadAtomAccessList;
	}

	private void createFileWriter() {
		String filename = classname;
		filename = filename + ".txt";
		CommonUtil.createFileWriter(filename);
	}

	/**
	 * Find five types of patterns. First use partial order constraints; Then
	 * use locking constraints
	 */
	public void findAllPatterns() {
		CommonUtil
				.printerr("\n*** Violation Patterns [ID - Memory Location - Line Number - Thread - Access Type] ***\n");

		for (int i = 0; i < accessThreadNodes.size(); i++) {

			HashMap<Long, ThreadNodes> map = accessThreadNodes.get(i);
			Set<Long> mapkeyset = map.keySet();
			if (mapkeyset.size() > 1) {
				Iterator<Long> mapIt = mapkeyset.iterator();
				while (mapIt.hasNext()) {
					Long tid = mapIt.next();

					if (tid != 1 || !removemain) {
						//						if (checkRace) {
						//
						//							findRace(map, tid);
						//						}

						if (checkAtomicity) {
							findAtomicityPatterns(map, tid);
						}
					}
				}
			}
		}

		if (checkAtomSet) {
			findAtomSetPatterns();
		}

	}

	private void findAtomicityPatternOne(HashMap<Long, ThreadNodes> map,
			Long tid) {
		// COUNT number of computations
		int numberofredundantpatterncomputations = 0;
		int numberofredundantlockcomputations = 0;

		LinkedList<RWNode> writenodes = map.get(tid).getAllWNodes();
		for (int j = 0; j < writenodes.size(); j++) {
			RWNode wnode = writenodes.get(j);

			Iterator<Long> mapIt2 = map.keySet().iterator();
			while (mapIt2.hasNext()) {
				Long tid2 = mapIt2.next();
				if (tid != tid2 && (tid2 != 1 || !removemain)) {
					int tid2AtomSize = map.get(tid2).getSize();
					for (int tid2AtomIndex = 0; tid2AtomIndex < tid2AtomSize; tid2AtomIndex++) {
						LinkedList<RWNode> nodes = map.get(tid2).getrwnodes(
								tid2AtomIndex);
						if (nodes.size() > 1) {
							// PRE CHECK PARTIAL ORDER CONSTRAINTS
							boolean value = false;
							try {
								value = canNotReach(wnode, nodes.getFirst())
										&& canNotReach(nodes.getLast(), wnode);
							} catch (Exception e) {
								System.out.println("null exceptions");
								value = canNotReach(wnode, nodes.getFirst())
										&& canNotReach(nodes.getLast(), wnode);
							}

							if (value) {
								for (int k = 0; k < nodes.size(); k++) {
									RWNode firstnode = nodes.get(k);
									if (!canNotReach(wnode, firstnode))
										break;

									for (int m = nodes.size() - 1; m > k; m--) {
										RWNode lastnode = nodes.get(m);
										if (!canNotReach(lastnode, wnode))
											break;

										Pattern p = new TypeOnePattern(
												firstnode, wnode, lastnode);

										if (!isRedundantPattern(p)) {
											int K = wnode.getID();
											int I = firstnode.getID();
											int J = lastnode.getID();

											if (K > I && K < J) {
												saveAtomPattern(p, firstnode);
											} else {
												if (wnode.getLockSet() != null
														&& firstnode
																.getLockSet() != null
														&& lastnode
																.getLockSet() != null) {
													Set<Integer> locks = new HashSet<Integer>(
															wnode.getLockSet());
													locks.retainAll(firstnode
															.getLockSet());
													if (locks.size() == 0) {
														// PATTERN SATISFIED
														saveAtomPattern(p,
																firstnode);

													} else {
														Vector<RWNode> flnodes = threadRWNodes
																.get(firstnode
																		.getTId());

														locks = new HashSet<Integer>(
																wnode.getLockSet());
														locks.retainAll(lastnode
																.getLockSet());
														if (locks.size() == 0) {
															// PATTERN SATISFIED
															saveAtomPattern(
																	p,
																	flnodes.get(flnodes
																			.indexOf(lastnode) - 1));
														} else {
															// IF WE CAN HERE,
															// PRINT MESSAGE
															// System.err.println("\n*** INEFFICIENT LOCKING CONSTRAINT ***\n");

															// JUST USE A SIMPLE
															// AND EFFICIENT WAY
															// HOPE FOR THE BEST
															// AT THIS MOMENT
															int firstIndex = flnodes
																	.indexOf(firstnode) + 1;
															int lastIndex = flnodes
																	.indexOf(lastnode) - 1;
															// CHECK LOCKING
															// CONSTRAINTS
															if (firstIndex <= lastIndex) {
																RWNode nodex = flnodes
																		.get((firstIndex + lastIndex) / 2);

																locks = new HashSet<Integer>(
																		wnode.getLockSet());
																locks.retainAll(nodex
																		.getLockSet());
																if (locks
																		.size() == 0) {
																	// PATTERN
																	// SATISFIED
																	saveAtomPattern(
																			p,
																			nodex);
																} else {
																	nodex = flnodes
																			.get(firstIndex);
																	locks = new HashSet<Integer>(
																			wnode.getLockSet());
																	locks.retainAll(nodex
																			.getLockSet());
																	if (locks
																			.size() == 0) {
																		// PATTERN
																		// SATISFIED
																		saveAtomPattern(
																				p,
																				nodex);
																	} else {
																		nodex = flnodes
																				.get(lastIndex);
																		locks = new HashSet<Integer>(
																				wnode.getLockSet());
																		locks.retainAll(nodex
																				.getLockSet());
																		if (locks
																				.size() == 0) {
																			// PATTERN
																			// SATISFIED
																			saveAtomPattern(
																					p,
																					nodex);
																		} else {
																			if (++numberofredundantlockcomputations > MaxRedundantLockComputation)
																				return;
																		}
																	}

																}
															} else {
															}
														}
													}
												}
											}
										} else {
											if (++numberofredundantpatterncomputations > MaxRedundantPatternComputation)
												return;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// System.out.println(numberofcomputations);
	}

	private void findAtomicityPatterns(HashMap<Long, ThreadNodes> map, Long tid) {
		findAtomicityPatternOne(map, tid);
		findAtomicityPatternTwo(map, tid);
	}

	private void findAtomicityPatternTwo(HashMap<Long, ThreadNodes> map,
			Long tid) {
		// COUNT number of computations
		int numberofredundantpatterncomputations = 0;
		int numberofredundantlockcomputations = 0;

		LinkedList<RWNode> nodes = map.get(tid).getAllRNodes();
		for (int j = 0; j < nodes.size(); j++) {
			RWNode rnode = nodes.get(j);
			Iterator<Long> mapIt2 = map.keySet().iterator();
			while (mapIt2.hasNext()) {
				Long tid2 = mapIt2.next();
				if (tid < tid2)// &&(tid2!=1||!removemain))
				{
					int tid2AtomSize = map.get(tid2).getSize();
					for (int tid2AtomIndex = 0; tid2AtomIndex < tid2AtomSize; tid2AtomIndex++) {
						LinkedList<RWNode> wnodes = map.get(tid2)
								.getwritenodes(tid2AtomIndex);
						if (wnodes.size() > 1) {
							// PRE CHECK PARTIAL ORDER CONSTRAINTS
							if (canNotReach(rnode, wnodes.getFirst())
									&& canNotReach(wnodes.getLast(), rnode)) {

								for (int k = 0; k < wnodes.size(); k++) {
									RWNode firstwnode = wnodes.get(k);
									if (!canNotReach(rnode, firstwnode))
										break;

									for (int m = wnodes.size() - 1; m > k; m--) {
										RWNode lastwnode = wnodes.get(m);
										if (!canNotReach(lastwnode, rnode))
											break;

										Pattern p = new TypeTwoPattern(
												firstwnode, rnode, lastwnode);

										if (!isRedundantPattern(p)) {
											int K = rnode.getID();
											int I = firstwnode.getID();
											int J = lastwnode.getID();

											if (K > I && K < J) {
												// showRealPattern(p);
											} else {
												if (rnode.getLockSet() != null
														&& firstwnode
																.getLockSet() != null
														&& lastwnode
																.getLockSet() != null) {
													Set<Integer> locks = new HashSet<Integer>(
															rnode.getLockSet());
													locks.retainAll(firstwnode
															.getLockSet());
													if (locks.size() == 0) {
														// PATTERN SATISFIED
														saveAtomPattern(p,
																firstwnode);
													} else {
														Vector<RWNode> flnodes = threadRWNodes
																.get(firstwnode
																		.getTId());

														locks = new HashSet<Integer>(
																rnode.getLockSet());
														locks.retainAll(lastwnode
																.getLockSet());
														if (locks.size() == 0) {
															// PATTERN SATISFIED
															saveAtomPattern(
																	p,
																	flnodes.get(flnodes
																			.indexOf(lastwnode) - 1));
														} else {
															int firstIndex = flnodes
																	.indexOf(firstwnode) + 1;
															int lastIndex = flnodes
																	.indexOf(lastwnode) - 1;
															// CHECK LOCKING
															// CONSTRAINTS
															if (firstIndex <= lastIndex) {
																RWNode nodex = flnodes
																		.get((firstIndex + lastIndex) / 2);

																locks = new HashSet<Integer>(
																		rnode.getLockSet());
																locks.retainAll(nodex
																		.getLockSet());
																if (locks
																		.size() == 0) {
																	// PATTERN
																	// SATISFIED
																	saveAtomPattern(
																			p,
																			nodex);
																} else {
																	nodex = flnodes
																			.get(firstIndex);
																	locks = new HashSet<Integer>(
																			rnode.getLockSet());
																	locks.retainAll(nodex
																			.getLockSet());
																	if (locks
																			.size() == 0) {
																		// PATTERN
																		// SATISFIED
																		saveAtomPattern(
																				p,
																				nodex);
																	} else {
																		nodex = flnodes
																				.get(lastIndex);
																		locks = new HashSet<Integer>(
																				rnode.getLockSet());
																		locks.retainAll(nodex
																				.getLockSet());
																		if (locks
																				.size() == 0) {
																			// PATTERN
																			// SATISFIED
																			saveAtomPattern(
																					p,
																					nodex);
																		} else {
																			if (++numberofredundantlockcomputations > MaxRedundantLockComputation)
																				return;
																		}
																	}

																}
															}

														}
													}
												}

											}
										} else {
											if (++numberofredundantpatterncomputations > MaxRedundantPatternComputation)
												return;
										}
									}
								}
							}
						}
					}

				}
			}
		}
	}

	private void findAtomSetPatterns() {
		if (removemain)
			threadIDList.remove();

		for (int i = 0; i < threadIDList.size() - 1; i++) {
			Long tid1 = threadIDList.get(i);
			LinkedList<HashMap<Integer, LinkedList<RWNode>>> atomAccessList1 = threadAtomAccessList
					.get(tid1);
			if (atomAccessList1 != null)
				for (int atomIndex1 = 0; atomIndex1 < atomAccessList1.size(); atomIndex1++) {
					HashMap<Integer, LinkedList<RWNode>> accessmap1 = atomAccessList1
							.get(atomIndex1);

					if (accessmap1.keySet().size() > 1) {
						HashSet<Integer> memSet = new HashSet<Integer>(
								accessmap1.keySet());

						for (int j = i + 1; j < threadIDList.size(); j++) {
							Long tid2 = threadIDList.get(j);
							LinkedList<HashMap<Integer, LinkedList<RWNode>>> atomAccessList2 = threadAtomAccessList
									.get(tid2);
							if (atomAccessList2 != null)
								for (int atomIndex2 = 0; atomIndex2 < atomAccessList2
										.size(); atomIndex2++) {
									HashMap<Integer, LinkedList<RWNode>> accessmap2 = atomAccessList2
											.get(atomIndex2);
									memSet.retainAll(accessmap2.keySet());
									if (memSet.size() > 1) {
										Integer[] memarray = new Integer[memSet
												.size()];
										Iterator<Integer> memSetIt = memSet
												.iterator();
										int memPos = 0;
										while (memSetIt.hasNext()) {
											memarray[memPos++] = memSetIt
													.next();
										}

										for (int m1index = 0; m1index < memarray.length - 1; m1index++)
											for (int m2index = m1index + 1; m2index < memarray.length; m2index++) {
												Integer mem1 = memarray[m1index];
												Integer mem2 = memarray[m2index];

												searchAtomSetPatterns(
														accessmap1.get(mem1),
														accessmap1.get(mem2),
														accessmap2.get(mem1),
														accessmap2.get(mem2));
											}
									}
								}

						}
					}

				}
		}
	}

	private void findRace(HashMap<Long, ThreadNodes> map, Long tid) {
		LinkedList<RWNode> nodes = map.get(tid).getAllWRNodes();
		for (int j = 0; j < nodes.size(); j++) {
			RWNode node = nodes.get(j);

			Iterator<Long> mapIt2 = map.keySet().iterator();
			while (mapIt2.hasNext()) {
				// COUNT number of computations
				int numberofredundantpatterncomputations = 0;

				Long tid2 = mapIt2.next();
				if (tid < tid2)// &&(tid2!=1||!removemain))
				{
					LinkedList<RWNode> nodes2 = map.get(tid2).getAllWRNodes();
					if (nodes2.size() > 0) {
						// PRE CHECK PARTIAL ORDER CONSTRAINTS

						if (canNotReach(node, nodes2.getFirst())
								&& canNotReach(nodes2.getLast(), node)) {
							for (int k = 0; k < nodes2.size(); k++) {
								RWNode node2 = nodes2.get(k);

								if (node.getType() == TYPE.WRITE
										|| node2.getType() == TYPE.WRITE) {
									if (!canNotReach(node, node2)
											|| !canNotReach(node2, node))
										break;

									if (node.getLockSet() != null
											&& node2.getLockSet() != null) {
										Set<Integer> locks = new HashSet<Integer>(
												node.getLockSet());
										locks.retainAll(node2.getLockSet());
										if (locks.size() == 0) {
											saveRacePattern(node, node2);
											// if(node.getMemString().contains("_available"))
											// System.out.print(true);

										}
									}
								}
								if (++numberofredundantpatterncomputations > 100)
									break;
							}
						}
					}
				}
			}
		}
	}

	private Vector<AbstractNode> getAFreshTrace(int bIndex) {

		Vector<AbstractNode> trace = new Vector<AbstractNode>();
		for (int i = 0; i <= bIndex; i++) {
			trace.add(maintrace.get(i));
		}
		return trace;
	}

	/**
	 * TODO: Rewrite this function - USE threadIdset and then memoryset
	 * 
	 * @param aIndex
	 * @param bIndex
	 * @return
	 */
	private Vector<AbstractNode> getDependentNodes(int aIndex, int bIndex) {

		Vector<AbstractNode> depNodes = new Vector<AbstractNode>();
		Set<AbstractNode> recvNodes = new HashSet<AbstractNode>();
		Set<Long> depTidSet = new HashSet<Long>();
		// Set<Integer> depMemSet = new HashSet<Integer>();

		AbstractNode startNode = maintrace.get(aIndex);
		depTidSet.add(startNode.getTId());
		// if(startNode.getType() ==TYPE.WRITE)
		// depMemSet.add(startNode.getMem());

		for (int pos = aIndex + 1; pos < bIndex; pos++) {
			AbstractNode node = maintrace.get(pos);
			// boolean isDepNode =false;
			long tid = node.getTId();
			node.getMem();
			if (depTidSet.contains(tid))// ||
			{
				depNodes.add(node);
				// if(node.getType() == TYPE.WRITE)
				// depMemSet.add(mem);
				// else

				// TODO: DON'T USE getOutNodes but DepNode
				/*
				 * if(node.getType() == TYPE.SEND)
				 * recvNodes.addAll(((MessageNode)node).getOutNodes());
				 */
			}
			// else if(depMemSet.contains(mem))
			// {
			// depNodes.add(node);
			// depTidSet.add(tid);
			// }
			else if (recvNodes.contains(node)) {
				depNodes.add(node);
				depTidSet.add(tid);
			}

		}

		return depNodes;
	}

	/**
	 * 
	 * @param node
	 * @return the node which happens-immediately-before the input node
	 */
	private AbstractNode getDetDNode(RWNode node) {
		int tindex = node.getTIndex();
		// int index = tIndexToNodeMap.get(tindex);

		return maintrace.get(tindex);
	}

	private AbstractNode getDetNode(RWNode node) {
		int tindex = node.getTIndex();
		Integer id = determinNodeMap.get(tindex);

		if (id == null)
			return null;

		return maintrace.get(id);
	}

	private String getScheduleLineDir() {
		if (lineDir == null) {

			lineDir = CommonUtil.getTempDir() + "schedule_line";

			File tempFile = new File(lineDir);

			if (!tempFile.exists()) {
				tempFile.mkdir();
			} else {
				CommonUtil.deleteFile(tempFile);
			}
		}
		return lineDir;
	}

	private String getScheduleTidDir() {
		if (tidDir == null) {
			tidDir = CommonUtil.getTempDir() + "schedule_tid";

			File tempFile = new File(tidDir);

			if (!tempFile.exists()) {
				tempFile.mkdir();
			} else {
				CommonUtil.deleteFile(tempFile);
			}
		}
		return tidDir;
	}

	private void intializeLockHistory() {

		for (int k = 0; k < threadIDList.size(); k++) {
			Long threadId = threadIDList.get(k);

			Vector<LockNode> locknodes = threadLockNodes.get(threadId);
			if (locknodes == null)
				locknodes = new Vector<LockNode>();

			threadLockHistory.put(threadId, new LockHistory(locknodes));
		}
	}

	private boolean isRedundantPattern(Pattern pattern) {
		String mem = pattern.getAnormalMem();
		HashSet patternset = patterns.get(mem);
		if (patternset == null) {
			patterns.put(mem, new HashSet<Pattern>());
			return false;
		}
		if (patternset.contains(pattern))
			return true;

		return false;
	}

	/**
	 * 
	 * processMonitorData() 1. compute pre-statics 2. get instance-accessvec 3.
	 * compute acclist 4. assign line number
	 */
	public void preProcess() {
		createFileWriter();
		processMonitorData();
		processAccessVector();
	}

	private void processAccessVector() {
		removeImmutableAccess();
		removeSingleThreadAccess();

		LinkedList<HashMap<Long, LinkedList<RWNode>>> accessList = computeAccessList();

		if (removeRedundance)
			removeRepeatedEvents(accessList);

		accessThreadNodes = computeAccessThreadNodes(accessList);

		threadAtomAccessList = computeThreadAtomAccessList();

	}

	private void processMonitorData() {
		processMonitorDataPassA();

		processMonitorDataPassB();

		if (checkAtomSet)
			intializeLockHistory();
	}

	private void processMonitorDataPassA() {
		int nonMethodEntryExitNodes_index = 0;

		int k = 0;

		HashMap<Long, Vector<AbstractNode>> threadAtomNodes = new HashMap<Long, Vector<AbstractNode>>();

		while (maintrace.size() > k) {
			AbstractNode node = maintrace.get(k);
			long threadId = node.getTId();

			if (node instanceof MethodNode) {

				maintrace.remove(k);
				k--;
			} else {
				node.setID(++nonMethodEntryExitNodes_index);
				Integer atomIndex = threadAtomIndex.get(threadId);
				if (atomIndex == null) {
					atomIndex = 0;
					threadAtomIndex.put(threadId, 0);
					threadIDList.add(threadId);
				}

				if (atomRegionAll) {
					if (node instanceof MessageNode) {
						threadAtomIndex.put(threadId, ++atomIndex);
					} else if (node instanceof RWNode) {
						RWNode rwnode = (RWNode) node;
						rwnode.setAtomIndex(atomIndex);
					}
				} else {
					Vector<AbstractNode> vecatom = threadAtomNodes
							.get(threadId);
					if (vecatom == null) {
						vecatom = new Vector<AbstractNode>();
						threadAtomNodes.put(threadId, vecatom);
					}

					if (node.getType() == TYPE.LOCK) {
						vecatom.add(node);
					} else if (node.getType() == TYPE.UNLOCK) {
						if (vecatom.size() > 0)
							vecatom.remove(vecatom.size() - 1);

						if (vecatom.isEmpty()) {
							threadAtomIndex.put(threadId, ++atomIndex);
						}
					} else if (node instanceof MessageNode) {
						threadAtomIndex.put(threadId, ++atomIndex);
					}

					if (node instanceof RWNode) {
						RWNode rwnode = (RWNode) node;

						// manually maintain the context for RWnode!
						//						List<ContextMethod> curCtxt = threadCurrentContext_lp
						//								.get(threadId);
						//						rwnode.setMCPairList_deepClone(curCtxt);

						if (!vecatom.isEmpty())
							rwnode.setAtomIndex(atomIndex);
					}
				}
			}
			k++;
		}
		numberOfThreads = threadIDList.size();
		numberOfTotalNonMethodEntryExitNodes = nonMethodEntryExitNodes_index;
	}

	private void processMonitorDataPassB() {
		por = new PartialOrderRelation();

		HashMap<Long, Integer> threadIdIndex = new HashMap<Long, Integer>();

		Integer t_index = 0;

		// k is the index of the node in the main trace
		for (int k = 0; k < numberOfTotalNonMethodEntryExitNodes; k++) {
			AbstractNode node = maintrace.get(k);
			long threadId = node.getTId();
			Set<Integer> lockset = threadLockSet.get(threadId);
			if (lockset == null) {
				lockset = new HashSet<Integer>();
				threadLockSet.put(threadId, lockset);
			}

			if (node instanceof LockNode) {
				numberOfLockNodes++;

				LockNode locknode = (LockNode) node;

				Vector<LockNode> vec = threadLockNodes.get(threadId);
				if (vec == null) {
					vec = new Vector<LockNode>();
					threadLockNodes.put(threadId, vec);
				}

				if (node.getType() == TYPE.LOCK) {
					lockset.add(node.getMem());
					vec.add(locknode);
				} else {
					lockset.remove(node.getMem());
				}
			} else if (node instanceof MessageNode) {
				numberOfMessageNodes++;

				AbstractNode depnode = node.getDepNode();
				if (depnode != null) {
					por.addMultiThreadOrder(depnode.getID(), k + 1);
					t_index++;
				}

				Integer lastIndex = threadIdIndex.get(threadId);
				if (lastIndex != null) {

					por.addSingleThreadOrder(lastIndex + 1, k + 1);
					determinNodeMap.put(lastIndex, k);
				} else {
					if (threadId == 1) {
						determinNodeMap.put(0, k);
					}
				}
				threadIdIndex.put(threadId, k);
			} else if (node instanceof RWNode) {
				RWNode rwnode = (RWNode) node;
				numberOfReadWriteNodes++;
				Vector<RWNode> vec = threadRWNodes.get(threadId);
				if (vec == null) {
					vec = new Vector<RWNode>();
					threadRWNodes.put(threadId, vec);
				}

				vec.add(rwnode);
				rwnode.setLockSet(new HashSet<Integer>(lockset));

				Integer ii = threadIdIndex.get(threadId);

				if (ii != null)
					rwnode.setTIndex(ii);
			}
		}
		por.computePORelation_new();// por.computePartialOrder();
	}

	private void removeImmutableAccess() {
		HashSet set = new HashSet();
		for (int i = 0; i < accessvec.length; i++) {
			for (int j = 0; j < accessvec[i].size(); j++) {
				RWNode node = accessvec[i].get(j);
				if (node.getType() == TYPE.WRITE) {
					set.add(i);
					break;
				}
			}
		}
		for (int i = 0; i < accessvec.length; i++) {
			if (!set.contains(i))
				accessvec[i].clear();
		}
	}

	/**
	 * TODO: trace redundancy elimination
	 * 
	 * 1. remove repeated events from the same threads with the same context
	 * 
	 * 2. remove repeated events across different threads
	 */
	private void removeRepeatedEvents(
			LinkedList<HashMap<Long, LinkedList<RWNode>>> accessList) {
		for (int k = 0; k < accessList.size(); k++) {
			HashMap<Integer, HashMap<Integer, Integer>> hash_map = new HashMap<Integer, HashMap<Integer, Integer>>();

			HashMap<Long, LinkedList<RWNode>> map = accessList.get(k);

			Iterator<Long> tidIt = map.keySet().iterator();
			while (tidIt.hasNext()) {
				Long threadId = tidIt.next();
				LinkedList<RWNode> nodes = map.get(threadId);

				ContextTree tree = new ContextTree(nodes, ctx_size);
				LinkedList<RWNode> simplified_nodes = tree.getSimplifiedNodes();

				// Remove redundant event sequences

				boolean isRedundantSequence = false;

				int key1 = simplified_nodes.size();
				int key2 = 0;
				for (int m = 0; m < key1; m++)
					key2 += simplified_nodes.get(m).getLine();

				HashMap<Integer, Integer> res1 = hash_map.get(key1);

				if (res1 != null) {
					Integer res2 = res1.get(key2);
					if (res2 == null) {
						res1.put(key2, 0);
					} else if (res2 == 0) {
						res2++;
						res1.put(key2, res2);
					} else {
						isRedundantSequence = true;
					}
				} else {
					res1 = new HashMap<Integer, Integer>();
					res1.put(key2, 0);
					hash_map.put(key1, res1);
				}

				if (!isRedundantSequence)
					map.put(threadId, simplified_nodes);
			}
		}
	}

	private void removeSingleThreadAccess() {
		HashSet set = new HashSet();
		for (int i = 0; i < accessvec.length; i++) {
			HashSet tset = new HashSet();
			for (int j = 0; j < accessvec[i].size(); j++) {
				RWNode node = accessvec[i].get(j);
				tset.add(node.getTId());
				if (tset.size() > 1) {
					numberOfSharedVariables++;
					set.add(i);
					break;
				}
			}
		}
		for (int i = 0; i < accessvec.length; i++) {
			if (!set.contains(i))
				accessvec[i].clear();
		}
	}

	public void reportStatistics() {
		CommonUtil.print("\n-------------------------------------\n");
		CommonUtil.print("Number of Threads: " + numberOfThreads);
		CommonUtil.print("Number of Shared Variables: "
				+ numberOfSharedVariables);
		CommonUtil.print("Number of Lock Nodes: " + numberOfLockNodes);
		CommonUtil.print("Number of Message Nodes: " + numberOfMessageNodes);
		CommonUtil.print("Number of Non-Method Entry/Exit Nodes: "
				+ numberOfTotalNonMethodEntryExitNodes);
		CommonUtil.print("Number of Read/Write Nodes: "
				+ numberOfReadWriteNodes);
		CommonUtil.print("Number of Total Nodes: " + numberOfTotalNodes + "\n");
		CommonUtil.print("Number of BUGS (no contexts): " + pcrStrings.size()
				+ "\n");
		CommonUtil.print("Number of BUGS: " + numOfBugs + "\n");

	}

	public void saveAVsOnly() {
		Iterator<String> keyIt = patterns.keySet().iterator();
		List<Pattern> list = new ArrayList<Pattern>();
		PreciseSTEContextEncoderDecoder.ctxts = this.monitorData.ctxts;
		while (keyIt.hasNext()) {
			String mem = keyIt.next();

			Iterator<Pattern> patternsIt = patterns.get(mem).iterator();
			while (patternsIt.hasNext()) {
				Pattern p = patternsIt.next();
				// System.out.println(p.toString());
				// printDetails(p);
				if (p instanceof TypeOnePattern || p instanceof TypeTwoPattern) {
					PatternI patternI = (PatternI) p;
					list.add(patternI);
				}
			}
		}

		if (list == null || list.size() == 0)
			return;

		PreciseSTEContextEncoderDecoder.ctxts = this.monitorData.ctxts;

		if (Options.useasmStack) {

			Iterator<Pattern> patternsIt = list.iterator();
			int pid = 0;
			while (patternsIt.hasNext()) {
				Pattern p = patternsIt.next();
				pid++;
				if (p instanceof TypeOnePattern || p instanceof TypeTwoPattern) {
					PatternI patternI = (PatternI) p;
					System.out.println(patternI.printToString());

				}

			}

		}

	}

	public void saveAVsOnly(SerializableComposition composition) {
		Iterator<String> keyIt = patterns.keySet().iterator();
		List<Pattern> list = new ArrayList<Pattern>();
		PreciseSTEContextEncoderDecoder.ctxts = this.monitorData.ctxts;
		while (keyIt.hasNext()) {
			String mem = keyIt.next();

			Iterator<Pattern> patternsIt = patterns.get(mem).iterator();
			while (patternsIt.hasNext()) {
				Pattern p = patternsIt.next();
				// System.out.println(p.toString());
				// printDetails(p);
				if (p instanceof TypeOnePattern || p instanceof TypeTwoPattern) {
					PatternI patternI = (PatternI) p;
					list.add(patternI);
				}
			}
		}

		if (list == null || list.size() == 0)
			return;

		PreciseSTEContextEncoderDecoder.ctxts = this.monitorData.ctxts;

		if (Options.useasmStack) {

			Iterator<Pattern> patternsIt = list.iterator();
			int pid = 0;
			while (patternsIt.hasNext()) {
				Pattern p = patternsIt.next();
				pid++;
				if (p instanceof TypeOnePattern || p instanceof TypeTwoPattern) {
					PatternI patternI = (PatternI) p;
					//					System.err.println("composition under checking: ");
					//					composition.print();
					if (compatible(patternI, composition)) {
						addRInvoke2Composition(patternI, composition); // enhance the composition
						Serializer.serialize(composition, CentralConstants
								.getFile4CompositionDetected(composition));
						System.err
								.println("step 4: an incorrect composition found! and serialized to: "
										+ CentralConstants
												.getFile4CompositionDetected(composition));
						System.out
								.println("[the incorrect composition detected:] ");
						composition.print();
						System.out.println("interleaving details:");

						System.out.println(patternI.printToString());

					}

				}

			}

		}

	}

	private void addRInvoke2Composition(PatternI patternI,
			SerializableComposition composition) {
		//library side.
		String libClass = composition.getLibClass();

		RWNode rnode = patternI.getNodeK();
		Vector rVector = PreciseSTEContextEncoderDecoder.getSTEcontext(rnode);
		for (int i = rVector.size() - 1; i >= 0; i--) { // backwards, important.
			String ste = (String) rVector.get(i);
			if (ste.startsWith(libClass)) {//ste.contains(libClass)
				String stelast = (String) rVector.get(i + 1);
				String rInvokeAPI = PreciseSTEContextHelper
						.getSootMethodSig(ste);
				String rContainer = PreciseSTEContextHelper
						.getSootMethodSig(stelast);

				int lineNo = PreciseSTEContextHelper.getInvokeLine(stelast);

				//				System.out.println("invokeAPI: " + rInvokeAPI);
				//				System.out.println("contianer: " + rContainer);
				//				System.out.println("invoke line " + lineNo);

				composition.setrInvokeAPI(rInvokeAPI);
				composition.setrInvokeContainerMethodString(rContainer);
				composition.setrInokeSite(lineNo);

				break;
			}

		}

		//TODO check the details and impl.

	}

	private boolean compatible(PatternI patternI,
			SerializableComposition composition) {

		// client side: p and c
		String firstInvokedAPI = composition.getFirstInvokedAPI();
		String lastInvokedAPI = composition.getLastInvokedAPI();
		String clientMethod = composition.getClientMethod();

		//library side.
		String libClass = composition.getLibClass();

		RWNode pnode = patternI.getNodeI();
		RWNode rnode = patternI.getNodeK();
		RWNode cnode = patternI.getNodeJ();
		Vector pVector = PreciseSTEContextEncoderDecoder.getSTEcontext(pnode);
		Vector cVector = PreciseSTEContextEncoderDecoder.getSTEcontext(cnode);
		Vector rVector = PreciseSTEContextEncoderDecoder.getSTEcontext(rnode);
		boolean jcodeAboutAC = jcodeAboutAC(pnode) && jcodeAboutAC(cnode)
				&& jcodeAboutAC(rnode);
		boolean containsFirstInvokedAPI = containsMethodInContext(
				firstInvokedAPI, pVector)
				|| containsMethodInContext(firstInvokedAPI, cVector);
		boolean containsLastInvokedAPI = containsMethodInContext(
				lastInvokedAPI, pVector)
				|| containsMethodInContext(lastInvokedAPI, cVector);
		boolean containsClientMethod = containsMethodInContext(clientMethod,
				pVector) && containsMethodInContext(clientMethod, cVector); // and relation.
		boolean libTypeCompatible = libTypeCompatible(libClass, rVector);

		return jcodeAboutAC && containsFirstInvokedAPI
				&& containsLastInvokedAPI && containsClientMethod
				&& libTypeCompatible;

	}

	private boolean jcodeAboutAC(RWNode pnode) {
		return pnode.getJcode().contains(CentralConstants.ACprefix);
	}

	private boolean libTypeCompatible(String libClass, Vector rVector) {
		for (Object obj : rVector) {
			String ste = (String) obj;
			if (ste.startsWith(libClass)) {//ste.contains(libClass)
				return true;
			}

		}
		return false;

	}

	private boolean containsMethodInContext(String firstInvokedAPI,
			Vector pVector) {
		String classname = PreciseSTEContextHelper
				.getClassName_sootsig(firstInvokedAPI);
		String methodname = PreciseSTEContextHelper
				.getMethName_sootsig(firstInvokedAPI);
		String argString = PreciseSTEContextHelper
				.getArgList_sootsig(firstInvokedAPI);

		for (Object obj : pVector) {
			String ste = (String) obj;
			if (ste.contains(classname) && ste.contains(methodname)
					&& ste.contains(argString)) {
				return true;
			}

		}
		return false;
	}

	private void saveAtomPattern(Pattern pattern, AbstractNode nodex) {
		savePattern(pattern);
		atompatternToNodeMap.put(pattern, nodex);
	}

	/**
	 * save the original trace first
	 */
	private void saveOriginalTrace() {
		Vector<AbstractNode> originaltrace = getAFreshTrace(maintrace.size() - 1);
		removeNonReadWriteNodes(originaltrace);
		saveTransformedSchedule(originaltrace, "0");// 0 represents the original
													// trace
	}

	/**
	 * 
	 * @param pattern
	 *            : real detected pattern
	 * @param nodex
	 *            : i'
	 */
	private void savePattern(Pattern pattern) {
		patterns.get(pattern.getAnormalMem()).add(pattern);
	}

	private void saveRacePattern(RWNode node, RWNode node2) {
		PatternRace pattern = new PatternRace(node, node2);
		pattern.getAnormalMem();
		if (!isRedundantPattern(pattern)) {
			savePattern(pattern);
		}
	}

	private void saveTransformedSchedule(Vector<AbstractNode> trace,
			String patternID) {
		ObjectOutputStream out = null;
		Vector<Long> schedule_tid_ = new Vector<Long>();
		Vector<Integer> schedule_line_ = new Vector<Integer>();
		for (int k = 0; k < trace.size(); k++) {
			AbstractNode node = trace.get(k);
			schedule_tid_.add(node.getTId());
			schedule_line_.add(((RWNode) node).getLine());
		}

		try {
			out = CommonUtil.getOutputStream(getScheduleTidDir()
					+ System.getProperty("file.separator") + classname + "."
					+ patternID);
			out.writeObject(schedule_tid_);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			out = CommonUtil.getOutputStream(getScheduleLineDir()
					+ System.getProperty("file.separator") + classname + "."
					+ patternID);
			out.writeObject(schedule_line_);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Four reads/writes on two different shared variables by two threads
	 */
	private void searchAtomSetPatterns(LinkedList<RWNode> linkedListI,
			LinkedList<RWNode> linkedListJ, LinkedList<RWNode> linkedListK,
			LinkedList<RWNode> linkedListR) {
		for (int i = 0; i < linkedListI.size(); i++) {
			for (int j = 0; j < linkedListJ.size(); j++) {
				for (int r = 0; r < linkedListR.size(); r++) {
					for (int k = 0; k < linkedListK.size(); k++) {

						RWNode nodeI = linkedListI.get(i);
						RWNode nodeJ = linkedListJ.get(j);
						RWNode nodeK = linkedListK.get(k);
						RWNode nodeR = linkedListR.get(r);

						if (nodeI.getType() == TYPE.WRITE
								&& nodeJ.getType() == TYPE.WRITE
								&& nodeK.getType() == TYPE.WRITE
								&& nodeR.getType() == TYPE.WRITE
								|| nodeI.getType() == TYPE.WRITE
								&& nodeJ.getType() == TYPE.WRITE
								&& nodeK.getType() == TYPE.READ
								&& nodeR.getType() == TYPE.READ
								|| nodeI.getType() == TYPE.READ
								&& nodeJ.getType() == TYPE.READ
								&& nodeK.getType() == TYPE.WRITE
								&& nodeR.getType() == TYPE.WRITE) {

							int I = nodeI.getID();
							int J = nodeJ.getID();
							int K = nodeK.getID();
							int R = nodeR.getID();

							if (I > J) {
								int tempIndex = I;
								I = J;
								J = tempIndex;

								RWNode tempNode = nodeI;
								nodeI = nodeJ;
								nodeJ = tempNode;
							}

							if (K > R) {
								int tempIndex = K;
								K = R;
								R = tempIndex;

								RWNode tempNode = nodeK;
								nodeK = nodeR;
								nodeR = tempNode;
							}

							if (K < I) {
								int tempIndexI = I;
								int tempIndexJ = J;
								I = K;
								J = R;
								K = tempIndexI;
								R = tempIndexJ;

								RWNode tempNodeI = nodeI;
								RWNode tempNodeJ = nodeJ;
								nodeI = nodeK;
								nodeJ = nodeR;
								nodeK = tempNodeI;
								nodeR = tempNodeJ;
							}
							if (canNotReach(nodeJ, nodeK)) {

								Pattern p = new PatternII(nodeI, nodeK, nodeR,
										nodeJ);

								if (!isRedundantPattern(p)) {

									if (I < K && R < J) { // lp: data race
															// showRealPattern(p);
									} else {
										if (nodeI.getLockSet() != null
												&& nodeJ.getLockSet() != null
												&& nodeK.getLockSet() != null
												&& nodeR.getLockSet() != null) {
											// CHECK LOCKING CONSTRAINTS

											Set<Integer> lockhistory = threadLockHistory
													.get(nodeK.getTId())
													.getLockHistory(nodeK,
															nodeR);

											Set<Integer> locks = new HashSet<Integer>(
													lockhistory);
											locks.retainAll(nodeI.getLockSet());
											if (locks.size() == 0) {
												// PATTERN SATISFIED
												saveAtomPattern(p, nodeI);
											} else {
												Vector<RWNode> ijnodes = threadRWNodes
														.get(nodeI.getTId());

												locks = new HashSet<Integer>(
														lockhistory);
												locks.retainAll(nodeJ
														.getLockSet());
												if (locks.size() == 0) {
													// PATTERN SATISFIED
													saveAtomPattern(
															p,
															ijnodes.get(ijnodes
																	.indexOf(nodeJ) - 1));
												} else {
													// CHECK THREE CASES

													int iindex = ijnodes
															.indexOf(nodeI) + 2;
													int jindex = ijnodes
															.indexOf(nodeJ) - 2;
													if (iindex <= jindex) {

														int index = (iindex + jindex) / 2;
														// for(int index =
														// iindex;index<=jindex;index++)
														{
															RWNode nodex = ijnodes
																	.get(index);

															locks = new HashSet<Integer>(
																	lockhistory);
															locks.retainAll(nodex
																	.getLockSet());
															if (locks.size() == 0) {
																if (canNotReach(
																		nodex,
																		nodeR)) {
																	saveAtomPattern(
																			p,
																			nodex);
																}
															} else {
																index = iindex;
																nodex = ijnodes
																		.get(index);

																locks = new HashSet<Integer>(
																		lockhistory);
																locks.retainAll(nodex
																		.getLockSet());
																if (locks
																		.size() == 0) {
																	if (canNotReach(
																			nodex,
																			nodeR)) {
																		saveAtomPattern(
																				p,
																				nodex);
																	}
																} else {
																	index = jindex;
																	nodex = ijnodes
																			.get(index);

																	locks = new HashSet<Integer>(
																			lockhistory);
																	locks.retainAll(nodex
																			.getLockSet());
																	if (locks
																			.size() == 0) {
																		if (canNotReach(
																				nodex,
																				nodeR)) {
																			saveAtomPattern(
																					p,
																					nodex);
																		}
																	}
																}
															}

														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	public void setAtomRegionAll() {
		atomRegionAll = true;
	}

	public void setCheckAtomicity() {
		checkAtomicity = true;
	}

	public void setCheckAtomSet() {
		checkAtomSet = true;
	}

	public void setCheckRace() {
		checkRace = true;
	}

	public void setMaxPatternPerVar(int k) {
		MaxPatternPerVar = k;
	}

	public void setMaxRedundantLockComputation(int k) {
		MaxRedundantLockComputation = k;
	}

	public void setMaxRedundantPatternComputation(int k) {
		MaxRedundantPatternComputation = k;
	}

	public void setRemoveRedundance() {
		removeRedundance = true;
	}

	public void setRemoveRedundance(int size) {
		removeRedundance = true;
		this.ctx_size = size;
	}

	public void showAllPatterns() {
		int sum = 0;
		Iterator<String> keyIt = patterns.keySet().iterator();
		while (keyIt.hasNext()) {
			String mem = keyIt.next();

			Iterator<Pattern> patternsIt = patterns.get(mem).iterator();
			while (patternsIt.hasNext()) {
				Pattern p = patternsIt.next();
				if (p instanceof TypeOnePattern || p instanceof TypeTwoPattern) {
					sum++;
				}
			}
		}

		System.out.println("total bugs: " + sum);

	}

	public void transform() {
		saveOriginalTrace();

		Iterator<String> keyIt = patterns.keySet().iterator();
		while (keyIt.hasNext()) {
			String mem = keyIt.next();

			Iterator<Pattern> patternsIt = patterns.get(mem).iterator();
			int numberpervar = 0;
			while (patternsIt.hasNext() && numberpervar++ < MaxPatternPerVar) {
				Pattern p = patternsIt.next();

				transPatternNumber++;

				if (p instanceof PatternRace) {
					transformRace((PatternRace) p);
				} else {
					CommonUtil.print("\n*** Apply Transformation >>> "
							+ transPatternNumber + ": " + p.printToString());

					AbstractNode nodeX = atompatternToNodeMap.get(p);
					if (p instanceof PatternI) {
						transformAtomicity((PatternI) p, nodeX);

					} else if (p instanceof PatternII) {

						transformAtomset((PatternII) p, nodeX);
					}
				}
			}
		}
	}

	private void transformAtomicity(PatternI p, AbstractNode nodeX) {
		AbstractNode nodeI = p.getNodeI();
		AbstractNode nodeJ = p.getNodeJ();
		AbstractNode nodeK = p.getNodeK();

		int IIndex = nodeI.getID() - 1;
		int JIndex = nodeJ.getID() - 1;
		int KIndex = nodeK.getID() - 1;

		int XIndex = nodeX.getID() - 1;

		if (IIndex > 0 && JIndex > 0 && KIndex > 0 && XIndex > 0) {

			if (KIndex > JIndex) {
				Vector<AbstractNode> trace = getAFreshTrace(KIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(XIndex,
						JIndex + 1);
				for (int k = 0; k < depNodes_move.size(); k++) {
					AbstractNode node = depNodes_move.get(k);
					trace.remove(node);
					trace.add(node);
				}

				Vector<AbstractNode> depNodes = getDependentNodes(JIndex,
						KIndex);
				trace.removeAll(depNodes);

				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,
						Integer.toString(transPatternNumber));
			} else if (KIndex < IIndex) {

				Vector<AbstractNode> trace = getAFreshTrace(JIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(KIndex,
						XIndex);

				depNodes_move.add(0, nodeK);

				int pos;
				AbstractNode lastnode = nodeX;
				for (int k = 0; k < depNodes_move.size(); k++) {
					AbstractNode node = depNodes_move.get(k);
					trace.remove(node);
					pos = trace.indexOf(lastnode);
					trace.insertElementAt(node, pos + 1);
					lastnode = node;
				}

				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,
						Integer.toString(transPatternNumber));

			}
		}
	}

	private void transformAtomset(PatternII p, AbstractNode nodeX) {

		AbstractNode nodeI = p.getNodeI();
		AbstractNode nodeJ = p.getNodeJ();
		AbstractNode nodeK = p.getNodeK();
		AbstractNode nodeR = p.getNodeR();

		int IIndex = nodeI.getID() - 1;
		int JIndex = nodeJ.getID() - 1;
		int KIndex = nodeK.getID() - 1;
		int RIndex = nodeR.getID() - 1;

		int XIndex = nodeX.getID() - 1;

		if (IIndex > 0 && JIndex > 0 && KIndex > 0 && RIndex > 0 && XIndex > 0) {
			// MAKE SURE K<R
			if (KIndex > RIndex) {
				int tempIndex = KIndex;
				KIndex = RIndex;
				RIndex = tempIndex;

				AbstractNode tempNode = nodeK;
				nodeK = nodeR;
				nodeR = tempNode;
			}

			if (KIndex > IIndex) {

				Vector<AbstractNode> trace = getAFreshTrace(RIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(XIndex,
						JIndex + 1);
				for (int k = 0; k < depNodes_move.size(); k++) {
					AbstractNode node = depNodes_move.get(k);

					trace.remove(node);
					trace.add(node);
				}

				Vector<AbstractNode> depNodes = getDependentNodes(JIndex,
						RIndex);
				trace.removeAll(depNodes);

				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,
						Integer.toString(transPatternNumber));
			} else if (RIndex < JIndex) {
				Vector<AbstractNode> trace = getAFreshTrace(JIndex);

				Vector<AbstractNode> depNodes_move = getDependentNodes(KIndex,
						XIndex);

				depNodes_move.add(0, nodeK);

				int pos;
				AbstractNode lastnode = nodeX;
				for (int k = 0; k < depNodes_move.size(); k++) {
					AbstractNode node = depNodes_move.get(k);
					trace.remove(node);
					pos = trace.indexOf(lastnode);
					trace.insertElementAt(node, pos + 1);
					lastnode = node;
				}

				Vector<AbstractNode> depNodes = getDependentNodes(RIndex,
						JIndex);
				trace.removeAll(depNodes);

				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,
						Integer.toString(transPatternNumber));
			} else if (KIndex < IIndex && RIndex > JIndex) {
				Vector<AbstractNode> trace = getAFreshTrace(RIndex);

				Vector<AbstractNode> depNodes_move1 = getDependentNodes(XIndex,
						JIndex + 1);
				for (int k = 0; k < depNodes_move1.size(); k++) {
					AbstractNode node = depNodes_move1.get(k);
					trace.remove(node);
					trace.add(node);
				}

				Vector<AbstractNode> depNodes_move2 = getDependentNodes(KIndex,
						XIndex);

				depNodes_move2.add(0, nodeK);

				int pos;
				AbstractNode lastnode = nodeX;
				for (int k = 0; k < depNodes_move2.size(); k++) {
					AbstractNode node = depNodes_move2.get(k);
					trace.remove(node);
					pos = trace.indexOf(lastnode);
					trace.insertElementAt(node, pos + 1);
					lastnode = node;
				}

				Vector<AbstractNode> depNodes = getDependentNodes(JIndex,
						RIndex);
				trace.removeAll(depNodes);

				removeNonReadWriteNodes(trace);
				saveTransformedSchedule(trace,
						Integer.toString(transPatternNumber));
			}

		}

	}

	private void transformRace(PatternRace p) {
		RWNode nodeA = p.getNodeI();
		RWNode nodeB = p.getNodeII();

		int aIndex = nodeA.getID() - 1;
		int bIndex = nodeB.getID() - 1;

		if (aIndex > bIndex) {
			int tempIndex = aIndex;
			aIndex = bIndex;
			bIndex = tempIndex;

			RWNode tempNode = nodeA;
			nodeA = nodeB;
			nodeB = tempNode;
		}

		if (aIndex > 0 && bIndex > 0) {

			Vector<AbstractNode> depNodes = getDependentNodes(aIndex, bIndex);

			Vector<AbstractNode> trace = getAFreshTrace(bIndex);

			trace.removeAll(depNodes);

			removeNonReadWriteNodes(trace);

			// if(nodeA.getType()==TYPE.WRITE)
			// trace.insertElementAt(nodeA, trace.indexOf(nodeB));
			// else
			// trace.add(nodeA);

			trace.remove(nodeA);
			trace.add(nodeA);

			p.setNodeI(nodeB);
			p.setNodeII(nodeA);

			CommonUtil.print("\n*** Apply Transformation >>> "
					+ transPatternNumber + "_1: " + p.printToString());

			// saveTransformedSchedule(trace,transPatternNumber+"_1");
			saveTransformedSchedule(trace, transPatternNumber + "");

			trace.remove(nodeA);
			trace.insertElementAt(nodeA, trace.indexOf(nodeB));

			p.setNodeI(nodeA);
			p.setNodeII(nodeB);

			CommonUtil.print("\n*** Apply Transformation >>> "
					+ transPatternNumber + "_2: " + p.printToString());

			// saveTransformedSchedule(trace,transPatternNumber+"_2");
			saveTransformedSchedule(trace, ++transPatternNumber + "");

		}

	}
}
