package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    Optional<Facture> findByCode(String code);
}