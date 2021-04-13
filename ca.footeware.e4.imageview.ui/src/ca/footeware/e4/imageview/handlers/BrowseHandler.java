
package ca.footeware.e4.imageview.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import ca.footeware.e4.imageview.models.ImageViewDTO;
import ca.footeware.e4.imageview.parts.ImageView;

/**
 * @author Footeware.ca
 *
 */
public class BrowseHandler {

	private List<String> extensions = Arrays.asList(new String[] { "jpg", "JPG", "jpeg", "JPEG", "png", "PNG" });

	@Inject
	EPartService partService;

	/**
	 * 
	 */
	@Execute
	public void execute() {
		DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
		String result = dialog.open();
		if (result != null) {
			
			ImageViewDTO dto = new ImageViewDTO();
			dto.setPath(result);
			
			File folder = new File(result);
			if (folder.exists() && folder.canRead() && folder.isDirectory()) {
				File[] files = folder.listFiles();
				List<String> imagePaths = new ArrayList<>();
				for (File file : files) {
					if (file.exists() && file.canRead() && file.isFile()) {
						Optional<String> extension = Optional.ofNullable(file.getName()).filter(f -> f.contains("."))
								.map(f -> f.substring(file.getName().lastIndexOf(".") + 1));
						if (extensions.contains(extension.get())) {
							imagePaths.add("file://"+file.getAbsolutePath());
						}
					}
				}
				dto.setImages(imagePaths);
				MPart activeMPart = partService.getActivePart();
				Object part = activeMPart.getObject();
				if (part instanceof ImageView) {
					((ImageView) part).setInput(dto);
				}
			}
		}
	}

}