package bilalbyg.spotifyClone.core.utilities;

public class ErrorResult extends Result{

	public ErrorResult(boolean success) {
		super(success);
		// TODO Auto-generated constructor stub
	}
	
	public ErrorResult(boolean success, String message) {
		super(success, message);
		// TODO Auto-generated constructor stub
	}

}
