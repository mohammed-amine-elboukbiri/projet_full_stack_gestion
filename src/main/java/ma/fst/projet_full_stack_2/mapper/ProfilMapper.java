package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.profil.ProfilRequestDto;
import ma.fst.projet_full_stack_2.dto.profil.ProfilResponseDto;
import ma.fst.projet_full_stack_2.entities.Profil;
import org.springframework.stereotype.Component;

@Component
public class ProfilMapper {

    public Profil toEntity(ProfilRequestDto dto) {
        if (dto == null) return null;

        return Profil.builder()
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .build();
    }

    public ProfilResponseDto toResponseDto(Profil profil) {
        if (profil == null) return null;

        return ProfilResponseDto.builder()
                .id(profil.getId())
                .code(profil.getCode())
                .libelle(profil.getLibelle())
                .build();
    }

    public void updateEntityFromDto(ProfilRequestDto dto, Profil profil) {
        profil.setCode(dto.getCode());
        profil.setLibelle(dto.getLibelle());
    }
}