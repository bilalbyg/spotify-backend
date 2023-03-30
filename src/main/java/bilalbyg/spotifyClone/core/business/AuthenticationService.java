package bilalbyg.spotifyClone.core.business;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.core.dataAccess.UserRepository;
import bilalbyg.spotifyClone.core.entities.Role;
import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.core.utilities.requests.AuthenticationRequest;
import bilalbyg.spotifyClone.core.utilities.requests.RegisterRequest;
import bilalbyg.spotifyClone.core.utilities.responses.AuthenticationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.userMail(request.getUserMail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.build();
		
		userRepository.save(user);
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.userId(user.getUserId())
				.build();
	}	
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUserMail(), 
						request.getPassword()
						)
				);
		var user = userRepository.findByUserMail(request.getUserMail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
	    return AuthenticationResponse.builder()
	        .token(jwtToken)
	        .userId(user.getUserId())
	        .build();
	}
}
