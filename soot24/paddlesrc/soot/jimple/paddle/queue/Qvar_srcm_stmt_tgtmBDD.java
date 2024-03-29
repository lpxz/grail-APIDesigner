package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qvar_srcm_stmt_tgtmBDD extends Qvar_srcm_stmt_tgtm {
    public Qvar_srcm_stmt_tgtmBDD(String name) {
        super(name);
    }
    
    private LinkedList readers =
      new LinkedList();
    
    public void add(VarNode _var,
                    SootMethod _srcm,
                    Unit _stmt,
                    SootMethod _tgtm) {
        add(new jedd.internal.RelationContainer(new Attribute[] { A_var.v(), A_srcm.v(), A_stmt.v(), A_tgtm.v() },
                                                new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), MT.v() },
                                                ("add(jedd.internal.Jedd.v().literal(new java.lang.Object[...]" +
                                                 ", new jedd.Attribute[...], new jedd.PhysicalDomain[...])) at" +
                                                 " Qvar_srcm_stmt_tgtmBDD.jedd:34,8-11"),
                                                jedd.internal.Jedd.v().literal(new Object[] { _var, _srcm, _stmt, _tgtm },
                                                                               new Attribute[] { A_var.v(), A_srcm.v(), A_stmt.v(), A_tgtm.v() },
                                                                               new PhysicalDomain[] { V1.v(), MS.v(), ST.v(), MT.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(in),
                                           jedd.internal.Jedd.v().falseBDD()))
            invalidate();
        for (Iterator it =
               readers.iterator();
             it.hasNext();
             ) {
            Rvar_srcm_stmt_tgtmBDD reader =
              (Rvar_srcm_stmt_tgtmBDD)
                it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { A_var.v(), A_srcm.v(), A_tgtm.v(), A_stmt.v() },
                                                           new PhysicalDomain[] { V1.v(), MS.v(), MT.v(), ST.v() },
                                                           "reader.add(in) at Qvar_srcm_stmt_tgtmBDD.jedd:40,12-18",
                                                           in));
        }
    }
    
    public Rvar_srcm_stmt_tgtm reader(String rname) {
        Rvar_srcm_stmt_tgtm ret =
          new Rvar_srcm_stmt_tgtmBDD(name +
                                     ":" +
                                     rname,
                                     this);
        readers.add(ret);
        return ret;
    }
}
