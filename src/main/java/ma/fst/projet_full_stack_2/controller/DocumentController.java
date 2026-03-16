package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.document.DocumentRequestDto;
import ma.fst.projet_full_stack_2.dto.document.DocumentResponseDto;
import ma.fst.projet_full_stack_2.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SECRETAIRE','CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public DocumentResponseDto create(@Valid @RequestBody DocumentRequestDto dto) {
        return documentService.create(dto);
    }

    @PutMapping("/{id}")
    public DocumentResponseDto update(@PathVariable Long id, @Valid @RequestBody DocumentRequestDto dto) {
        return documentService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','CHEF_PROJET','DIRECTEUR','COMPTABLE','ADMINISTRATEUR')")
    public DocumentResponseDto getById(@PathVariable Long id) {
        return documentService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SECRETAIRE','CHEF_PROJET','DIRECTEUR','COMPTABLE','ADMINISTRATEUR')")
    public List<DocumentResponseDto> getAll() {
        return documentService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SECRETAIRE','CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public void delete(@PathVariable Long id) {
        documentService.delete(id);
    }
}