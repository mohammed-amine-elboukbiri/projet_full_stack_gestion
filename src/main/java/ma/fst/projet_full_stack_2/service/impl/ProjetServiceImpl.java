package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.config.SecurityUtils;
import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetRequestDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetResponseDto;
import ma.fst.projet_full_stack_2.dto.projet.ProjetSearchDto;
import ma.fst.projet_full_stack_2.entities.Employe;
import ma.fst.projet_full_stack_2.entities.Organisme;
import ma.fst.projet_full_stack_2.entities.Projet;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.exception.UnauthorizedActionException;
import ma.fst.projet_full_stack_2.mapper.ProjetMapper;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.repository.OrganismeRepository;
import ma.fst.projet_full_stack_2.repository.ProjetRepository;
import ma.fst.projet_full_stack_2.service.ProjetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetServiceImpl implements ProjetService {

    private final ProjetRepository projetRepository;
    private final OrganismeRepository organismeRepository;
    private final EmployeRepository employeRepository;
    private final ProjetMapper projetMapper;
    private final SecurityUtils securityUtils;

    @Override
    public ProjetResponseDto create(ProjetRequestDto dto) {
        validateDates(dto);
        checkUniqueCode(dto.getCode(), null);

        Organisme organisme = organismeRepository.findById(dto.getOrganismeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organisme introuvable avec l'id : " + dto.getOrganismeId()
                ));

        Employe chefProjet = employeRepository.findById(dto.getChefProjetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employé introuvable avec l'id : " + dto.getChefProjetId()
                ));

        Projet projet = Projet.builder()
                .code(dto.getCode())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .montant(dto.getMontant())
                .organisme(organisme)
                .chefProjet(chefProjet)
                .build();

        return projetMapper.toResponseDto(projetRepository.save(projet));
    }

    @Override
    public ProjetResponseDto update(Long id, ProjetRequestDto dto) {
        validateDates(dto);
        checkUniqueCode(dto.getCode(), id);

        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + id
                ));
        checkProjetModificationPermission(projet);
        Organisme organisme = organismeRepository.findById(dto.getOrganismeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organisme introuvable avec l'id : " + dto.getOrganismeId()
                ));

        Employe chefProjet = employeRepository.findById(dto.getChefProjetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employé introuvable avec l'id : " + dto.getChefProjetId()
                ));

        projetMapper.updateentitiesFromDto(dto, projet);
        projet.setOrganisme(organisme);
        projet.setChefProjet(chefProjet);

        return projetMapper.toResponseDto(projetRepository.save(projet));
    }

    @Override
    public ProjetResponseDto getById(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + id
                ));
        checkProjetReadPermission(projet);

        return projetMapper.toResponseDto(projet);
    }

    @Override
    public List<ProjetResponseDto> getAll() {
        return projetRepository.findAll()
                .stream()
                .map(projetMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + id
                ));
        checkProjetModificationPermission(projet);

        projetRepository.delete(projet);
    }



    private void validateDates(ProjetRequestDto dto) {
        if (dto.getDateDebut().isAfter(dto.getDateFin())) {
            throw new BadRequestException("La date de début ne peut pas être postérieure à la date de fin");
        }
    }

    private void checkUniqueCode(String code, Long currentId) {
        projetRepository.findByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Un projet avec ce code existe déjà");
            }
        });
    }
    @Override
    public List<ProjetResponseDto> search(ProjetSearchDto dto) {
        if (dto.getCode() != null && !dto.getCode().isBlank()) {
            return projetRepository.findByCodeContainingIgnoreCase(dto.getCode())
                    .stream()
                    .map(projetMapper::toResponseDto)
                    .toList();
        }

        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            return projetRepository.findByNomContainingIgnoreCase(dto.getNom())
                    .stream()
                    .map(projetMapper::toResponseDto)
                    .toList();
        }

        if (dto.getOrganismeId() != null) {
            return projetRepository.findByOrganismeId(dto.getOrganismeId())
                    .stream()
                    .map(projetMapper::toResponseDto)
                    .toList();
        }

        if (dto.getChefProjetId() != null) {
            return projetRepository.findByChefProjetId(dto.getChefProjetId())
                    .stream()
                    .map(projetMapper::toResponseDto)
                    .toList();
        }

        return projetRepository.findAll()
                .stream()
                .map(projetMapper::toResponseDto)
                .toList();
    }
    private void checkProjetModificationPermission(Projet projet) {
        if (securityUtils.hasRole("ADMIN") || securityUtils.hasRole("DIRECTEUR")) {
            return;
        }

        if (securityUtils.hasRole("CHEF_PROJET")) {
            String currentLogin = securityUtils.getCurrentLogin();

            if (currentLogin == null) {
                throw new UnauthorizedActionException("Utilisateur non authentifié");
            }

            if (projet.getChefProjet() == null || projet.getChefProjet().getLogin() == null) {
                throw new UnauthorizedActionException("Projet sans chef de projet valide");
            }

            if (!currentLogin.equals(projet.getChefProjet().getLogin())) {
                throw new UnauthorizedActionException(
                        "Vous ne pouvez modifier que les projets que vous dirigez"
                );
            }

            return;
        }

        throw new UnauthorizedActionException("Vous n'avez pas le droit de modifier ce projet");
    }
    private void checkProjetReadPermission(Projet projet) {
        if (securityUtils.hasRole("ADMIN")
                || securityUtils.hasRole("DIRECTEUR")
                || securityUtils.hasRole("COMPTABLE")
                || securityUtils.hasRole("SECRETAIRE")) {
            return;
        }

        if (securityUtils.hasRole("CHEF_PROJET")) {
            String currentLogin = securityUtils.getCurrentLogin();

            if (currentLogin != null
                    && projet.getChefProjet() != null
                    && currentLogin.equals(projet.getChefProjet().getLogin())) {
                return;
            }
        }

        throw new UnauthorizedActionException("Vous ne pouvez consulter que vos propres projets");
    }
    @Override
    public PageResponseDto<ProjetResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Projet> projetPage = projetRepository.findAll(pageable);

        return PageResponseDto.<ProjetResponseDto>builder()
                .content(projetPage.getContent().stream().map(projetMapper::toResponseDto).toList())
                .page(projetPage.getNumber())
                .size(projetPage.getSize())
                .totalElements(projetPage.getTotalElements())
                .totalPages(projetPage.getTotalPages())
                .last(projetPage.isLast())
                .build();
    }
}
