package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.config.SecurityUtils;
import ma.fst.projet_full_stack_2.dto.affectation.AffectationRequestDto;
import ma.fst.projet_full_stack_2.dto.affectation.AffectationResponseDto;
import ma.fst.projet_full_stack_2.entities.*;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.exception.UnauthorizedActionException;
import ma.fst.projet_full_stack_2.mapper.AffectationMapper;
import ma.fst.projet_full_stack_2.repository.AffectationRepository;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.repository.PhaseRepository;
import ma.fst.projet_full_stack_2.service.AffectationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class git aAffectationServiceImpl implements AffectationService {

    private final AffectationRepository affectationRepository;
    private final EmployeRepository employeRepository;
    private final PhaseRepository phaseRepository;
    private final AffectationMapper affectationMapper;
    private final SecurityUtils securityUtils;

    @Override
    public AffectationResponseDto create(AffectationRequestDto dto) {
        validateDates(dto);

        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employé introuvable avec l'id : " + dto.getEmployeId()
                ));


        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + dto.getPhaseId()
                ));
        checkAffectationPermission(phase);

        AffectationId id = new AffectationId(dto.getEmployeId(), dto.getPhaseId());

        if (affectationRepository.existsById(id)) {
            throw new BadRequestException("Cette affectation existe déjà");
        }

        Affectation affectation = Affectation.builder()
                .id(id)
                .employe(employe)
                .phase(phase)
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .build();

        return affectationMapper.toResponseDto(affectationRepository.save(affectation));
    }

    @Override
    public AffectationResponseDto update(Long employeId, Long phaseId, AffectationRequestDto dto) {
        validateDates(dto);

        AffectationId id = new AffectationId(employeId, phaseId);

        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Affectation introuvable pour employeId=" + employeId + " et phaseId=" + phaseId
                ));
        checkAffectationPermission(affectation.getPhase());

        affectation.setDateDebut(dto.getDateDebut());
        affectation.setDateFin(dto.getDateFin());

        return affectationMapper.toResponseDto(affectationRepository.save(affectation));
    }

    @Override
    public AffectationResponseDto getById(Long employeId, Long phaseId) {
        AffectationId id = new AffectationId(employeId, phaseId);

        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Affectation introuvable pour employeId=" + employeId + " et phaseId=" + phaseId
                ));
        checkAffectationPermission(affectation.getPhase());

        return affectationMapper.toResponseDto(affectation);
    }

    @Override
    public List<AffectationResponseDto> getAll() {
        return affectationRepository.findAll()
                .stream()
                .map(affectationMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long employeId, Long phaseId) {
        AffectationId id = new AffectationId(employeId, phaseId);

        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Affectation introuvable pour employeId=" + employeId + " et phaseId=" + phaseId
                ));
        checkAffectationPermission(affectation.getPhase());

        affectationRepository.delete(affectation);
    }

    private void validateDates(AffectationRequestDto dto) {
        if (dto.getDateDebut().isAfter(dto.getDateFin())) {
            throw new BadRequestException("La date de début ne peut pas être postérieure à la date de fin");
        }
    }
    private void checkAffectationPermission(Phase phase) {

        if (securityUtils.hasRole("ADMIN") || securityUtils.hasRole("DIRECTEUR")) {
            return;
        }

        if (securityUtils.hasRole("CHEF_PROJET")) {

            String currentLogin = securityUtils.getCurrentLogin();

            if (currentLogin == null) {
                throw new UnauthorizedActionException("Utilisateur non authentifié");
            }

            if (phase.getProjet() == null ||
                    phase.getProjet().getChefProjet() == null ||
                    phase.getProjet().getChefProjet().getLogin() == null) {

                throw new UnauthorizedActionException("Projet invalide");
            }

            if (!currentLogin.equals(phase.getProjet().getChefProjet().getLogin())) {
                throw new UnauthorizedActionException(
                        "Vous ne pouvez affecter des employés que sur vos projets"
                );
            }

            return;
        }

        throw new UnauthorizedActionException("Accès non autorisé aux affectations");
    }
}
