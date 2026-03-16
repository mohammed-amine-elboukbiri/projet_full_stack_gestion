package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeRequestDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeResponseDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeSearchDto;
import ma.fst.projet_full_stack_2.service.OrganismeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organismes")
@RequiredArgsConstructor
public class OrganismeController {

    private final OrganismeService organismeService;

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")
    public PageResponseDto<OrganismeResponseDto> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return organismeService.getAllPaginated(page, size, sortBy, sortDir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SECRETAIRE','ADMINISTRATEUR')")
    public OrganismeResponseDto create(@Valid @RequestBody OrganismeRequestDto dto) {
        return organismeService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")
    public OrganismeResponseDto update(@PathVariable Long id, @Valid @RequestBody OrganismeRequestDto dto) {
        return organismeService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")
    public OrganismeResponseDto getById(@PathVariable Long id) {
        return organismeService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")

    public List<OrganismeResponseDto> getAll() {
        return organismeService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        organismeService.delete(id);
    }
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")
    public List<OrganismeResponseDto> search(@RequestBody OrganismeSearchDto dto) {
        return organismeService.search(dto);
    }
}