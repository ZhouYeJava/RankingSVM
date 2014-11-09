package exception;

/**
 * Title: Exception From JSON
 * Description: This will happen if you cannot get information from JSON
 * 
 * @author Zhou Ye
 *
 */

public class JSONGetterException extends Exception{

	private static final long serialVersionUID = 1L;

	public JSONGetterException() {}
	
	public JSONGetterException(String message) {
		super(message);
	}
	
}
