package ma.fst.projet_full_stack_2.service;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeRequestDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeResponseDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeSearchDto;

import java.util.List;

public interface EmployeService {
    EmployeResponseDto create(EmployeRequestDto dto);
    EmployeResponseDto update(Long id, EmployeRequestDto dto);
    EmployeResponseDto getById(Long id);
    List<EmployeResponseDto> getAll();
    void delete(Long id);
    List<EmployeResponseDto> search(EmployeSearchDto dto);
    PageResponseDto<EmployeResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir);
}
