package com.openclassrooms.rentals.security;

import com.openclassrooms.rentals.repository.UserRepository;
import com.openclassrooms.rentals.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration centrale de Spring Security pour l'application.
 *
 * <p>Cette classe définit quatre choses :
 * <ul>
 *   <li>Quelles routes sont publiques et lesquelles exigent un JWT.</li>
 *   <li>Comment les mots de passe sont encodés (BCrypt).</li>
 *   <li>Comment charger un utilisateur depuis la base de données.</li>
 *   <li>À quel endroit le filtre JWT est inséré dans la chaîne de sécurité.</li>
 * </ul>
 * </p>
 */
@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public SecurityConfig(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Définit l'algorithme d'encodage des mots de passe utilisé dans toute l'application.
     *
     * @return une instance de BCryptPasswordEncoder, injectée partout où PasswordEncoder est requis
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définit comment Spring Security charge un utilisateur à partir de son identifiant.
     * {@link UserDetailsService} est un contrat (interface) de Spring Security qui expose
     * une seule méthode : {@code loadUserByUsername(String username)}.
     *
     * <p>Ici, on l'implémente directement avec une lambda : la lambda reçoit un email
     * et retourne l'utilisateur correspondant depuis la base de données.
     * {@code orElseThrow} lève une {@link UsernameNotFoundException} si l'email est inconnu,
     * ce que Spring Security intercepte et convertit automatiquement en réponse 401.</p>
     *
     * @return l'implémentation de UserDetailsService utilisée par JwtAuthenticationFilter
     *         et par l'AuthenticationManager
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    /**
     * Instancie le filtre JWT et l'expose comme bean Spring.
     *
     * <p>Le filtre est créé ici (via {@code @Bean}) plutôt qu'annoté {@code @Component}
     * sur la classe directement. La raison : si {@code @Component} était utilisé, Spring Boot
     * enregistrerait le filtre automatiquement comme filtre servlet, ce qui le ferait s'exécuter
     * deux fois par requête — une fois dans la chaîne de sécurité, une fois hors de celle-ci.
     * En le créant ici, on garde le contrôle total sur son cycle de vie.</p>
     *
     * @return l'instance du filtre JWT configurée avec ses deux dépendances
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService());
    }

    /**
     * Expose l'AuthenticationManager de Spring Security comme bean injectable.
     * Utilisé dans AuthService pour valider les credentials lors du login.
     *
     * @param config la configuration d'authentification fournie automatiquement par Spring
     * @return l'AuthenticationManager configuré par Spring Security
     * @throws Exception si la configuration de l'AuthenticationManager échoue
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure la chaîne de filtres de sécurité : le cœur de la configuration Spring Security.
     * C'est ici que se définissent toutes les règles d'accès aux routes de l'API.
     *
     * @param http le builder fourni par Spring pour configurer la sécurité HTTP
     * @return la chaîne de filtres construite, enregistrée comme bean par Spring Security
     * @throws Exception si la construction de la chaîne échoue
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
