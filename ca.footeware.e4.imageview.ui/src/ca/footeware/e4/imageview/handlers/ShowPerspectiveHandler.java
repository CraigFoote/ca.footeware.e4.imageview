
package ca.footeware.e4.imageview.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Shows Image View perspective.
 * 
 * @author Footeware.ca
 *
 */
public class ShowPerspectiveHandler {

	/**
	 * 
	 * @param partService {@link EPartService}
	 */
	@Execute
	public void execute(EPartService partService) {
		partService.switchPerspective("ca.footeware.e4.imageview.perspective");
	}

}