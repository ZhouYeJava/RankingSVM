package exception;

/**
 * Title: Exception From HTML
 * Description: This will happen if you cannot get information from HTML
 * 
 * @author Zhou Ye
 *
 */

public class HTMLGetterException extends Exception{

	private static final long serialVersionUID = 1L;

	public HTMLGetterException() {}
	
	public HTMLGetterException(String message) {
		super(message);
	}
	
}
