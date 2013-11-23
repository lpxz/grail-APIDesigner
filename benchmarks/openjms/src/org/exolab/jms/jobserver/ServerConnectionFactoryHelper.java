// **********************************************************************
//
// Generated by the ORBacus IDL to Java Translator
//
// Copyright (c) 2005
// IONA Technologies, Inc.
// Waltham, MA, USA
//
// All Rights Reserved
//
// **********************************************************************

// Version: 4.3.2

package org.exolab.jms.jobserver;

//
// IDL:org.exolab.jms/org/exolab/jms/jobserver/ServerConnectionFactory:1.0
//
final public class ServerConnectionFactoryHelper
{
    public static void
    insert(org.omg.CORBA.Any any, ServerConnectionFactory val)
    {
        any.insert_Object(val, type());
    }

    public static ServerConnectionFactory
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return narrow(any.extract_Object());

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_interface_tc(id(), "ServerConnectionFactory");
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:org.exolab.jms/org/exolab/jms/jobserver/ServerConnectionFactory:1.0";
    }

    public static ServerConnectionFactory
    read(org.omg.CORBA.portable.InputStream in)
    {
        org.omg.CORBA.Object _ob_v = in.read_Object();

        if(_ob_v != null)
        {
            if(_ob_v instanceof ServerConnectionFactory)
                return (ServerConnectionFactory)_ob_v;

            org.omg.CORBA.portable.ObjectImpl _ob_impl;
            _ob_impl = (org.omg.CORBA.portable.ObjectImpl)_ob_v;
            _ServerConnectionFactoryStub _ob_stub = new _ServerConnectionFactoryStub();
            _ob_stub._set_delegate(_ob_impl._get_delegate());
            return _ob_stub;
        }

        return null;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, ServerConnectionFactory val)
    {
        out.write_Object(val);
    }

    public static ServerConnectionFactory
    narrow(org.omg.CORBA.Object val)
    {
        if(val != null)
        {
            if(val instanceof ServerConnectionFactory)
                return (ServerConnectionFactory)val;

            if(val._is_a(id()))
            {
                org.omg.CORBA.portable.ObjectImpl _ob_impl;
                _ServerConnectionFactoryStub _ob_stub = new _ServerConnectionFactoryStub();
                _ob_impl = (org.omg.CORBA.portable.ObjectImpl)val;
                _ob_stub._set_delegate(_ob_impl._get_delegate());
                return _ob_stub;
            }

            throw new org.omg.CORBA.BAD_PARAM();
        }

        return null;
    }

    public static ServerConnectionFactory
    unchecked_narrow(org.omg.CORBA.Object val)
    {
        if(val != null)
        {
            if(val instanceof ServerConnectionFactory)
                return (ServerConnectionFactory)val;

            org.omg.CORBA.portable.ObjectImpl _ob_impl;
            _ServerConnectionFactoryStub _ob_stub = new _ServerConnectionFactoryStub();
            _ob_impl = (org.omg.CORBA.portable.ObjectImpl)val;
            _ob_stub._set_delegate(_ob_impl._get_delegate());
            return _ob_stub;
        }

        return null;
    }
}