package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.phase.PhaseRequestDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseResponseDto;
import ma.fst.projet_full_stack_2.entities.Phase;
import org.springframework.stereotype.Component;

@Component
public class PhaseMapper {

    public PhaseResponseDto toResponseDto(Phase phase) {
        if (phase == null) return null;

        return PhaseResponseDto.builder()
                .id(phase.getId())
                .code(phase.getCode())
                .libelle(phase.getLibelle())
                .description(phase.getDescription())
                .dateDebut(phase.getDateDebut())
                .dateFin(phase.getDateFin())
                .montant(phase.getMontant())
                .etatRealisation(phase.getEtatRealisation())
                .etatFacturation(phase.getEtatFacturation())
                .etatPaiement(phase.getEtatPaiement())
                .projetId(phase.getProjet() != null ? phase.getProjet().getId() : null)
                .projetNom(phase.getProjet() != null ? phase.getProjet().getNom() : null)
                .build();
    }

    public void updateEntityFromDto(PhaseRequestDto dto, Phase phase) {
        phase.setCode(dto.getCode());
        phase.setLibelle(dto.getLibelle());
        phase.setDescription(dto.getDescription());
        phase.setDateDebut(dto.getDateDebut());
        phase.setDateFin(dto.getDateFin());
        phase.setMontant(dto.getMontant());
        phase.setEtatRealisation(dto.getEtatRealisation());
        phase.setEtatFacturation(dto.getEtatFacturation());
        phase.setEtatPaiement(dto.getEtatPaiement());
    }
}