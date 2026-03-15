package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Organisme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganismeRepository extends JpaRepository<Organisme, Long> {
    Optional<Organisme> findByCode(String code);
    long count();
    List<Organisme> findByCodeContainingIgnoreCase(String code);
    List<Organisme> findByNomContainingIgnoreCase(String nom);
    List<Organisme> findByNomContactContainingIgnoreCase(String nomContact);
}