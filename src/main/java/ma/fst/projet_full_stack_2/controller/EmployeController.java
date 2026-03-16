package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeRequestDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeResponseDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeSearchDto;
import ma.fst.projet_full_stack_2.service.EmployeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public PageResponseDto<EmployeResponseDto> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return employeService.getAllPaginated(page, size, sortBy, sortDir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATEUR')")

    public EmployeResponseDto create(@Valid @RequestBody EmployeRequestDto dto) {
        return employeService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")

    public EmployeResponseDto update(@PathVariable Long id, @Valid @RequestBody EmployeRequestDto dto) {
        return employeService.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public EmployeResponseDto getById(@PathVariable Long id) {
        return employeService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")

    public List<EmployeResponseDto> getAll() {
        return employeService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeService.delete(id);
    }
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")

    public List<EmployeResponseDto> search(@RequestBody EmployeSearchDto dto) {
        return employeService.search(dto);
    }
}