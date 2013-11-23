/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2004 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: IllegalStateException.java,v 1.1 2010/06/18 16:44:40 smhuang Exp $
 */
package org.exolab.jms.net.connector;


/**
 * An <code>IllegalStateException</code> is thrown if a method is invoked at an
 * illegal or inappropriate time
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:44:40 $
 */
public class IllegalStateException extends ResourceException {

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 1L;


    /**
     * Construct a new <code>IllegalStateException</code>
     */
    public IllegalStateException() {
    }

    /**
     * Construct a new <code>IllegalStateException</code> with no target
     * exception
     *
     * @param detail the detail message
     */
    public IllegalStateException(String detail) {
        super(detail);
    }

    /**
     * Construct a new <code>IllegalStateException</code> with a target
     * exception
     *
     * @param target the target exception
     */
    public IllegalStateException(Throwable target) {
        super(target);
    }

    /**
     * Construct a new <code>IllegalStateException</code> with a detail message
     * and target exception
     *
     * @param detail the detail message
     * @param target the target exception
     */
    public IllegalStateException(String detail, Throwable target) {
        super(detail, target);
    }

}
