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
 * $Id: ServerConnectionManagerImpl.java,v 1.1 2010/06/18 16:47:23 smhuang Exp $
 */
package org.exolab.jms.jobserver;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;

import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.jms.common.security.BasicPrincipal;
import org.exolab.jms.messagemgr.ConsumerManager;
import org.exolab.jms.messagemgr.MessageManager;
import org.exolab.jms.messagemgr.ResourceManager;
import org.exolab.jms.net.connector.Authenticator;
import org.exolab.jms.net.connector.ResourceException;
import org.exolab.jms.persistence.DatabaseService;
import org.exolab.jms.scheduler.Scheduler;
import org.exolab.jms.server.ServerConnectionImpl;
import org.exolab.jms.server.ServerConnectionManager;
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
 * The <code>ServerConnectionManagerImpl</code> is responsible for managing all
 * connections to the server.
 *
 * @author <a href="mailto:jima@comware.com.au">Jim Alateras</a>
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:47:23 $
 * @see ServerConnectionImpl
 */
public class ServerConnectionManagerImpl extends ServerConnectionFactoryPOA implements ServerConnectionFactory {
	
	public org.exolab.jms.server.ServerConnectionManagerImpl adaptee;
    /**
     * Construct a new <code>ServerConnectionManagerImpl</code>.
     *
     * @param authenticator the authenticator to verify users with
     */
    public ServerConnectionManagerImpl(Authenticator authenticator,
                                      MessageManager messages,
                                      DatabaseService database,
                                      Scheduler scheduler) {
    	adaptee = new org.exolab.jms.server.ServerConnectionManagerImpl(authenticator, messages, database, scheduler);
    }        

  
    /**
     * Creates a connection with the specified user identity.
     * <p/>
     * The connection is created in stopped mode. No messages will be delivered
     * until the <code>Connection.start</code> method is explicitly called.
     * <p/>
     * If <code>clientID</code> is specified, it indicates the pre-configured
     * client identifier associated with the client <code>ConnectionFactory</code>
     * object.
     *
     * @param clientID the pre-configured client identifier. May be
     *                 <code>null</code> <code>null</code>.
     * @param userName the caller's user name
     * @param password the caller's password
     * @return a newly created connection
     * @throws InvalidClientIDException if the JMS client specifies an invalid
     *                                  or duplicate client ID.
     * @throws JMSException             if the JMS provider fails to create the
     *                                  connection due to some internal error.
     * @throws JMSSecurityException     if client authentication fails due to an
     *                                  invalid user name or password.
     */
    public ServerConnection createConnection(String clientID, String userName,
                                             String password) {
    	try {
			org.exolab.jms.server.ServerConnectionImpl session = (org.exolab.jms.server.ServerConnectionImpl)adaptee.createConnection(clientID, userName, password); 
			org.exolab.jms.jobserver.ServerConnectionImpl impl = new org.exolab.jms.jobserver.ServerConnectionImpl(session);
			return impl;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			return null;
		}
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


	public Policy _get_policy(int policy_type) {
		// TODO Auto-generated method stub
		return null;
	}


	public int _hash(int maximum) {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean _is_equivalent(Object other) {
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
