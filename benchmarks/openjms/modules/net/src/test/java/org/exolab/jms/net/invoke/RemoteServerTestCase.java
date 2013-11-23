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
 * Copyright 2007 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: RemoteServerTestCase.java,v 1.1 2010/06/18 16:48:52 smhuang Exp $
 */

package org.exolab.jms.net.invoke;

import org.exolab.jms.net.RemoteServer;
import org.exolab.jms.net.orb.ORB;
import org.exolab.jms.net.proxy.Proxy;
import org.exolab.jms.net.registry.Registry;

import java.util.Map;


/**
 * Base class for test cases where the server may run in another JVM.
 *
 * @author <a href="mailto:tma@netspace.net.au">Tim Anderson</a>
 * @version $LastChangedDate: 2006-05-02 05:16:31Z $
 */
public abstract class RemoteServerTestCase extends RemoteTestCase {

    /**
     * Creates a new <tt>RemoteServerTestCase</tt>.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     */
    public RemoteServerTestCase(String name, String uri,
                                boolean embeddedService) {
        super(name, uri, embeddedService);
    }

    /**
     * Creates a new <tt>RemoteServerTestCase</tt>.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     * @param properties      connection properties. May be <tt>null</tt>
     */
    public RemoteServerTestCase(String name, String uri,
                                boolean embeddedService, Map properties) {
        super(name, uri, null, embeddedService, properties);
    }

    /**
     * Creates a new <tt>RemoteServerTestCase</tt>.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param routeURI        the route URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     */
    public RemoteServerTestCase(String name, String uri, String routeURI,
                                boolean embeddedService) {
        super(name, uri, routeURI, embeddedService);
    }

    /**
     * Creates a new <tt>RemoteServerTestCase</tt>.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param routeURI        the route URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     * @param properties      connection properties. May be <tt>null</tt>
     */
    public RemoteServerTestCase(String name, String uri, String routeURI,
                                boolean embeddedService, Map properties) {
        super(name, uri, routeURI, embeddedService, properties);
    }

    /**
     * Creates a new <tt>RemoteServerTestCase</tt>.
     *
     * @param name            the name of test case
     * @param uri             the export URI
     * @param routeURI        the route URI
     * @param embeddedService determines if the service will be run in the
     *                        current JVM
     * @param connectionProps connection properties. May be <tt>null</tt>
     * @param acceptorProps   acceptor properites. May be <tt>null</tt>
     */
    public RemoteServerTestCase(String name, String uri, String routeURI,
                                boolean embeddedService, Map connectionProps,
                                Map acceptorProps) {
        super(name, uri, routeURI, embeddedService, connectionProps,
              acceptorProps);
    }

    /**
     * Starts the server.
     *
     * @param server the server class
     */
    protected void startServer(Class server, String serviceName)
            throws Exception {
        if (isEmbeddedService()) {
            ORB orb = getORB();
            Registry serverRegistry = orb.getRegistry();
            Proxy proxy = orb.exportObject(server.newInstance());
            serverRegistry.bind(serviceName, proxy);
        } else {
            StringBuffer args = new StringBuffer(serviceName);
            args.append(" ");
            args.append(server.getName());
            args.append(" ");
            args.append(getURI());
            String routeURI = getRouteURI();
            if (routeURI != null) {
                args.append(" ");
                args.append(routeURI);
            }
            startJVM(RemoteServer.class, args.toString());
        }
    }

}
