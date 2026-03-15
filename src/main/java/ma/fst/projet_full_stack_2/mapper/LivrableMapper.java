package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.livrable.LivrableResponseDto;
import ma.fst.projet_full_stack_2.entities.Livrable;
import org.springframework.stereotype.Component;

@Component
public class LivrableMapper {

    public LivrableResponseDto toResponseDto(Livrable livrable) {
        if (livrable == null) return null;

        return LivrableResponseDto.builder()
                .id(livrable.getId())
                .code(livrable.getCode())
                .libelle(livrable.getLibelle())
                .description(livrable.getDescription())
                .chemin(livrable.getChemin())
                .phaseId(livrable.getPhase() != null ? livrable.getPhase().getId() : null)
                .phaseLibelle(livrable.getPhase() != null ? livrable.getPhase().getLibelle() : null)
                .build();
    }
}