package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Affectation;
import com.projet.suivi_projet.entity.AffectationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffectationRepository extends JpaRepository<Affectation, AffectationId> {
}