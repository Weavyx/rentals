package com.openclassrooms.rentals.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Service responsable de toutes les opérations JWT : génération, extraction, validation.
 *
 * <p>Un JWT (JSON Web Token) est une chaîne signée qui prouve l'identité d'un utilisateur
 * sans nécessiter de requête en base de données à chaque requête HTTP.
 * Format : header.payload.signature — chaque partie est encodée en base64.
 * Le serveur signe le token à la connexion ; le client le renvoie à chaque requête ;
 * le serveur vérifie la signature pour s'assurer que le token n'a pas été falsifié.</p>
 *
 * <p>La clé secrète est lue depuis application.properties (app.jwt.secret).
 * Elle doit faire au minimum 32 caractères pour l'algorithme HS256.</p>
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    /**
     * Génère un token JWT signé pour l'utilisateur donné.
     * Le sujet du token est l'email de l'utilisateur — c'est ce qui identifiera l'utilisateur
     * dans chaque requête ultérieure, sans toucher à la base de données.
     *
     * @param userDetails l'utilisateur authentifié (fournit l'email via getUsername())
     * @return le token JWT sous forme de String, prêt à être envoyé au client dans AuthResponse
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait l'email (le "subject") contenu dans le payload du token JWT.
     * Délègue à extractClaim avec une référence de méthode pour éviter de dupliquer
     * le code de parsing.
     *
     * @param token le token JWT reçu dans le header Authorization de la requête
     * @return l'email de l'utilisateur tel qu'il a été enregistré lors de la génération du token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Vérifie qu'un token est valide pour un utilisateur donné.
     * Deux conditions doivent être remplies simultanément :
     * 1. L'email contenu dans le token correspond bien à l'utilisateur chargé depuis la base.
     * 2. Le token n'a pas dépassé sa date d'expiration.
     * Un token expiré mais avec le bon email est refusé, et vice versa.
     *
     * @param token       le token JWT à valider
     * @param userDetails l'utilisateur fraîchement chargé depuis la base de données
     * @return true si le token est authentique et encore valide, false sinon
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Vérifie si la date d'expiration du token est antérieure à maintenant.
     *
     * @param token le token JWT à inspecter
     * @return true si le token est expiré, false s'il est encore valide
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Méthode générique pour extraire n'importe quel champ du payload JWT.
     * Centralise le code de parsing du token pour éviter de le dupliquer dans chaque
     * méthode d'extraction (extractEmail, isTokenExpired, etc.).
     *
     * <p><strong>Le paramètre de type {@code <T>}</strong> est un placeholder de type générique :
     * l'appelant décide quel type sera retourné. Par exemple :
     * extractClaim(token, Claims::getSubject) retourne un {@code String},
     * extractClaim(token, Claims::getExpiration) retourne un {@code Date}.
     * Cela évite d'écrire une méthode séparée pour chaque champ.</p>
     *
     * @param <T>            le type de la valeur retournée, déterminé par le claimsResolver fourni
     * @param token          le token JWT brut à parser et à vérifier
     * @param claimsResolver une fonction qui reçoit le payload décodé et retourne le champ souhaité
     * @return la valeur extraite du payload, du type T
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    /**
     * Convertit le secret brut (String) en clé cryptographique utilisable par jjwt.
     * jjwt n'accepte pas une String directement comme clé de signature : il exige un objet
     * {@link Key} pour pouvoir vérifier que la longueur est suffisante (min. 256 bits pour HS256).
     *
     * @return la clé cryptographique HMAC dérivée du secret configuré dans application.properties
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
