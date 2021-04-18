package ca.footeware.imageview.ui.test.matchers;

import org.eclipse.nebula.widgets.gallery.Gallery;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class GalleryMatcher extends BaseMatcher<Gallery> {
	
	@Override
	public boolean matches(Object item) {
		return item instanceof Gallery;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("An image gallery.");
	}
}