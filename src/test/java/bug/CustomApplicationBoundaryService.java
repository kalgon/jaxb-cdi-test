package bug;

import org.apache.webbeans.corespi.se.DefaultApplicationBoundaryService;

public class CustomApplicationBoundaryService extends DefaultApplicationBoundaryService {
	
	public @Override ClassLoader getBoundaryClassLoader(@SuppressWarnings("rawtypes") Class classToProxy) {
		ClassLoader classLoader = super.getBoundaryClassLoader(classToProxy);
		return classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
	}
}
