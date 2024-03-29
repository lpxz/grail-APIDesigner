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
 * Copyright 2001-2005 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: JmsXATopicConnection.java,v 1.1 2010/06/18 16:47:28 smhuang Exp $
 */
package org.exolab.jms.client;

import javax.jms.JMSException;
import javax.jms.TopicSession;
import javax.jms.XASession;
import javax.jms.XATopicConnection;
import javax.jms.XATopicSession;


/**
 * Client implementation of the <code>javax.jms.XATopicConnection</code>
 * interface.
 *
 * @author <a href="mailto:jima@comware.com.au">Jim Alateras</a>
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:47:28 $
 */
public class JmsXATopicConnection
        extends JmsTopicConnection
        implements XATopicConnection {

    /**
     * Construct a new <code>JmsXATopicConnection</code>.
     *
     * @param factory  factory creating this object
     * @param id       the connection identifier
     * @param username client username
     * @param password client password
     * @throws JMSException if there is any problem creating this object
     */
    public JmsXATopicConnection(JmsXAConnectionFactory factory, String id,
                                String username, String password)
            throws JMSException {
        super(factory, id, username, password);
    }

    /**
     * Creates an <code>XASession</code> object.
     *
     * @return a newly created <code>XASession</code>
     * @throws JMSException if the <code>XAConnection</code> object fails to
     *                      create an <code>XASession</code> due to some
     *                      internal error.
     */
    public XASession createXASession() throws JMSException {
        return new JmsXASession(this);
    }

    /**
     * Creates an <code>XATopicSession</code> object.
     *
     * @return a newly created <code>XATopicSession</code>
     * @throws JMSException if the <code>XATopicConnection</code> object fails
     *                      to create an XA queue session due to some internal
     *                      error.
     */
    public XATopicSession createXATopicSession() throws JMSException {
        ensureOpen();
        setModified();
        return new JmsXATopicSession(this);
    }

    /**
     * Creates an <code>XATopicSession</code> object.
     *
     * @param transacted      usage undefined
     * @param acknowledgeMode usage undefined
     * @return a newly created <code>XATopicSession</code>
     * @throws JMSException if the <code>XATopicConnection</code> object fails
     *                      to create an XA queue session due to some internal
     *                      error.
     */
    public TopicSession createTopicSession(boolean transacted,
                                           int acknowledgeMode)
            throws JMSException {

        ensureOpen();
        setModified();
        return new JmsXATopicSession(this);
    }
}