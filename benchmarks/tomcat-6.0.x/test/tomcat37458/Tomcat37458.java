package tomcat37458;

import org.apache.catalina.loader.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Permission;
import java.util.jar.JarFile;

import org.apache.naming.resources.FileDirContext;


public class Tomcat37458 {

	public static void main(String[] args)
	{
		new Tomcat37458().go();

	}
	
	public void go()
	{
		for (int i = 0; i < 100; i++) {
            try {
                testFindClassWithSecurityManager();
            } catch (Throwable th) {
            	//"reCrash_with".equals(th);
                System.err.println("Iteration " + i + " resulted in failure:");
                th.printStackTrace();
                System.exit(-1);
                //throw th;
            }
        }
	}
    /**
     * Tests race condition in {@link WebappClassLoader#findClass()} method.
     * 
     * @see bug 37458
     */
    public void testFindClassWithSecurityManager() throws Throwable {
        // Find, where the compiled classes are
        URL classesURL = ((URLClassLoader) WebappClassLoader.class
                .getClassLoader()).getURLs()[0];

        File classesPath = new File(classesURL.getPath());

        // The class that we will try loading is sealed.Sealed
        // from sealed.jar
        final String className = "sealed.Sealed";

        // The sealed.jar is a resource that comes along with this class
        File sealedJarPath = new File(classesPath, this.getClass().getPackage()
                .getName().replace('.', '/')
                + "/sealed.jar");

        boolean smWasSet = false;
        SecurityManager oldSm = System.getSecurityManager();
        if (oldSm == null) {
            System.setSecurityManager(new SecurityManager() {

                @Override
                public void checkPermission(Permission perm, Object context) {
                }

                @Override
                public void checkPermission(Permission perm) {
                }

            });
            smWasSet = true;
        }

        boolean throwableWasCaught = false;
        try {

            WebappClassLoader loader = new WebappClassLoader();

            FileDirContext ctx = new FileDirContext();
            ctx.setDocBase(sealedJarPath.getParent().toString());
            loader.setResources(ctx);

            loader.addJar("sealed.jar", new JarFile(sealedJarPath), sealedJarPath);

            loader.start();

            FindClassThread th1 = new FindClassThread(loader, className);
            FindClassThread th2 = new FindClassThread(loader, className);

            th1.start();
            th2.start();

            th1.join();
            th2.join();

            if (th1.exception != null) {
                throw new RuntimeException("Attempt 1 resulted in exception",
                        th1.exception);
            }
            if (th2.exception != null) {
                throw new RuntimeException("Attempt 2 resulted in exception",
                        th2.exception);
            }
            //assertNotNull("Result 1 was null", th1.result);
            //assertNotNull("Result 2 was null", th2.result);
            //assertEquals(th1.result, th2.result);

        } catch (Throwable th) {
            throwableWasCaught = true;
            throw th;
        } finally {
            if (smWasSet) {
                try {
                    System.setSecurityManager(oldSm);
                } catch (RuntimeException ex) {
                    RuntimeException ex2 = new RuntimeException(
                            "Failed to restore SecurityManager", ex);
                    if (!throwableWasCaught) {
                        throw ex2;
                    }
                    // something more serious happened, let's just print out
                    //ex2.printStackTrace();
                }
            }
        }
    }
}
