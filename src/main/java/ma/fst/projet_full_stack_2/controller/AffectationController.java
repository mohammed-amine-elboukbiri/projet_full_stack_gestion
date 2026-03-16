package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.affectation.AffectationRequestDto;
import ma.fst.projet_full_stack_2.dto.affectation.AffectationResponseDto;
import ma.fst.projet_full_stack_2.service.AffectationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CHEF_PROJET','ADMINISTRATEUR')")
    public AffectationResponseDto create(@Valid @RequestBody AffectationRequestDto dto) {
        return affectationService.create(dto);
    }

    @PutMapping("/{employeId}/{phaseId}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','ADMINISTRATEUR')")
    public AffectationResponseDto update(
            @PathVariable Long employeId,
            @PathVariable Long phaseId,
            @Valid @RequestBody AffectationRequestDto dto
    ) {
        return affectationService.update(employeId, phaseId, dto);
    }

    @GetMapping("/{employeId}/{phaseId}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','ADMINISTRATEUR')")
    public AffectationResponseDto getById(
            @PathVariable Long employeId,
            @PathVariable Long phaseId
    ) {
        return affectationService.getById(employeId, phaseId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CHEF_PROJET','ADMINISTRATEUR')")
    public List<AffectationResponseDto> getAll() {
        return affectationService.getAll();
    }

    @DeleteMapping("/{employeId}/{phaseId}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','ADMINISTRATEUR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long employeId,
            @PathVariable Long phaseId
    ) {
        affectationService.delete(employeId, phaseId);
    }
}