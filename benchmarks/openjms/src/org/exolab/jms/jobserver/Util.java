package org.exolab.jms.jobserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Util {
	public static byte[] externalizeToBytes(Externalizable obj){
		ByteArrayOutputStream str = new ByteArrayOutputStream();
		try {
			obj.writeExternal(new ObjectOutputStream(str));
			return str.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static void btyesToExternalizable(byte[] data, Externalizable obj ){
		ByteArrayInputStream str = new ByteArrayInputStream(data);
		try {
			obj.readExternal(new ObjectInputStream(str));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
