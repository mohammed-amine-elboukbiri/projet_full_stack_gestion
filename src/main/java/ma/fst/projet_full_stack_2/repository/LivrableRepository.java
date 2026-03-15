package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Livrable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivrableRepository extends JpaRepository<Livrable, Long> {
    Optional<Livrable> findByCode(String code);
}