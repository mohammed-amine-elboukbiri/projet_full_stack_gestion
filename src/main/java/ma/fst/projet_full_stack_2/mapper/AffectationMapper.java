package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.affectation.AffectationResponseDto;
import ma.fst.projet_full_stack_2.entities.Affectation;
import org.springframework.stereotype.Component;

@Component
public class AffectationMapper {

    public AffectationResponseDto toResponseDto(Affectation affectation) {
        if (affectation == null) return null;

        return AffectationResponseDto.builder()
                .employeId(affectation.getEmploye() != null ? affectation.getEmploye().getId() : null)
                .employeNomComplet(
                        affectation.getEmploye() != null
                                ? affectation.getEmploye().getNom() + " " + affectation.getEmploye().getPrenom()
                                : null
                )
                .phaseId(affectation.getPhase() != null ? affectation.getPhase().getId() : null)
                .phaseLibelle(affectation.getPhase() != null ? affectation.getPhase().getLibelle() : null)
                .dateDebut(affectation.getDateDebut())
                .dateFin(affectation.getDateFin())
                .build();
    }
}