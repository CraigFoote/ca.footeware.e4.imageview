/**
 * 
 */
package ca.footeware.imageview.ui.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import ca.footeware.e4.imageview.models.ImageViewDTO;
import ca.footeware.e4.imageview.parts.ImageView;

/**
 * @author <a href="http://Footeware.ca">Footeware.ca</a>
 *
 */
@ExtendWith(SWTBotJunit5Extension.class)
class ImageViewTests {

	private static SWTWorkbenchBot bot;
	private static IEclipseContext context;

	@BeforeAll
	public static void beforeAll() {
		BundleContext bundleContext = FrameworkUtil.getBundle(ImageViewTests.class).getBundleContext();
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
		context = serviceContext.get(IWorkbench.class).getApplication().getContext();
		bot = new SWTWorkbenchBot(context);
	}

	@AfterEach
	void afterEach() {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Could not find Image View";
			}

			@Override
			public boolean test() throws Exception {
				return bot.menu("Window").menu("Show View").menu("Image View").click() != null;
			}
		});
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
				return ImageViewTests.bot.partByTitle("Image View").getPart().isVisible();
			}
		});
	}

	@Test
	void testPerspective() {
		bot.menu("Window").menu("Perspective").menu("Image View").click();
	}

	@Test
	void testBrowseButton() {
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
		bot.partByTitle("Image View").toolbarButton("Browse a folder").click();
		bot.shells()[0].pressShortcut(Keystrokes.ESC);
	}

	@Test
	void testSetInput() {
		MPart mpart = bot.partByTitle("Image View").getPart();
		ImageView view = (ImageView) mpart.getObject();

		String root = System.getProperty("user.dir");
		Path filePath = Paths.get(root, "..", "ca.footeware.e4.imageview.ui", "icons");
		File folder = filePath.toFile();
		File[] files = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			fileNames.add("file://" + file.toString());
		}
		ImageViewDTO dto = new ImageViewDTO();
		dto.setPath(filePath.toString());
		dto.setImages(fileNames);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				view.setInput(dto);
			}
		});

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Could not find Image View label";
			}

			@Override
			public boolean test() throws Exception {
				return ImageViewTests.bot.partByTitle("Image View").bot().label().getText().equals(dto.getFolderName());
			}
		});

		SWTBot viewBot = ImageViewTests.bot.partByTitle("Image View").bot();
		viewBot.canvas().click(10, 10);
		Gallery gallery = viewBot.widget(new GalleryMatcher());

		int numItems = UIThreadRunnable.syncExec(new Result<Integer>() {
			@Override
			public Integer run() {
				GalleryItem[] children = gallery.getItems();
				return children[0].getItemCount();
			}
		});
		assertTrue(numItems == 2);
	}

	class GalleryMatcher extends BaseMatcher<Gallery> {
		@Override
		public boolean matches(Object item) {
			return item instanceof Gallery;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("An image gallery.");
		}
	}
}
