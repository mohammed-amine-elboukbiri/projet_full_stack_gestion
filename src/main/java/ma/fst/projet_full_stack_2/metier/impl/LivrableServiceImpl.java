package ma.fst.projet_full_stack_2.metier.impl;



import ma.fst.projet_full_stack_2.config.SecurityUtils;
import ma.fst.projet_full_stack_2.dto.livrable.LivrableRequestDto;
import ma.fst.projet_full_stack_2.dto.livrable.LivrableResponseDto;
import ma.fst.projet_full_stack_2.entities.Livrable;
import ma.fst.projet_full_stack_2.entities.Phase;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.exception.UnauthorizedActionException;
import ma.fst.projet_full_stack_2.mapper.LivrableMapper;
import ma.fst.projet_full_stack_2.repository.LivrableRepository;
import ma.fst.projet_full_stack_2.repository.PhaseRepository;
import ma.fst.projet_full_stack_2.metier.LivrableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivrableServiceImpl implements LivrableService {

    private final LivrableRepository livrableRepository;
    private final PhaseRepository phaseRepository;
    private final LivrableMapper livrableMapper;
    private final SecurityUtils securityUtils;

    @Override
    public LivrableResponseDto create(LivrableRequestDto dto) {
        checkUniqueCode(dto.getCode(), null);

        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + dto.getPhaseId()
                ));
        checkLivrablePermission(phase);

        Livrable livrable = Livrable.builder()
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .description(dto.getDescription())
                .chemin(dto.getChemin())
                .phase(phase)
                .build();

        return livrableMapper.toResponseDto(livrableRepository.save(livrable));
    }

    @Override
    public LivrableResponseDto update(Long id, LivrableRequestDto dto) {
        checkUniqueCode(dto.getCode(), id);

        Livrable livrable = livrableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Livrable introuvable avec l'id : " + id
                ));
        checkLivrablePermission(livrable.getPhase());

        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + dto.getPhaseId()
                ));

        livrable.setCode(dto.getCode());
        livrable.setLibelle(dto.getLibelle());
        livrable.setDescription(dto.getDescription());
        livrable.setChemin(dto.getChemin());
        livrable.setPhase(phase);

        return livrableMapper.toResponseDto(livrableRepository.save(livrable));
    }

    @Override
    public LivrableResponseDto getById(Long id) {
        Livrable livrable = livrableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Livrable introuvable avec l'id : " + id
                ));
        checkLivrablePermission(livrable.getPhase());

        return livrableMapper.toResponseDto(livrable);
    }

    @Override
    public List<LivrableResponseDto> getAll() {
        return livrableRepository.findAll()
                .stream()
                .map(livrableMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Livrable livrable = livrableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Livrable introuvable avec l'id : " + id
                ));
        checkLivrablePermission(livrable.getPhase());

        livrableRepository.delete(livrable);
    }

    private void checkUniqueCode(String code, Long currentId) {
        livrableRepository.findByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Un livrable avec ce code existe déjà");
            }
        });
    }
    private void checkLivrablePermission(Phase phase) {

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

                throw new UnauthorizedActionException("Projet ou chef de projet invalide");
            }

            if (!currentLogin.equals(phase.getProjet().getChefProjet().getLogin())) {
                throw new UnauthorizedActionException(
                        "Vous ne pouvez gérer que les livrables de vos projets"
                );
            }

            return;
        }

        throw new UnauthorizedActionException("Accès non autorisé aux livrables");
    }
}
