package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Rsrcm_stmt_kind_tgtm implements PaddleQueueReader {
    public Rsrcm_stmt_kind_tgtm(String name, PaddleQueue q) { super();
                                                              this.name = name;
                                                              this.q = q;
                                                              Readers.v().add(this); }
    
    protected String name;
    
    protected PaddleQueue q;
    
    public PaddleQueue queue() { return q; }
    
    public final String toString() { return name; }
    
    public abstract Iterator iterator();
    
    public abstract jedd.internal.RelationContainer get();
    
    public abstract boolean hasNext();
    
    public static class Tuple {
        private SootMethod _srcm;
        
        public SootMethod srcm() { return _srcm; }
        
        private Unit _stmt;
        
        public Unit stmt() { return _stmt; }
        
        private Kind _kind;
        
        public Kind kind() { return _kind; }
        
        private SootMethod _tgtm;
        
        public SootMethod tgtm() { return _tgtm; }
        
        public Tuple(SootMethod _srcm, Unit _stmt, Kind _kind, SootMethod _tgtm) { super();
                                                                                   this._srcm = _srcm;
                                                                                   this._stmt = _stmt;
                                                                                   this._kind = _kind;
                                                                                   this._tgtm = _tgtm; }
        
        public int hashCode() { return 0; }
        
        public boolean equals(Object other) { if (!(other instanceof Tuple)) return false;
                                              Tuple o = (Tuple) other;
                                              if (o._srcm != _srcm) return false;
                                              if (o._stmt != _stmt) return false;
                                              if (o._kind != _kind) return false;
                                              if (o._tgtm != _tgtm) return false;
                                              return true; }
        
        public String toString() { StringBuffer ret = new StringBuffer();
                                   ret.append(srcm());
                                   ret.append(", ");
                                   ret.append(stmt());
                                   ret.append(", ");
                                   ret.append(kind());
                                   ret.append(", ");
                                   ret.append(tgtm());
                                   ret.append(", ");
                                   return ret.toString(); }
    }
    
}
