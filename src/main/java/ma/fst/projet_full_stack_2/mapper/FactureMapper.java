package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.facture.FactureResponseDto;
import ma.fst.projet_full_stack_2.entities.Facture;
import org.springframework.stereotype.Component;

@Component
public class FactureMapper {

    public FactureResponseDto toResponseDto(Facture facture) {
        if (facture == null) return null;

        return FactureResponseDto.builder()
                .id(facture.getId())
                .code(facture.getCode())
                .dateFacture(facture.getDateFacture())
                .phaseId(facture.getPhase() != null ? facture.getPhase().getId() : null)
                .phaseLibelle(facture.getPhase() != null ? facture.getPhase().getLibelle() : null)
                .etatRealisation(facture.getPhase() != null ? facture.getPhase().getEtatRealisation() : null)
                .etatFacturation(facture.getPhase() != null ? facture.getPhase().getEtatFacturation() : null)
                .etatPaiement(facture.getPhase() != null ? facture.getPhase().getEtatPaiement() : null)
                .build();
    }
}