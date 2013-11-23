package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.monitor.MonitorData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CommonUtil {
	private static FileWriter	fw;
	private static String		tempDir;

	public static void closeFileWriter() {
		try {
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createFileWriter(String filename) {
		try {
			fw = new FileWriter(new File(CommonUtil.getTempFilePath(filename)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteFile(File f) {
		// Get all files in directory
		File[] files = f.listFiles();
		for (File file : files) {
			// Delete each file

			if (!file.delete()) {
				// Failed to delete file
				// System.out.println("Failed to delete "+file);
			}
		}
	}

	public static Object getDeserializedObject(String path) {
		Object obj = null;
		try {
			File traceFile = new File(path);
			FileInputStream fis = new FileInputStream(traceFile);
			ObjectInputStream in = new ObjectInputStream(fis);

			obj = in.readObject();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return obj;
		}
	}

	public static ObjectOutputStream getOutputStream(String filename) {
		ObjectOutputStream out = null;
		try {
			String path = filename;
			File f = new File(path);
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return out;
	}

	public static String getTempDir() {
		if (tempDir == null) {
			String userdir = System.getProperty("user.dir");// user.dir
			if (!(userdir.endsWith("/") || userdir.endsWith("\\"))) {
				userdir = userdir + System.getProperty("file.separator");
			}
			tempDir = userdir + "tmp" + System.getProperty("file.separator");

			File tempFile = new File(tempDir);

			if (!tempFile.exists()) {
				tempFile.mkdir();
			} else {
				// deleteFile(tempFile);
			}
		}
		return tempDir;
	}

	public static String getTempFilePath(String string) {

		String path = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "tmp"
				+ System.getProperty("file.separator") + string;
		return path;
	}

	public static String getTmpTransDirectory() {
		String tempdir = System.getProperty("user.dir");
		tempdir = tempdir.replace("monitor", "transformer");

		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir + "tmp" + System.getProperty("file.separator");
		return tempdir;
	}

	public static Object loadObject(ObjectInputStream in) {
		Object o = null;
		try {
			o = in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return o;
		}
	}

	public static void print(String str) {
		System.out.println(str);
		try {
			fw.write(str + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printerr(String str) {
		System.err.println(str);
		System.out.println("--------------------------------------");
		try {
			fw.write(str + "\n");
			fw.write("--------------------------------------" + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void saveProcessMonitorData(MonitorData mondata) {
		ObjectOutputStream out = null;

		try {
			String path = System.getProperty("user.dir");
			path = path + System.getProperty("file.separator") + "tmp"
					+ System.getProperty("file.separator") + "trace";
			File f = new File(path);
			f.delete();

			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
			out.writeObject(mondata);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
