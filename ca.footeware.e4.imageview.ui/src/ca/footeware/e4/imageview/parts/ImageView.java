package ca.footeware.e4.imageview.parts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import ca.footeware.e4.imageview.exceptions.ImageNotFoundException;
import ca.footeware.e4.imageview.models.ImageViewDTO;

/**
 * @author Footeware.ca *
 */
public class ImageView {

	private GalleryItem group;
	private int size = 100;
	private Label pathLabel;
	private String scheme = "file:" + File.separator + File.separator;
	private ImageLoader loader = new ImageLoader();
	@Inject
	UISynchronize sync;

	/**
	 * @param dto {@link ImageView} of {@link String} The URLs to the image files.
	 */
	public void setInput(final ImageViewDTO dto) {
		for (GalleryItem item : group.getItems()) {
			item.getImage().dispose();
			group.remove(item);
		}
		pathLabel.setText(dto.getFolderName());
		List<String> imageNames = dto.getImageNames();

		Job job = Job.create("Set input", (ICoreRunnable) monitor -> {
			monitor.beginTask("Fetch pictures", imageNames.size());
			for (String imageName : imageNames) {
				try {
					Image image = getImage(imageName);
					sync.asyncExec(() -> {
						GalleryItem item = new GalleryItem(group, SWT.None);
						item.setImage(image);
						item.setText(imageName.substring(imageName.lastIndexOf('/') + 1));
						group.getParent().redraw();
					});
				} catch (ImageNotFoundException e) {
					e.printStackTrace();
				}
				monitor.worked(1);
			}
			monitor.done();
		});
		job.setUser(true);
		job.setSystem(false);
		job.schedule();
	}

	/**
	 * @param parent {@link Composite}
	 */
	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		pathLabel = new Label(parent, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(pathLabel);

		final Scale slider = new Scale(parent, SWT.NONE);
		slider.setMinimum(1);
		slider.setMaximum(5);
		slider.setSelection(1);
		slider.setIncrement(1);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(slider);

		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				size = slider.getSelection() * 100;
				((NoGroupRenderer) group.getParent().getGroupRenderer()).setItemSize(size, size);
			}
		});

		Gallery gallery = new Gallery(parent, SWT.V_SCROLL | SWT.VIRTUAL);
		GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(gallery);
		gallery.setVirtualGroups(true);
		group = new GalleryItem(gallery, SWT.NONE);
		NoGroupRenderer gr = new NoGroupRenderer();
		gr.setItemSize(size, size);
		gr.setAutoMargin(true);
		gallery.setGroupRenderer(gr);
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		gallery.setItemRenderer(ir);
		gallery.setItemCount(1);
		gallery.setLowQualityOnUserAction(true);
	}

	private Image getImage(String urlString) throws ImageNotFoundException {
		String replaced = urlString.replace(" ", "%20");

		try (InputStream is = new URI(scheme + replaced).toURL().openStream()) {
			ImageData[] data = loader.load(is);
			if (data.length == 0) {
				throw new ImageNotFoundException("No image data found for " + urlString);
			}
			Image image = new Image(Display.getDefault(), data[0]);
			return image;
		} catch (IOException | URISyntaxException e) {
			throw new ImageNotFoundException(e);
		}
	}

	/**
	 * 
	 */
	@Focus
	public void setFocus() {
		if (group != null && group.getParent() != null && !group.getParent().isDisposed()) {
			group.getParent().setFocus();
		}
	}

	/**
	 * This method manages the selection of your current object. In this example we
	 * listen to a single Object (even the ISelection already captured in E3 mode).
	 * <br/>
	 * You should change the parameter type of your received Object to manage your
	 * specific selection
	 * 
	 * @param o : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {
	}

	/**
	 * This method manages the multiple selection of your current objects. <br/>
	 * You should change the parameter type of your array of Objects to manage your
	 * specific selection
	 * 
	 * @param selectedObjects {@link Object} array - the current array of objects
	 *                        received in case of multiple selection
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {
	}
}
