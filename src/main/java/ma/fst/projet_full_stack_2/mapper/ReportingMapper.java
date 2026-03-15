package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.reporting.PhaseReportingDto;
import ma.fst.projet_full_stack_2.entities.Phase;
import org.springframework.stereotype.Component;

@Component
public class ReportingMapper {

    public PhaseReportingDto toDto(Phase phase) {
        if (phase == null) return null;

        return PhaseReportingDto.builder()
                .id(phase.getId())
                .code(phase.getCode())
                .libelle(phase.getLibelle())
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
}