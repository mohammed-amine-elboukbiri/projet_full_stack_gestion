package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.entities.Projet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjetRepository extends JpaRepository<Projet, Long> {

    Optional<Projet> findByCode(String code);

    List<Projet> findByCodeContainingIgnoreCase(String code);

    List<Projet> findByNomContainingIgnoreCase(String nom);

    List<Projet> findByOrganismeId(Long organismeId);

    List<Projet> findByChefProjetId(Long chefProjetId);
}
