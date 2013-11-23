package incorrectCompositions.detection.offlinePrediction;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.monitor.MonitorData;
import utils.CentralConstants;

public class EngineMain {
	// remember to set the PropertyManager.useJvmStack properly
	public static MonitorData getMonitorData() {
		String path = CommonUtil.getTempFilePath("trace"
				+ CentralConstants.projectname);
		System.err.println("load from trace from: " + path);
		Object obj = CommonUtil.getDeserializedObject(path);
		if (obj instanceof MonitorData)
			return (MonitorData) obj;
		else
			return null;
	}

	public static void main(String[] args) {
		//		PropertyManager.initialize("Test");// projectname

		MonitorData mondata = null;

		Object obj = CommonUtil
				.getDeserializedObject("/Users/charlesz/git/git4api/APIDesigner/tmp/traceTest");
		if (obj instanceof MonitorData)
			mondata = (MonitorData) obj;

		TraceEngine engine = new TraceEngine(mondata);

		// engine.setCheckRace();
		engine.setCheckAtomicity();
		//engine.setCheckAtomSet();

		// engine.setAtomRegionAll();

		//engine.setRemoveRedundance();

		engine.preProcess();

		engine.findAllPatterns();

		engine.saveAVsOnly();

	}
}
