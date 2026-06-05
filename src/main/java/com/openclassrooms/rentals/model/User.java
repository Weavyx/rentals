package com.openclassrooms.rentals.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;-

/**
 * Entité JPA mappée sur la table {@code users} en base de données.
 * Implémente également {@link UserDetails} (contrat de Spring Security) pour que l'objet
 * User puisse être utilisé directement par Spring Security, sans classe wrapper séparée.
 *
 * <p><strong>Pourquoi pas {@code @Data} ?</strong>
 * {@code @Data} génère {@code equals()} et {@code hashCode()} basés sur <em>tous</em> les champs.
 * Sur des entités JPA, cela casse Hibernate : deux objets User représentant la même ligne en base
 * ne seront pas considérés comme égaux si l'un d'eux est un proxy Hibernate (chargement différé).
 * {@code @Getter} et {@code @Setter} sont la solution sûre : ils ne génèrent que les accesseurs.</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // Le mot de passe stocké ici est toujours un hash BCrypt, jamais le mot de passe en clair.
    // Il ne doit jamais apparaître dans les réponses API : UserResponse ne contient pas ce champ.
    private String password;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    /**
     * Retourne les rôles et permissions de l'utilisateur.
     * Cette application ne gère pas de rôles différenciés : tous les utilisateurs ont
     * les mêmes droits. La liste vide signifie "aucune permission spéciale", ce qui
     * suffit pour le contrôle d'accès basé sur l'authentification seule (authentifié vs non).
     *
     * @return une liste vide (aucun rôle différencié dans cette application)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Retourne le mot de passe haché de l'utilisateur.
     * Spring Security appelle cette méthode pour comparer le mot de passe fourni au login
     * avec le hash stocké en base, via le PasswordEncoder (BCrypt).
     *
     * <p>L'override explicite est nécessaire même si {@code @Getter} génère déjà un getter
     * pour le champ {@code password} : sans cet override, le compilateur peut ne pas reconnaître
     * le getter Lombok comme implémentation de la méthode abstraite {@code getPassword()}
     * de l'interface {@link UserDetails}, selon la configuration du processeur d'annotations.</p>
     *
     * @return le hash BCrypt du mot de passe de l'utilisateur
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retourne l'identifiant unique de l'utilisateur au sens de Spring Security.
     * Bien que la méthode s'appelle {@code getUsername()}, elle retourne l'email —
     * c'est ce que {@code authentication.getName()} retourne dans les contrôleurs.
     *
     * @return l'email de l'utilisateur, utilisé comme identifiant unique dans tout le système de sécurité
     */
    @Override
    public String getUsername() {
        return email;
    }
}
