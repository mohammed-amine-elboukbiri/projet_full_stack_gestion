package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.projet.ProjetRequestDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetResponseDto;
import ma.fst.projet_full_stack_2.entities.Projet;
import org.springframework.stereotype.Component;

@Component
public class ProjetMapper {

    public ProjetResponseDto toResponseDto(Projet projet) {
        if (projet == null) return null;

        return ProjetResponseDto.builder()
                .id(projet.getId())
                .code(projet.getCode())
                .nom(projet.getNom())
                .description(projet.getDescription())
                .dateDebut(projet.getDateDebut())
                .dateFin(projet.getDateFin())
                .montant(projet.getMontant())
                .organismeId(projet.getOrganisme() != null ? projet.getOrganisme().getId() : null)
                .organismeNom(projet.getOrganisme() != null ? projet.getOrganisme().getNom() : null)
                .chefProjetId(projet.getChefProjet() != null ? projet.getChefProjet().getId() : null)
                .chefProjetNomComplet(
                        projet.getChefProjet() != null
                                ? projet.getChefProjet().getNom() + " " + projet.getChefProjet().getPrenom()
                                : null
                )
                .build();
    }

    public void updateEntityFromDto(ProjetRequestDto dto, Projet projet) {
        projet.setCode(dto.getCode());
        projet.setNom(dto.getNom());
        projet.setDescription(dto.getDescription());
        projet.setDateDebut(dto.getDateDebut());
        projet.setDateFin(dto.getDateFin());
        projet.setMontant(dto.getMontant());
    }
}