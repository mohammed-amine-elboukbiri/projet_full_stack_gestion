package ma.fst.projet_full_stack_2.service.impl;

import ma.fst.projet_full_stack_2.config.SecurityUtils;
import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseEtatDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseRequestDto;
import ma.fst.projet_full_stack_2.dto.phase.PhaseResponseDto;
import ma.fst.projet_full_stack_2.entities.Phase;
import ma.fst.projet_full_stack_2.entities.Projet;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.exception.UnauthorizedActionException;
import ma.fst.projet_full_stack_2.mapper.PhaseMapper;
import ma.fst.projet_full_stack_2.repository.PhaseRepository;
import ma.fst.projet_full_stack_2.repository.ProjetRepository;
import ma.fst.projet_full_stack_2.service.PhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhaseServiceImpl implements PhaseService {

    private final PhaseRepository phaseRepository;
    private final ProjetRepository projetRepository;
    private final PhaseMapper phaseMapper;
    private final SecurityUtils securityUtils;

    @Override
    public PhaseResponseDto create(PhaseRequestDto dto) {
        validateDates(dto);
        checkUniqueCode(dto.getCode(), null);

        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + dto.getProjetId()
                ));
        checkProjetPermissionForCreation(projet);

        validateMontant(dto.getMontant(), projet.getMontant());

        Phase phase = Phase.builder()
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .description(dto.getDescription())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .montant(dto.getMontant())
                .etatRealisation(dto.getEtatRealisation() != null ? dto.getEtatRealisation() : false)
                .etatFacturation(dto.getEtatFacturation() != null ? dto.getEtatFacturation() : false)
                .etatPaiement(dto.getEtatPaiement() != null ? dto.getEtatPaiement() : false)
                .projet(projet)
                .build();

        return phaseMapper.toResponseDto(phaseRepository.save(phase));
    }

    @Override
    public PhaseResponseDto update(Long id, PhaseRequestDto dto) {
        validateDates(dto);
        checkUniqueCode(dto.getCode(), id);

        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + id
                ));
        checkPhasePermission(phase);
        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + dto.getProjetId()
                ));
        checkProjetPermissionForCreation(projet);


        validateMontant(dto.getMontant(), projet.getMontant());

        phaseMapper.updateEntityFromDto(dto, phase);
        phase.setProjet(projet);

        return phaseMapper.toResponseDto(phaseRepository.save(phase));
    }


    @Override
    public PhaseResponseDto getById(Long id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + id
                ));
        checkPhasePermission(phase);

        return phaseMapper.toResponseDto(phase);
    }

    @Override
    public List<PhaseResponseDto> getAll() {
        return phaseRepository.findAll()
                .stream()
                .map(phaseMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + id
                ));
        checkPhasePermission(phase);

        phaseRepository.delete(phase);
    }

    private void validateDates(PhaseRequestDto dto) {
        if (dto.getDateDebut().isAfter(dto.getDateFin())) {
            throw new BadRequestException("La date de début ne peut pas être postérieure à la date de fin");
        }
    }

    private void validateMontant(Double montantPhase, Double montantProjet) {
        if (montantPhase > montantProjet) {
            throw new BadRequestException("Le montant de la phase ne peut pas dépasser le montant du projet");
        }
    }

    private void checkUniqueCode(String code, Long currentId) {
        phaseRepository.findByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Une phase avec ce code existe déjà");
            }
        });
    }
    @Override
    public PhaseResponseDto updateEtat(Long id, PhaseEtatDto dto) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + id
                ));

        checkPhasePermission(phase);

        if (dto.getEtatRealisation() != null) {
            phase.setEtatRealisation(dto.getEtatRealisation());
        }

        if (dto.getEtatFacturation() != null) {
            if (Boolean.TRUE.equals(dto.getEtatFacturation()) && Boolean.FALSE.equals(phase.getEtatRealisation())) {
                throw new BadRequestException("Impossible de facturer une phase non terminée");
            }
            phase.setEtatFacturation(dto.getEtatFacturation());
        }

        if (dto.getEtatPaiement() != null) {
            if (Boolean.TRUE.equals(dto.getEtatPaiement()) && Boolean.FALSE.equals(phase.getEtatFacturation())) {
                throw new BadRequestException("Impossible de marquer comme payée une phase non facturée");
            }
            phase.setEtatPaiement(dto.getEtatPaiement());
        }

        return phaseMapper.toResponseDto(phaseRepository.save(phase));
    }
    private void checkProjetPermissionForCreation(Projet projet) {
        if (securityUtils.hasRole("ADMINISTRATEUR") || securityUtils.hasRole("DIRECTEUR")) {
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
                        "Vous ne pouvez créer des phases que pour vos propres projets"
                );
            }

            return;
        }

        throw new UnauthorizedActionException("Vous n'avez pas le droit de gérer cette phase");
    }

    private void checkPhasePermission(Phase phase) {
        if (securityUtils.hasRole("ADMINISTRATEUR") || securityUtils.hasRole("DIRECTEUR") || securityUtils.hasRole("COMPTABLE")) {
            return;
        }

        if (securityUtils.hasRole("CHEF_PROJET")) {
            String currentLogin = securityUtils.getCurrentLogin();

            if (currentLogin == null) {
                throw new UnauthorizedActionException("Utilisateur non authentifié");
            }

            if (phase.getProjet() == null
                    || phase.getProjet().getChefProjet() == null
                    || phase.getProjet().getChefProjet().getLogin() == null) {
                throw new UnauthorizedActionException("Phase liée à un projet invalide");
            }

            if (!currentLogin.equals(phase.getProjet().getChefProjet().getLogin())) {
                throw new UnauthorizedActionException(
                        "Vous ne pouvez gérer que les phases de vos propres projets"
                );
            }

            return;
        }

        throw new UnauthorizedActionException("Vous n'avez pas le droit d'accéder à cette phase");
    }
    @Override
    public PageResponseDto<PhaseResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Phase> phasePage = phaseRepository.findAll(pageable);

        return PageResponseDto.<PhaseResponseDto>builder()
                .content(phasePage.getContent().stream().map(phaseMapper::toResponseDto).toList())
                .page(phasePage.getNumber())
                .size(phasePage.getSize())
                .totalElements(phasePage.getTotalElements())
                .totalPages(phasePage.getTotalPages())
                .last(phasePage.isLast())
                .build();
    }
}
