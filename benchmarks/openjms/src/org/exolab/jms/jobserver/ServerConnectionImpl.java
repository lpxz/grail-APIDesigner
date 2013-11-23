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
 * Copyright 2000-2005 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: ServerConnectionImpl.java,v 1.1 2010/06/18 16:47:18 smhuang Exp $
 */
package org.exolab.jms.jobserver;

import java.util.HashSet;
import java.util.Iterator;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exolab.jms.messagemgr.ConsumerManager;
import org.exolab.jms.messagemgr.MessageManager;
import org.exolab.jms.messagemgr.ResourceManager;
import org.exolab.jms.persistence.DatabaseService;
import org.exolab.jms.scheduler.Scheduler;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

/**
 * Server implementation of the <code>javax.jms.Connection</code> interface.
 *
 * @author <a href="mailto:jima@comware.com.au">Jim Alateras</a>
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:47:18 $
 * @see ServerConnectionManagerImpl
 */
public class ServerConnectionImpl extends ServerConnectionPOA implements ServerConnection  {

	public org.exolab.jms.server.ServerConnectionImpl adaptee;
    /**
     * Construct a new <code>ServerConnectionImpl</code>.
     *
     * @param manager      the connection manager
     * @param connectionId the identifier for this connection
     * @param clientId     the client identifier. May be <code>null</code>
     * @param messages     the message manager
     * @param consumers    the consumer manager
     * @param resources    the resource manager
     */
    protected ServerConnectionImpl(org.exolab.jms.server.ServerConnectionManagerImpl manager,
                                  long connectionId, String clientId,
                                  MessageManager messages,
                                  ConsumerManager consumers,
                                  ResourceManager resources,
                                  DatabaseService database,
                                  Scheduler scheduler) {
    	adaptee = new org.exolab.jms.server.ServerConnectionImpl(manager, connectionId, clientId, messages,consumers, resources,database,scheduler);
    }
    
    public ServerConnectionImpl(org.exolab.jms.server.ServerConnectionImpl impl){
    	adaptee = impl;
    }

    /**
     * Returns the connection identifier.
     *
     * @return the connection identifier
     */
    public long getConnectionId() {
        return adaptee.getConnectionId();
    }

    /**
     * Returns the client identifier.
     *
     * @return the client identifier
     */
    public String getClientID() {
        return adaptee.getClientID();
    }

    /**
     * Sets the client identifier for this connection.
     *
     * @param clientID the unique client identifier
     * @throws JMSException             if the JMS provider fails to set the
     *                                  client ID for this connection due to
     *                                  some internal error.
     * @throws InvalidClientIDException if the JMS client specifies an invalid
     *                                  or duplicate client ID.
     * @throws IllegalStateException    if the JMS client attempts to set a
     *                                  connection's client ID at the wrong time
     *                                  or when it has been administratively
     *                                  configured.
     */
    public void setClientID(String clientID) {
    	try {
			adaptee.setClientID(clientID);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Create a new session
     *
     * @param acknowledgeMode indicates whether the consumer or the client will
     *                        acknowledge any messages it receives; ignored if
     *                        the session is transacted. Legal values are
     *                        <code>Session.AUTO_ACKNOWLEDGE</code>,
     *                        <code>Session.CLIENT_ACKNOWLEDGE</code>, and
     *                        <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
     * @param transacted      indicates whether the session is transacted
     * @return a newly created session
     * @throws JMSException for any JMS error
     */
    public synchronized ServerSession createSession(int acknowledgeMode, boolean transacted){
    		try {
				org.exolab.jms.server.ServerSessionImpl session = (org.exolab.jms.server.ServerSessionImpl)adaptee.createSession(acknowledgeMode, transacted);
      return new ServerSessionImpl(session);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				return null;
			}
    }

    /**
     * Closes the connection.
     */
    public synchronized void close() {
    	adaptee.close();
    }

    /**
     * Notify closure of a session
     *
     * @param session the closed session
     */
    public synchronized void closed(ServerSessionImpl session) {
    	adaptee.closed(session.adaptee);
    }

	public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) {
		// TODO Auto-generated method stub
		return null;
	}

	public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object _duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	public DomainManager[] _get_domain_managers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object _get_interface_def() {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy _get_policy(int policy_type) {
		// TODO Auto-generated method stub
		return null;
	}

	public int _hash(int maximum) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean _is_a(String repositoryIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean _is_equivalent(Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean _non_existent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void _release() {
		// TODO Auto-generated method stub
		
	}

	public Request _request(String operation) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object _set_policy_override(Policy[] policies, SetOverrideType set_add) {
		// TODO Auto-generated method stub
		return null;
	}

}
