package ma.fst.projet_full_stack_2.service;

import ma.fst.projet_full_stack_2.dto.profil.ProfilRequestDto;
import ma.fst.projet_full_stack_2.dto.profil.ProfilResponseDto;

import java.util.List;

public interface ProfilService {
    ProfilResponseDto create(ProfilRequestDto dto);
    ProfilResponseDto update(Long id, ProfilRequestDto dto);
    ProfilResponseDto getById(Long id);
    List<ProfilResponseDto> getAll();
    void delete(Long id);
}
