package soot.jimple.paddle.bdddomains;

import jedd.*;
import soot.*;

public class A_dstc extends Attribute {
    public final ContextDomain domain = (ContextDomain) ContextDomain.v();
    
    public Domain domain() { return domain; }
    
    public static Attribute v() { return instance; }
    
    private static Attribute instance = new A_dstc();
    
    public A_dstc() { super(); }
}
