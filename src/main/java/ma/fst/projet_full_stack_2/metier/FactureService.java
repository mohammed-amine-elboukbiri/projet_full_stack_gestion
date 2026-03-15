package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.facture.FactureRequestDto;
import ma.fst.projet_full_stack_2.dto.facture.FactureResponseDto;

import java.util.List;

public interface FactureService {
    FactureResponseDto create(FactureRequestDto dto);
    FactureResponseDto update(Long id, FactureRequestDto dto);
    FactureResponseDto getById(Long id);
    List<FactureResponseDto> getAll();
    void delete(Long id);
    PageResponseDto<FactureResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir);
}
