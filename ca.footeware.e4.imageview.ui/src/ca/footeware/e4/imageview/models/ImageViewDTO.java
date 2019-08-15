/**
 * 
 */
package ca.footeware.e4.imageview.models;

import java.util.List;

/**
 * @author Footeware.ca
 *
 */
public class ImageViewDTO {
	private String path;
	private List<String> images;

	/**
	 * @return {@link String}
	 */
	public String getFolderName() {
		return path;
	}

	/**
	 * @param path {@link String}
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return {@link List} of {@link String}
	 */
	public List<String> getImageNames() {
		return images;
	}

	/**
	 * @param images {@link List} of {@link String}
	 */
	public void setImages(List<String> images) {
		this.images = images;
	}

}
