package com.ar.laboratory.demoiaproduct.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad JPA que representa un producto del catálogo.
 *
 * <p>Corresponde a la tabla {@code product}. Incluye control de concurrencia
 * optimista mediante {@link Version} y campos de auditoría temporal.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    /** Identificador único autogenerado. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** SKU único del producto. */
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    @EqualsAndHashCode.Include
    private String sku;

    /** Nombre descriptivo del producto. */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /** Descripción detallada (opcional). */
    @Column(name = "description")
    private String description;

    /** Precio unitario con dos decimales. */
    @Column(name = "price", nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    /** Moneda ISO-4217 (3 letras). */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /** Stock disponible (no negativo). */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    /** Indicador de disponibilidad comercial. */
    @Column(name = "active", nullable = false)
    private Boolean active;

    /** Fecha/hora de creación. */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /** Fecha/hora de última actualización. */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** Versión para control de concurrencia optimista. */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

