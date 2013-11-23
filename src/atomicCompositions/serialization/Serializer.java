package atomicCompositions.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.CentralConstants;

import atomicCompositions.datastructure.Composition;

public class Serializer {

	public static void main(String[] args) throws IOException {
	}

	public static List<SerializableComposition> toSerializableCompositions(
			Set<Composition> g_atomic_compositions_use) {

		List<SerializableComposition> toserialize = new ArrayList<SerializableComposition>();
		for (Composition composition : g_atomic_compositions_use) {
			SerializableComposition serializableComposition = SerializableComposition.Factory
					.toSerializableComposition(composition);
			if (!toserialize.contains(serializableComposition))
				toserialize.add(serializableComposition);
		}
		return toserialize;
	}

	// save to the atomic_compositions folder, the file name is the projectname.
	//List<SerializableComposition> toserialize
	public static void serialize(Object toserialize, String filename) {

		ObjectOutputStream out = null;
		try {
			File f = new File(filename);
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
			out.writeObject(toserialize);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Object deserialize(String filename) {

		if (CentralConstants.projectname == null)
			throw new RuntimeException(
					"you must set the project name to proceed.");

		Object deserialized = null;//List<SerializableComposition>
		ObjectInputStream in = null;
		try {
			File f = new File(filename);
			if (!f.exists())
				f.createNewFile();
			FileInputStream fis = new FileInputStream(f);
			in = new ObjectInputStream(fis);
			deserialized = in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return deserialized;

	}

}
