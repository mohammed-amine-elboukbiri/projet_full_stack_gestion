package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.profil.ProfilRequestDto;
import ma.fst.projet_full_stack_2.dto.profil.ProfilResponseDto;
import ma.fst.projet_full_stack_2.service.ProfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profils")
@RequiredArgsConstructor
public class ProfilController {

    private final ProfilService profilService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ProfilResponseDto create(@Valid @RequestBody ProfilRequestDto dto) {
        return profilService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ProfilResponseDto update(@PathVariable Long id, @Valid @RequestBody ProfilRequestDto dto) {
        return profilService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ProfilResponseDto getById(@PathVariable Long id) {
        return profilService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public List<ProfilResponseDto> getAll() {
        return profilService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public void delete(@PathVariable Long id) {
        profilService.delete(id);
    }
}