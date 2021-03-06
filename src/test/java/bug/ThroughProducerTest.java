package bug;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.CdiExtensions;
import org.apache.openejb.testing.Module;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public @CdiExtensions class ThroughProducerTest {
	
	public static class JAXBContextProducer {
		
		public @Produces @ApplicationScoped JAXBContext jaxbContext() throws JAXBException {
			return JAXBContext.newInstance();
		}
	}
	
	private @Inject JAXBContext context;
	
	public final @Rule TestRule rule = new ApplicationComposerRule(this);
	
	public @Module Class<?> klass() {
		return JAXBContextProducer.class;
	}
	
	public @Test void test() throws JAXBException {
		assertNotNull(this.context);
		context.createMarshaller();
	}	
}
