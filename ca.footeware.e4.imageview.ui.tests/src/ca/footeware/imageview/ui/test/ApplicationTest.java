/**
 * 
 */
package ca.footeware.imageview.ui.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author <a href="http://Footeware.ca">Footeware.ca</a>
 *
 */
@ExtendWith(SWTBotJunit5Extension.class)
class ApplicationTest {

	private static SWTWorkbenchBot bot;
	private static IEclipseContext context;

	@BeforeAll
	public static void beforeAll() {
		BundleContext bundleContext = FrameworkUtil.getBundle(ApplicationTest.class).getBundleContext();
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
		context = serviceContext.get(IWorkbench.class).getApplication().getContext();
		bot = new SWTWorkbenchBot(context);
	}

	@Test
	void testViewOpenByDefault() {
		assertTrue(bot.partByTitle("Image View").getPart().isVisible());
	}

	@Test
	void testCloseView() {
		SWTBotView view = bot.partByTitle("Image View");
		view.close();
		assertFalse(bot.parts().contains(view));
	}

	@Test
	void testOpenViewFromMenu() {
		SWTBotView view = bot.partByTitle("Image View");
		view.close();
		assertFalse(bot.parts().contains(view));
		bot.menu("Window").menu("Show View").menu("Image View").click();

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Could not find Image View";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotView view = ApplicationTest.bot.partByTitle("Image View");
				return view != null && view.getPart().isVisible();
			}
		});
	}
}
