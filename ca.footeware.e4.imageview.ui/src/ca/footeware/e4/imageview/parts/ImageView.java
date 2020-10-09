package ca.footeware.e4.imageview.parts;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

import ca.footeware.e4.imageview.exceptions.ImageNotFoundException;
import ca.footeware.e4.imageview.models.ImageViewDTO;

/**
 * @author Footeware.ca
 *
 */
public class ImageView {

	private Gallery gallery;
	private List<Image> toBeDestroyed = new ArrayList<>();
	private List<String> urls = new ArrayList<>();
	private static int SIZE = 16;
	private Label pathLabel;
	@Inject
	UISynchronize sync;

	/**
	 * @param dto {@link List} of {@link String} The URLs to the image files.
	 */
	public void setInput(final ImageViewDTO dto) {
		pathLabel.setText(dto.getFolderName());
		this.urls.addAll(dto.getImageNames());
		gallery.clearAll();
		GalleryItem group = new GalleryItem(gallery, SWT.BORDER);
		final Map<String, Image> imageMap = new HashMap<>();

		Job job = Job.create("Get images", (ICoreRunnable) monitor -> {
			for (String imageName : dto.getImageNames()) {
				if (!monitor.isCanceled()) {
					try {
						Image image = getImage(imageName);
						imageMap.put(imageName, image);
						toBeDestroyed.add(image);
					} catch (ImageNotFoundException e1) {
						// TODO report
					}

					sync.asyncExec(() -> {
						GalleryItem item = new GalleryItem(group, SWT.BORDER);
						item.setText(imageName.substring(imageName.lastIndexOf('/') + 1));
						item.setImage(imageMap.get(imageName));
//						gallery.refresh(0);
					});
				}
			}
		});
		job.setUser(false);
		job.schedule();
	}

	/**
	 * @param parent {@link Composite}
	 */
	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		pathLabel = new Label(parent, SWT.BORDER);
		GridData gridData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create();
		pathLabel.setLayoutData(gridData);

		Slider slider = new Slider(parent, SWT.NONE);
		slider.setMinimum(16);
		slider.setMaximum(10000);
		slider.setValues(100, 100, 2000, 100, 10, 20);
		gridData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create();
		slider.setLayoutData(gridData);

		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SIZE = slider.getSelection();
				NoGroupRenderer groupRenderer = (NoGroupRenderer) gallery.getGroupRenderer();
				groupRenderer.setItemSize(SIZE, SIZE);
			}
		});

		gallery = new Gallery(parent, SWT.V_SCROLL | SWT.VIRTUAL | SWT.BORDER);
		gridData = GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create();
		gallery.setLayoutData(gridData);
		gallery.setAntialias(SWT.ON);

		NoGroupRenderer gr = new NoGroupRenderer();
		gr.setMinMargin(5);
		gr.setItemSize(SIZE, SIZE);
		gr.setAutoMargin(true);
		gallery.setGroupRenderer(gr);

		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		ir.setShowLabels(true);
		gallery.setItemRenderer(ir);

		gallery.setVirtualGroups(true);
		gallery.setItemCount(0);

		gallery.setData(urls);

		gallery.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.err.println(event);
				if (!urls.isEmpty()) {
					GalleryItem item = (GalleryItem) event.item;
					try {
						displayItem(item);
					} catch (ImageNotFoundException e) {
						// TODO report
					}
				}
			}
		});
	}

	/**
	 * @throws ImageNotFoundException
	 * 
	 */
	protected void displayItem(GalleryItem item) throws ImageNotFoundException {
		int index = 0;
		if (item.getParent() != null) {
			index = item.getParent().indexOf(item);
		}
		@SuppressWarnings("unchecked")
		List<String> urlStrings = (List<String>) gallery.getData();
		String urlString = urlStrings.get(index);
		Image image = getImage(urlString);
		item.setText(urlString);
		item.setImage(image);
	}

	private Image getImage(String urlString) throws ImageNotFoundException {
		ImageLoader loader = new ImageLoader();
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new ImageNotFoundException(e);
		}
		ImageData[] data = null;
		try (InputStream is = url.openStream()) {
			data = loader.load(is);
			Image image = new Image(gallery.getDisplay(), data[0]);
			toBeDestroyed.add(image);
			return image;
		} catch (IOException | SWTException e) {
			throw new ImageNotFoundException(e);
		}
	}

	/**
	 * 
	 */
	@Focus
	public void setFocus() {
		gallery.setFocus();
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

	@PreDestroy
	private void preDestroy() {
		if (toBeDestroyed != null) {
			for (Image image : toBeDestroyed) {
				if (image != null) {
					image.dispose();
				}
			}
		}
	}
}
