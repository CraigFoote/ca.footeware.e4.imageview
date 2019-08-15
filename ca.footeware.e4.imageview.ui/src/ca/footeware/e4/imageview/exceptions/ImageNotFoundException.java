package ca.footeware.e4.imageview.exceptions;

/**
 * @author Footeware.ca
 *
 */
public class ImageNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param e Exception
	 */
	public ImageNotFoundException(Exception e) {
		super(e);
	}

}
