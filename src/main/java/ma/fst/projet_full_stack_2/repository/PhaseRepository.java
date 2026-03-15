package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PhaseRepository extends JpaRepository<Phase, Long> {

    Optional<Phase> findByCode(String code);

    List<Phase> findByDateFinBetween(LocalDate dateDebut, LocalDate dateFin);

    List<Phase> findByDateFinBetweenAndEtatRealisationTrueAndEtatFacturationFalse(
            LocalDate dateDebut, LocalDate dateFin
    );

    List<Phase> findByDateFinBetweenAndEtatFacturationTrueAndEtatPaiementFalse(
            LocalDate dateDebut, LocalDate dateFin
    );

    List<Phase> findByDateFinBetweenAndEtatPaiementTrue(
            LocalDate dateDebut, LocalDate dateFin
    );
    long countByEtatRealisationTrue();

    long countByEtatRealisationTrueAndEtatFacturationFalse();

    long countByEtatFacturationTrueAndEtatPaiementFalse();

    long countByEtatPaiementTrue();
}