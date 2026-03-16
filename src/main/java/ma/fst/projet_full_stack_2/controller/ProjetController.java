package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.projet.ProjetRequestDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetResponseDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetSearchDto;
import ma.fst.projet_full_stack_2.service.ProjetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','CHEF_PROJET','COMPTABLE','ADMINISTRATEUR')")
    public PageResponseDto<ProjetResponseDto> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return projetService.getAllPaginated(page, size, sortBy, sortDir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','ADMINISTRATEUR')")

    public ProjetResponseDto create(@Valid @RequestBody ProjetRequestDto dto) {
        return projetService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','CHEF_PROJET','ADMINISTRATEUR')")

    public ProjetResponseDto update(@PathVariable Long id, @Valid @RequestBody ProjetRequestDto dto) {
        return projetService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','CHEF_PROJET','COMPTABLE','ADMINISTRATEUR')")

    public ProjetResponseDto getById(@PathVariable Long id) {
        return projetService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','CHEF_PROJET','COMPTABLE','ADMINISTRATEUR')")

    public List<ProjetResponseDto> getAll() {
        return projetService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")

    public void delete(@PathVariable Long id) {
        projetService.delete(id);
    }
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('SECRETAIRE','DIRECTEUR','CHEF_PROJET','COMPTABLE','ADMINISTRATEUR')")

    public List<ProjetResponseDto> search(@RequestBody ProjetSearchDto dto) {
        return projetService.search(dto);
    }
}