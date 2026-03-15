package ma.fst.projet_full_stack_2.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String libelle;


    private String description;

    @Column(nullable = false)
    private String chemin;

    @ManyToOne
    @JoinColumn(name = "phase_id", nullable = false)
    private Phase phase;
}
