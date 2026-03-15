package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.dto.livrable.LivrableRequestDto;
import ma.fst.projet_full_stack_2.dto.livrable.LivrableResponseDto;

import java.util.List;

public interface LivrableService {
    LivrableResponseDto create(LivrableRequestDto dto);
    LivrableResponseDto update(Long id, LivrableRequestDto dto);
    LivrableResponseDto getById(Long id);
    List<LivrableResponseDto> getAll();
    void delete(Long id);
}
