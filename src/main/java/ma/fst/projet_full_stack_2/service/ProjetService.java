package ma.fst.projet_full_stack_2.service;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetRequestDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetResponseDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetSearchDto;

import java.util.List;

public interface ProjetService {
    ProjetResponseDto create(ProjetRequestDto dto);
    ProjetResponseDto update(Long id, ProjetRequestDto dto);
    ProjetResponseDto getById(Long id);
    List<ProjetResponseDto> getAll();
    void delete(Long id);
    List<ProjetResponseDto> search(ProjetSearchDto dto);
    PageResponseDto<ProjetResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir);
}
