/**
 * 
 */
package ca.footeware.e4.imageview.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * @author Footeware.ca
 *
 */
public class ShowViewHandler {

	/**
	 * 
	 * @param partService
	 */
	@Execute
	public void execute(EPartService partService) {
		partService.showPart("ca.footeware.e4.imageview.part.imageview", PartState.ACTIVATE);
	}

}
