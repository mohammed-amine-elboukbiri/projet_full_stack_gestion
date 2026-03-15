package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseEtatDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseRequestDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseResponseDto;

import java.util.List;

public interface PhaseService {
    PhaseResponseDto create(PhaseRequestDto dto);
    PhaseResponseDto update(Long id, PhaseRequestDto dto);
    PhaseResponseDto getById(Long id);
    List<PhaseResponseDto> getAll();
    void delete(Long id);
    PhaseResponseDto updateEtat(Long id, PhaseEtatDto dto);
    PageResponseDto<PhaseResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir);
}
