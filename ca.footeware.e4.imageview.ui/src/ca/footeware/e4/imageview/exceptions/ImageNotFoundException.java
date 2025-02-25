package ca.footeware.e4.imageview.exceptions;

/**
 * @author Footeware.ca
 */
public class ImageNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ImageNotFoundException(Exception e) {
		super(e);
	}

	public ImageNotFoundException(String message) {
		super(message);
	}
	
	public ImageNotFoundException(String message, Exception e) {
		super(message, e);
	}
}
