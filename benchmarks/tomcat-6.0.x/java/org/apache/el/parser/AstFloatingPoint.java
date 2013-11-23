/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Generated By:JJTree: Do not edit this line. AstFloatingPoint.java */

package org.apache.el.parser;

import java.math.BigDecimal;

import javax.el.ELException;

import org.apache.el.lang.EvaluationContext;


/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Id: AstFloatingPoint.java,v 1.1 2010/06/30 05:05:30 smhuang Exp $
 */
public final class AstFloatingPoint extends SimpleNode {
    public AstFloatingPoint(int id) {
        super(id);
    }

    private Number number;

    public Number getFloatingPoint() {
        if (this.number == null) {
            try {
                this.number = new Double(this.image);
            } catch (ArithmeticException e0) {
                this.number = new BigDecimal(this.image);
            }
        }
        return this.number;
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        return this.getFloatingPoint();
    }

    public Class getType(EvaluationContext ctx)
            throws ELException {
        return this.getFloatingPoint().getClass();
    }
}
