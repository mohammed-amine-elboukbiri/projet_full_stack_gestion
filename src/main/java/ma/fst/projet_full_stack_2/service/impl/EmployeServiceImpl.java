package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeRequestDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeResponseDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeSearchDto;
import ma.fst.projet_full_stack_2.entities.Employe;
import ma.fst.projet_full_stack_2.entities.Profil;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.mapper.EmployeMapper;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.repository.ProfilRepository;
import ma.fst.projet_full_stack_2.service.EmployeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements EmployeService {

    private final EmployeRepository employeRepository;
    private final ProfilRepository profilRepository;
    private final EmployeMapper employeMapper;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Override
    public EmployeResponseDto create(EmployeRequestDto dto) {
        checkUniqueFields(dto, null);

        Profil profil = profilRepository.findById(dto.getProfilId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profil introuvable avec l'id : " + dto.getProfilId()
                ));

        Employe employe = Employe.builder()
                .matricule(dto.getMatricule())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profil(profil)
                .build();

        employe = employeRepository.save(employe);

        return employeMapper.toResponseDto(employe);
    }

    @Override
    public EmployeResponseDto update(Long id, EmployeRequestDto dto) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id : " + id));

        checkUniqueFields(dto, id);

        Profil profil = profilRepository.findById(dto.getProfilId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil introuvable avec l'id : " + dto.getProfilId()));

        employeMapper.updateEntityFromDto(dto, employe);
        employe.setProfil(profil);
        employe.setPassword(passwordEncoder.encode(dto.getPassword()));

        return employeMapper.toResponseDto(employeRepository.save(employe));
    }

    @Override
    public EmployeResponseDto getById(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id : " + id));

        return employeMapper.toResponseDto(employe);
    }

    @Override
    public List<EmployeResponseDto> getAll() {
        return employeRepository.findAll()
                .stream()
                .map(employeMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id : " + id));

        employeRepository.delete(employe);
    }

    private void checkUniqueFields(EmployeRequestDto dto, Long currentId) {
        employeRepository.findByMatricule(dto.getMatricule()).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Un employé avec ce matricule existe déjà");
            }
        });

        employeRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Un employé avec cet email existe déjà");
            }
        });

        employeRepository.findByLogin(dto.getLogin()).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Un employé avec ce login existe déjà");
            }
        });
    }
    @Override
    public List<EmployeResponseDto> search(EmployeSearchDto dto) {
        if (dto.getMatricule() != null && !dto.getMatricule().isBlank()) {
            return employeRepository.findByMatriculeContainingIgnoreCase(dto.getMatricule())
                    .stream()
                    .map(employeMapper::toResponseDto)
                    .toList();
        }

        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            return employeRepository.findByNomContainingIgnoreCase(dto.getNom())
                    .stream()
                    .map(employeMapper::toResponseDto)
                    .toList();
        }

        if (dto.getPrenom() != null && !dto.getPrenom().isBlank()) {
            return employeRepository.findByPrenomContainingIgnoreCase(dto.getPrenom())
                    .stream()
                    .map(employeMapper::toResponseDto)
                    .toList();
        }

        if (dto.getProfilId() != null) {
            return employeRepository.findByProfilId(dto.getProfilId())
                    .stream()
                    .map(employeMapper::toResponseDto)
                    .toList();
        }

        return employeRepository.findAll()
                .stream()
                .map(employeMapper::toResponseDto)
                .toList();
    }
    @Override
    public PageResponseDto<EmployeResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employe> employePage = employeRepository.findAll(pageable);

        return PageResponseDto.<EmployeResponseDto>builder()
                .content(employePage.getContent().stream().map(employeMapper::toResponseDto).toList())
                .page(employePage.getNumber())
                .size(employePage.getSize())
                .totalElements(employePage.getTotalElements())
                .totalPages(employePage.getTotalPages())
                .last(employePage.isLast())
                .build();
    }
}
