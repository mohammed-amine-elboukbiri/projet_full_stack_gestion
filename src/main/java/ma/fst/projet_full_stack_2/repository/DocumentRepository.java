package ma.fst.projet_full_stack_2.repository;

import ma.fst.projet_full_stack_2.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByCode(String code);
}