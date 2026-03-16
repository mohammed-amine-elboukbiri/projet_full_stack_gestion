package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.facture.FactureRequestDto;
import ma.fst.projet_full_stack_2.dto.facture.FactureResponseDto;
import ma.fst.projet_full_stack_2.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public PageResponseDto<FactureResponseDto> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return factureService.getAllPaginated(page, size, sortBy, sortDir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COMPTABLE','ADMINISTRATEUR')")
    public FactureResponseDto create(@Valid @RequestBody FactureRequestDto dto) {
        return factureService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPTABLE','ADMINISTRATEUR')")
    public FactureResponseDto update(@PathVariable Long id, @Valid @RequestBody FactureRequestDto dto) {
        return factureService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public FactureResponseDto getById(@PathVariable Long id) {
        return factureService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public List<FactureResponseDto> getAll() {
        return factureService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('COMPTABLE','ADMINISTRATEUR')")
    public void delete(@PathVariable Long id) {
        factureService.delete(id);
    }
}