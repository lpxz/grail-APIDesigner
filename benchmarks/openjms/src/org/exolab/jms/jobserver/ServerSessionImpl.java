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
 * Copyright 2000-2004 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: ServerSessionImpl.java,v 1.1 2010/06/18 16:47:20 smhuang Exp $
 */
package org.exolab.jms.jobserver;

import java.util.Iterator;
import java.util.List;

import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.jms.client.JmsDestination;
import org.exolab.jms.client.JmsMessageListener;
import org.exolab.jms.client.JmsQueue;
import org.exolab.jms.client.JmsTopic;
import org.exolab.jms.message.MessageImpl;
import org.exolab.jms.messagemgr.ConsumerEndpoint;
import org.exolab.jms.messagemgr.ConsumerManager;
import org.exolab.jms.messagemgr.Flag;
import org.exolab.jms.messagemgr.MessageManager;
import org.exolab.jms.messagemgr.ResourceManager;
import org.exolab.jms.persistence.DatabaseService;
import org.exolab.jms.scheduler.Scheduler;
import org.exolab.jms.server.SessionConsumer;
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
 * A session represents a server side endpoint to the JMSServer. A client can
 * create producers, consumers and destinations through the session in addi-
 * tion to other functions. A session has a unique identifer which is a comb-
 * ination of clientId-connectionId-sessionId.
 * <p/>
 * A session represents a single-threaded context which implies that it cannot
 * be used with more than one thread concurrently. Threads registered with this
 * session are synchronized.
 *
 * @author <a href="mailto:jima@exoffice.com">Jim Alateras</a>
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:47:20 $
 * @see ServerConnectionImpl
 */
class ServerSessionImpl extends ServerSessionPOA implements ServerSession {
	
	public org.exolab.jms.server.ServerSessionImpl adaptee;

    /**
     * Construct a new <code>ServerSessionImpl</code>.
     *
     * @param connection  the connection that created this session
     * @param ackMode     the acknowledgement mode for the session
     * @param transacted  <code>true</code> if the session is transactional
     * @param messageMgr  the message manager
     * @param consumerMgr the consumer manager
     * @param resourceMgr the resource manager
     * @param database    the database service
     * @param scheduler   the scheduler
     */
    public ServerSessionImpl(ServerConnectionImpl connection, int ackMode,
                             boolean transacted,
                             MessageManager messageMgr,
                             ConsumerManager consumerMgr,
                             ResourceManager resourceMgr,
                             DatabaseService database,
                             Scheduler scheduler) {
    	adaptee = new org.exolab.jms.server.ServerSessionImpl(connection.adaptee, ackMode, transacted, messageMgr, consumerMgr, resourceMgr, database, scheduler);
    }
    
    public ServerSessionImpl(org.exolab.jms.server.ServerSessionImpl impl){
    	adaptee = impl;
    }

    /**
     * Returns the identifier of the connection that created this session.
     *
     * @return the connection identifier
     */
    public long getConnectionId() {
        return adaptee.getConnectionId();
    }

    /**
     * Acknowledge that a message has been processed.
     *
     * @param consumerId the identity of the consumer performing the ack
     * @param messageId  the message identifier
     * @throws JMSException for any error
     */
    public void acknowledgeMessage(long consumerId, String messageId){
//            throws JMSException {
        try {
			adaptee.acknowledgeMessage(consumerId, messageId);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Send a message.
     *
     * @param message the message to send
     * @throws JMSException for any error
     */
    public void send(MessageData message) {
    	MessageImpl msg = new MessageImpl();
    	Util.btyesToExternalizable(message.content, msg);
    	try {
			adaptee.send(msg);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

   

    /**
     * Return the next available mesage to the specified consumer.
     * <p/>
     * This method is non-blocking. If no messages are available, it will return
     * immediately.
     *
     * @param consumerId the consumer identifier
     * @return the next message or <code>null</code> if none is available
     * @throws JMSException for any JMS error
     */
    public MessageData receiveNoWait(long consumerId) {
    	try {
			MessageImpl msg = adaptee.receiveNoWait(consumerId);
			MessageData d = new MessageData(Util.externalizeToBytes(msg));
			return d;
		} catch (JMSException e) {
			return null;
		}
    }

    /**
     * Return the next available message to the specified consumer.
     * <p/>
     * This method is non-blocking. However, clients can specify a
     * <code>wait</code> interval to indicate how long they are prepared to wait
     * for a message. If no message is available, and the client indicates that
     * it will wait, it will be notified via the registered {@link
     * JmsMessageListener} if one subsequently becomes available.
     *
     * @param consumerId the consumer identifier
     * @param wait       number of milliseconds to wait. A value of <code>0
     *                   </code> indicates to wait indefinitely
     * @return the next message or <code>null</code> if none is available
     * @throws JMSException for any JMS error
     */
    public MessageData receive(long consumerId, long wait) {
    	try {
			MessageImpl msg = adaptee.receive(consumerId,wait);
			MessageData d = new MessageData(Util.externalizeToBytes(msg));
			return d;
		} catch (JMSException e) {
			return null;
		}
    }

  
    /**
     * Create a new message consumer.
     *
     * @param destination the destination to consume messages from
     * @param selector    the message selector. May be <code>null</code>
     * @param noLocal     if true, and the destination is a topic, inhibits the
     *                    delivery of messages published by its own connection.
     *                    The behavior for <code>noLocal</code> is not specified
     *                    if the destination is a queue.
     * @return the identifty of the message consumer
     * @throws JMSException for any JMS error
     */
    public long createConsumer(DestinationData destination, String selector,
                               boolean noLocal) {
    	try{
    		JmsDestination d = new JmsQueue();
    		Util.btyesToExternalizable(destination.content, d);
    		return adaptee.createConsumer(d, selector, noLocal);
    	}catch(JMSException e){
    		return -1;
    	}
    }


    /**
     * Close and release any resource allocated to this session.
     *
     * @throws JMSException if the session cannot be closed
     */
    public void close() {
    	try {
			adaptee.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void acknowledgeMessage(int consumerId, String messageId) {
		try {
			adaptee.acknowledgeMessage(consumerId, messageId);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeConsumer(long consumerId) {
		try {
			adaptee.closeConsumer(consumerId);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
