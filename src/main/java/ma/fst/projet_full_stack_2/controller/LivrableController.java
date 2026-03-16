package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.livrable.LivrableRequestDto;
import ma.fst.projet_full_stack_2.dto.livrable.LivrableResponseDto;
import ma.fst.projet_full_stack_2.service.LivrableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livrables")
@RequiredArgsConstructor
public class LivrableController {

    private final LivrableService livrableService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public LivrableResponseDto create(@Valid @RequestBody LivrableRequestDto dto) {
        return livrableService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public LivrableResponseDto update(@PathVariable Long id, @Valid @RequestBody LivrableRequestDto dto) {
        return livrableService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public LivrableResponseDto getById(@PathVariable Long id) {
        return livrableService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public List<LivrableResponseDto> getAll() {
        return livrableService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CHEF_PROJET','DIRECTEUR','ADMINISTRATEUR')")
    public void delete(@PathVariable Long id) {
        livrableService.delete(id);
    }
}