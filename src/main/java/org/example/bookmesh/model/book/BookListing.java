package org.example.bookmesh.model.book;

import jakarta.persistence.*;
import lombok.*;
import org.example.bookmesh.model.User;

@Entity
@Table(
        name = "book_listings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "supplier_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private User supplier;

    @Column(nullable = false)
    private int stockQuantity;
}