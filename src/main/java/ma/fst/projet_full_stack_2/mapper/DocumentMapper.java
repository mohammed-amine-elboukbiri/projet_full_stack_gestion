package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.document.DocumentResponseDto;
import ma.fst.projet_full_stack_2.entities.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponseDto toResponseDto(Document document) {
        if (document == null) return null;

        return DocumentResponseDto.builder()
                .id(document.getId())
                .code(document.getCode())
                .libelle(document.getLibelle())
                .description(document.getDescription())
                .chemin(document.getChemin())
                .projetId(document.getProjet() != null ? document.getProjet().getId() : null)
                .projetNom(document.getProjet() != null ? document.getProjet().getNom() : null)
                .build();
    }
}