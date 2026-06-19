package com.openclassrooms.rentals.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Entité JPA mappée sur la table {@code rentals} en base de données.
 * Représente une annonce de location : elle appartient à un propriétaire (User)
 * et contient les informations affichées dans l'application frontend.
 *
 * <p>{@code @Data} n'est pas utilisé pour la même raison que sur {@link User} :
 * incompatibilité {@code equals()}/{@code hashCode()} avec les proxies Hibernate.</p>
 */
@Entity
@Table(name = "rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double surface;
    private Double price;

    // Ce champ stocke une URL complète vers l'image (ex. "http://localhost:3001/uploads/abc.jpg"),
    // pas un chemin de fichier local ni des données binaires.
    // L'URL est construite par RentalService après avoir sauvegardé le fichier uploadé sur le disque.
    private String picture;

    private String description;

    // @ManyToOne : plusieurs annonces (Many) peuvent appartenir à un même utilisateur (One).
    // C'est la relation "un propriétaire a plusieurs locations".
    // @JoinColumn(name = "owner_id") : définit le nom de la colonne de clé étrangère dans la table
    // rentals — la colonne qui pointe vers la table users.
    //
    // Attention au comportement par défaut : @ManyToOne utilise FetchType.EAGER.
    // Cela signifie que charger une Rental charge automatiquement son User associé en base de données.
    // Pour une seule location c'est acceptable, mais charger une liste de 100 locations déclenchera
    // 100 requêtes supplémentaires (une par User) — c'est le problème dit "N+1 queries".
    // À garder en tête pour une future optimisation avec FetchType.LAZY.
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
