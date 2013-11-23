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
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: DestinationEventListener.java,v 1.1 2010/06/18 16:48:32 smhuang Exp $
 *
 * Date         Author  Changes
 * 05/17/2001   jima    Created
 */
package org.exolab.jms.messagemgr;

import javax.jms.JMSException;

import org.exolab.jms.client.JmsDestination;


/**
 * A DestinatonEventListener will be notified of events generated by the {@link
 * DestinationManager}.
 *
 * @author <a href="mailto:jima@comware.com.au">Jim Alateras</a>
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $Revision: 1.1 $ $Date: 2010/06/18 16:48:32 $
 */
public interface DestinationEventListener {

    /**
     * Invoked when a destination is created.
     *
     * @param destination the destination that was added
     * @throws JMSException for any error
     */
    void destinationAdded(JmsDestination destination) throws JMSException;

    /**
     * Invoked when a destination is removed.
     *
     * @param destination the destination that was removed
     * @throws JMSException for any error
     */
    void destinationRemoved(JmsDestination destination) throws JMSException;

    /**
     * Invoked when a message cache is created.
     *
     * @param destination the destination that messages are being cached for
     * @param cache       the corresponding cache
     */
    void cacheAdded(JmsDestination destination, DestinationCache cache);

    /**
     * Invoked when a message cache is removed.
     *
     * @param destination the destination that messages are no longer being
     *                    cached for
     * @param cache       the corresponding cache
     */
    void cacheRemoved(JmsDestination destination, DestinationCache cache);
}

