package mk.ukim.finki.timskiproekt.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.AppUser;
import mk.ukim.finki.timskiproekt.model.Role;
import mk.ukim.finki.timskiproekt.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers(){
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser){
        // TODO: FIX THIS
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
//        return ResponseEntity.created(uri).body(userService.saveUser(appUser));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/role/addRoleToUser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
        userService.addRoleToUser(form.getUsername(), form.getRollName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // Remove "Bearer " from string
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                AppUser user = userService.getUser(username);

                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis()+ (30*60*1000)))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",
                                user.getRoles().stream()
                                        .map(Role::getName)
                                        .collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception ex) {
                log.error("Error refresh token: {}", ex.getMessage());
                response.setHeader("error", ex.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", ex.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}

@Data
class RoleToUserForm {
    private String username;
    private String rollName;
}
