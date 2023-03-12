package bilalbyg.spotifyClone.core.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Result {
	private boolean success;
	private String message;
	
	public Result(boolean success) {
		this.success = success;
	}
}
