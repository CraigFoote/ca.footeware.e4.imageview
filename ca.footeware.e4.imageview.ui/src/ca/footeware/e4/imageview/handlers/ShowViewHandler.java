/**
 * 
 */
package ca.footeware.e4.imageview.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * @author Footeware.ca
 *
 */
public class ShowViewHandler {

	private static final String partDescriptorId = "ca.footeware.e4.imageview.ui.partdescriptor.imageview";
	private static final String partId = "ca.footeware.e4.imageview.part.imageview";
	
	/**
	 * 
	 * @param app
	 * @param partService
	 */
	@Execute
	public void execute(MApplication app, EPartService partService) {
		MPart mPart = partService.findPart(partId);
		PartState state;
		if (mPart == null) {
			mPart = partService.createPart(partDescriptorId);
			state = PartState.CREATE;
		} else {
			state = PartState.VISIBLE;
		}
		partService.showPart(mPart, state);
	}
}
