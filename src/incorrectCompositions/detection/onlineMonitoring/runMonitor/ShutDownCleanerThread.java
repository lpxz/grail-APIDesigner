package incorrectCompositions.detection.onlineMonitoring.runMonitor;

import edu.hkust.clap.monitor.Monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import utils.CentralConstants;

public class ShutDownCleanerThread extends Thread {
	private static String	path;
	private static long		start_time;

	public static ObjectOutputStream getOutputStream() {
		ObjectOutputStream out = null;
		try {
			String userdir = System.getProperty("user.dir");// System.getProperty("user.dir")
			if (!(userdir.endsWith("/") || userdir.endsWith("\\"))) {
				userdir = userdir + System.getProperty("file.separator");
			}
			String tempdir = userdir + "tmp";
			File tempFile = new File(tempdir);
			if (!(tempFile.exists()))
				tempFile.mkdirs();

			path = tempdir + System.getProperty("file.separator") + "trace"
					+ CentralConstants.projectname;
			System.out.println("save trace to path: " + path);
			File f = new File(path);
			if (!f.exists())
				f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			out = new ObjectOutputStream(fos);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return out;
	}

	public static void main(String[] args) {
		File tempFile = new File("/home/lpxz/hupu/workspace/qiguai/hama/gaga");

		if (!(tempFile.exists()))
			tempFile.mkdirs();

		File f = new File(tempFile + "/tst.txt");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	private ObjectOutputStream	out;

	public ShutDownCleanerThread() {
		super("MonitorThread");
		start_time = System.currentTimeMillis();
	}

	@Override
	public void run() {
		long end_time = System.currentTimeMillis();
		long exe_time = end_time - start_time;
		System.out.println("come here!!!");
		System.out.println("Monitoring time: " + exe_time + " ms");

		saveMonitorData();
		// Monitor.generateTestDriver(Monitor.saveMonitorData());// for replay..
		System.out.println("Save monitordata to " + path);

	}

	private void saveMonitorData() {
		try {
			out = getOutputStream();

			out.writeObject(Monitor.getMonitorData());
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
