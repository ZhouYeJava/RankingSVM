package exception;
/**
 * This will be thrown if the parameters cannot be imported correctly
 * @author Zhou Ye
 */

public class ParameterImportException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParameterImportException() {}
	
	public ParameterImportException(String message) {
		super(message);
	}
	
}
