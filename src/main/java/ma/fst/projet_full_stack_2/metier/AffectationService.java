package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.dto.affectation.AffectationRequestDto;
import ma.fst.projet_full_stack_2.dto.affectation.AffectationResponseDto;

import java.util.List;

public interface AffectationService {
    AffectationResponseDto create(AffectationRequestDto dto);
    AffectationResponseDto update(Long employeId, Long phaseId, AffectationRequestDto dto);
    AffectationResponseDto getById(Long employeId, Long phaseId);
    List<AffectationResponseDto> getAll();
    void delete(Long employeId, Long phaseId);
}