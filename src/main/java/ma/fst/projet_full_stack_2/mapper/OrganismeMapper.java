package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.organisme.OrganismeRequestDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeResponseDto;
import ma.fst.projet_full_stack_2.entities.Organisme;
import org.springframework.stereotype.Component;

@Component
public class OrganismeMapper {

    public Organisme toEntity(OrganismeRequestDto dto) {
        if (dto == null) return null;

        return Organisme.builder()
                .code(dto.getCode())
                .nom(dto.getNom())
                .adresse(dto.getAdresse())
                .telephone(dto.getTelephone())
                .nomContact(dto.getNomContact())
                .emailContact(dto.getEmailContact())
                .siteWeb(dto.getSiteWeb())
                .build();
    }

    public OrganismeResponseDto toResponseDto(Organisme organisme) {
        if (organisme == null) return null;

        return OrganismeResponseDto.builder()
                .id(organisme.getId())
                .code(organisme.getCode())
                .nom(organisme.getNom())
                .adresse(organisme.getAdresse())
                .telephone(organisme.getTelephone())
                .nomContact(organisme.getNomContact())
                .emailContact(organisme.getEmailContact())
                .siteWeb(organisme.getSiteWeb())
                .build();
    }

    public void updateEntityFromDto(OrganismeRequestDto dto, Organisme organisme) {
        organisme.setCode(dto.getCode());
        organisme.setNom(dto.getNom());
        organisme.setAdresse(dto.getAdresse());
        organisme.setTelephone(dto.getTelephone());
        organisme.setNomContact(dto.getNomContact());
        organisme.setEmailContact(dto.getEmailContact());
        organisme.setSiteWeb(dto.getSiteWeb());
    }
}