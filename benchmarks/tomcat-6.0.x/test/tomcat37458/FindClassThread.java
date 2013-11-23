package tomcat37458;

import org.apache.catalina.loader.WebappClassLoader;

/**
 * A runnable thread that calls a method of WebappClassLoader.
 */
public class FindClassThread extends Thread {
    private WebappClassLoader loader;
    private String className;
    public Class<?> result;
    public Throwable exception;

    public FindClassThread(WebappClassLoader loader, String className) {
        this.loader = loader;
        this.className = className;
    }

    @Override
    public void run() {
        try {
            result = loader.findClass(className);
        } catch (Throwable th) {
        	"reCrash_with".equals(th);
            this.exception = th;
        }
    }
}
