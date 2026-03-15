package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeRepository extends JpaRepository<Employe, Long> {
    Optional<Employe> findByMatricule(String matricule);
    Optional<Employe> findByEmail(String email);
    Optional<Employe> findByLogin(String login);
    long count();
    List<Employe> findByNomContainingIgnoreCase(String nom);
    List<Employe> findByPrenomContainingIgnoreCase(String prenom);
    List<Employe> findByMatriculeContainingIgnoreCase(String matricule);
    List<Employe> findByProfilId(Long profilId);
}