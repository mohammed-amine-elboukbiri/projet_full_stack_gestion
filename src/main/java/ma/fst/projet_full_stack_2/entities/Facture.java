package ma.fst.projet_full_stack_2.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDate dateFacture;

    @OneToOne
    @JoinColumn(name = "phase_id", nullable = false, unique = true)
    private Phase phase;
}
