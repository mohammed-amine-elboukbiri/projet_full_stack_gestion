package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.entities.Profil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilRepository extends JpaRepository<Profil, Long> {
    Optional<Profil> findByCode(String code);
}