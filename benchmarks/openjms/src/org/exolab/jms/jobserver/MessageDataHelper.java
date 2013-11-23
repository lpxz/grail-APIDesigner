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
// IDL:org.exolab.jms/org/exolab/jms/jobserver/MessageData:1.0
//
final public class MessageDataHelper
{
    public static void
    insert(org.omg.CORBA.Any any, MessageData val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static MessageData
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "content";
            members[0].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));

            typeCode_ = orb.create_struct_tc(id(), "MessageData", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:org.exolab.jms/org/exolab/jms/jobserver/MessageData:1.0";
    }

    public static MessageData
    read(org.omg.CORBA.portable.InputStream in)
    {
        MessageData _ob_v = new MessageData();
        int len0 = in.read_ulong();
        _ob_v.content = new byte[len0];
        in.read_octet_array(_ob_v.content, 0, len0);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, MessageData val)
    {
        int len0 = val.content.length;
        out.write_ulong(len0);
        out.write_octet_array(val.content, 0, len0);
    }
}
