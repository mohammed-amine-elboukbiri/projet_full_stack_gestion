package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeRequestDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeResponseDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeSearchDto;

import java.util.List;

public interface OrganismeService {
    OrganismeResponseDto create(OrganismeRequestDto dto);
    OrganismeResponseDto update(Long id, OrganismeRequestDto dto);
    OrganismeResponseDto getById(Long id);
    List<OrganismeResponseDto> getAll();
    void delete(Long id);
    List<OrganismeResponseDto> search(OrganismeSearchDto dto);
    PageResponseDto<OrganismeResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir);
}
