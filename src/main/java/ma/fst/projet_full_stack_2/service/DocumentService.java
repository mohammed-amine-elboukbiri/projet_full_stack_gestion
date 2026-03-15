package ma.fst.projet_full_stack_2.service;

import ma.fst.projet_full_stack_2.dto.document.DocumentRequestDto;
import ma.fst.projet_full_stack_2.dto.document.DocumentResponseDto;

import java.util.List;

public interface DocumentService {
    DocumentResponseDto create(DocumentRequestDto dto);
    DocumentResponseDto update(Long id, DocumentRequestDto dto);
    DocumentResponseDto getById(Long id);
    List<DocumentResponseDto> getAll();
    void delete(Long id);
}
