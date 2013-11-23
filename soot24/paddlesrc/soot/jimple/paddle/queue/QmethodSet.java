package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class QmethodSet extends Qmethod {
    public QmethodSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(SootMethod _method) {
        invalidate();
        Rmethod.Tuple in = new Rmethod.Tuple(_method);
        for (Iterator it = readers.iterator(); it.hasNext(); ) { RmethodSet reader = (RmethodSet) it.next();
                                                                 reader.add(in); } }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rmethod reader(String rname) { Rmethod ret = new RmethodSet(name + ":" + rname, this);
                                          readers.add(ret);
                                          return ret; }
    
    public Rmethod revreader(String rname) { Rmethod ret = new RmethodRev(name + ":" + rname, this);
                                             readers.add(ret);
                                             return ret; }
}
