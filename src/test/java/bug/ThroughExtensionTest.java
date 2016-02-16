package bug;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.literal.DefaultLiteral;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;
import org.apache.openejb.junit.ApplicationComposerRule;
import org.apache.openejb.testing.CdiExtensions;
import org.apache.openejb.testing.Module;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import bug.ThroughExtensionTest.MyExtension;

public @CdiExtensions(MyExtension.class) class ThroughExtensionTest {
	
	public static class MyExtension implements Extension {
	
		public void addBean(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
			try {
				Bean<JAXBContext> bean = new BeanBuilder<JAXBContext>(beanManager)
					.beanClass(JAXBContext.class) //
					.scope(ApplicationScoped.class) //
					.types(JAXBContext.class, Object.class) //
					.qualifiers(new DefaultLiteral(), new AnyLiteral()) //
					.beanLifecycle(new SingletonLifecycle<>(JAXBContext.newInstance()))
					.id("jaxb-context") //
					.create();
				afterBeanDiscovery.addBean(bean);
			} catch (JAXBException jaxbException) {
				afterBeanDiscovery.addDefinitionError(jaxbException);
			}
		}
	}
	
	public static class SingletonLifecycle<T> implements ContextualLifecycle<T> {
		
		private final T instance;
		
		public SingletonLifecycle(T instance) {
			this.instance = instance;
		}

		public @Override T create(Bean<T> bean, CreationalContext<T> creationalContext) {
			return this.instance;
		}
			
		public @Override void destroy(Bean<T> bean, T instance, CreationalContext<T> creationalContext) {}
	}

	private @Inject JAXBContext context;
	
	public final @Rule TestRule rule = new ApplicationComposerRule(this);
	
	public @Module Class<?>[] klasses() {
		return new Class<?>[0];
	}
	
	public @Test void test() throws JAXBException {
		assertNotNull(this.context); // will fail
		context.createMarshaller();
	}	
}
