package jroullet.mswebapp.auth;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jroullet.mswebapp.clients.IdentityFeignClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service // Authentication procedure done through AuthenticationProvider
public class AuthenticationService implements AuthenticationProvider {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final IdentityFeignClient identityFeignClient;
    private final HttpServletRequest request;

    //Consider it as a Post login form method
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        logger.info("Trying to authenticate with {}", email);

        try {
            // Provides ids to ms-identity for verification
            AuthRequestDTO requestDTO = new AuthRequestDTO();
            requestDTO.setEmail(email);
            requestDTO.setPassword(password);

            logger.info("Sending request to ms-identity...");

            // ms-identity verifies password matches with database
            AuthResponseDTO responseDTO = identityFeignClient.authenticate(requestDTO).getBody();
            logger.info("Answer from ms-identity : {}", responseDTO);

            if(responseDTO.isAuthenticated()) {
//                request.getSession().setAttribute("currentUserDTO", responseDTO);
                // Building authorities based on returned role
                List<GrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + responseDTO.getRole())
                );

                // Returns a successful authentication token
                return new UsernamePasswordAuthenticationToken(
                        email,
                        null, // password is erased after authentication
                        authorities);
            }
            logger.error("Authentication failed");
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
        catch (FeignException.Unauthorized e){
            logger.error("Email ou mot de passe incorrect");
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
        catch (Exception e){
            logger.error("Error authenticating user: " + email, e);
            throw new AuthenticationServiceException("Erreur lors de l'authentification", e);
        }
    }

    // Allows authentication of type user/password
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
