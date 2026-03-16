package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseEtatDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseRequestDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseResponseDto;
import ma.fst.projet_full_stack_2.service.PhaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phases")
@RequiredArgsConstructor
public class PhaseController {

    private final PhaseService phaseService;

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public PageResponseDto<PhaseResponseDto> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return phaseService.getAllPaginated(page, size, sortBy, sortDir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CHEF_PROJET','ADMINISTRATEUR')")

    public PhaseResponseDto create(@Valid @RequestBody PhaseRequestDto dto) {
        return phaseService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")

    public PhaseResponseDto update(@PathVariable Long id, @Valid @RequestBody PhaseRequestDto dto) {
        return phaseService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")

    public PhaseResponseDto getById(@PathVariable Long id) {
        return phaseService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CHEF_PROJET','COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")

    public List<PhaseResponseDto> getAll() {
        return phaseService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")

    public void delete(@PathVariable Long id) {
        phaseService.delete(id);
    }

    @PatchMapping("/{id}/etat")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")

    public PhaseResponseDto updateEtat(@PathVariable Long id, @RequestBody PhaseEtatDto dto) {
        return phaseService.updateEtat(id, dto);
    }
}