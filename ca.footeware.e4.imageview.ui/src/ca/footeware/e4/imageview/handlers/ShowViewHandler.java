/**
 * 
 */
package ca.footeware.e4.imageview.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * @author Footeware.ca
 *
 */
public class ShowViewHandler {

	@Inject
	EPartService partService;

	/**
	 * @param item {@link MDirectMenuItem}
	 */
	@Execute
	public void execute(MDirectMenuItem item) {
		partService.showPart(item.getElementId(), PartState.ACTIVATE);
	}

}
