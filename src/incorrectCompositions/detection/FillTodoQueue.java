package incorrectCompositions.detection;

import java.util.List;

import utils.CentralConstants;
import utils.ProjectDirectoryHelper;
import atomicCompositions.serialization.SerializableComposition;
import atomicCompositions.serialization.Serializer;

public class FillTodoQueue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1)
			throw new RuntimeException("argument list: projectname  ");

		CentralConstants.projectname = args[0];//"Test";
		CentralConstants.projectdirectory = ProjectDirectoryHelper
				.findProjectDirGivenName(CentralConstants.projectname);// required!
		//		CentralConstants.mainclass = args[1];
		//		CentralConstants.args = new String[args.length - 2];
		//		for (int i = 2; i < args.length; i++) {
		//			CentralConstants.args[i - 2] = args[i];
		//		}
		System.out.println("Projectname: " + CentralConstants.projectname);

		List<SerializableComposition> serialComps = (List<SerializableComposition>) Serializer
				.deserialize(CentralConstants.getFile4CompositionSet());
		for (SerializableComposition serialComp : serialComps) {
			Serializer.serialize(serialComp,
					CentralConstants.getFile4TodoComposition(serialComp));

		}
		System.out.println("Filling finished");

	}

}
