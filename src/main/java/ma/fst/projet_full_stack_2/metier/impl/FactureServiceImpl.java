package ma.fst.projet_full_stack_2.metier.impl;



import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.facture.FactureRequestDto;
import ma.fst.projet_full_stack_2.dto.facture.FactureResponseDto;
import ma.fst.projet_full_stack_2.entities.Facture;
import ma.fst.projet_full_stack_2.entities.Phase;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.mapper.FactureMapper;
import ma.fst.projet_full_stack_2.repository.FactureRepository;
import ma.fst.projet_full_stack_2.repository.PhaseRepository;
import ma.fst.projet_full_stack_2.metier.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final PhaseRepository phaseRepository;
    private final FactureMapper factureMapper;

    @Override
    public FactureResponseDto create(FactureRequestDto dto) {
        checkUniqueCode(dto.getCode(), null);

        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + dto.getPhaseId()
                ));

        if (Boolean.FALSE.equals(phase.getEtatRealisation())) {
            throw new BadRequestException("Impossible de facturer une phase non terminée");
        }

        if (Boolean.TRUE.equals(phase.getEtatFacturation())) {
            throw new BadRequestException("Cette phase est déjà facturée");
        }

        Facture facture = Facture.builder()
                .code(dto.getCode())
                .dateFacture(dto.getDateFacture())
                .phase(phase)
                .build();

        phase.setEtatFacturation(true);
        phaseRepository.save(phase);

        return factureMapper.toResponseDto(factureRepository.save(facture));
    }

    @Override
    public FactureResponseDto update(Long id, FactureRequestDto dto) {
        checkUniqueCode(dto.getCode(), id);

        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facture introuvable avec l'id : " + id
                ));

        Phase phase = phaseRepository.findById(dto.getPhaseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phase introuvable avec l'id : " + dto.getPhaseId()
                ));

        if (Boolean.FALSE.equals(phase.getEtatRealisation())) {
            throw new BadRequestException("Impossible d'associer une facture à une phase non terminée");
        }

        facture.setCode(dto.getCode());
        facture.setDateFacture(dto.getDateFacture());
        facture.setPhase(phase);

        phase.setEtatFacturation(true);
        phaseRepository.save(phase);

        return factureMapper.toResponseDto(factureRepository.save(facture));
    }

    @Override
    public FactureResponseDto getById(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facture introuvable avec l'id : " + id
                ));

        return factureMapper.toResponseDto(facture);
    }

    @Override
    public List<FactureResponseDto> getAll() {
        return factureRepository.findAll()
                .stream()
                .map(factureMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facture introuvable avec l'id : " + id
                ));

        Phase phase = facture.getPhase();
        if (phase != null) {
            phase.setEtatFacturation(false);
            phaseRepository.save(phase);
        }

        factureRepository.delete(facture);
    }

    private void checkUniqueCode(String code, Long currentId) {
        factureRepository.findByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Une facture avec ce code existe déjà");
            }
        });
    }
    @Override
    public PageResponseDto<FactureResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Facture> facturePage = factureRepository.findAll(pageable);

        return PageResponseDto.<FactureResponseDto>builder()
                .content(
                        facturePage.getContent()
                                .stream()
                                .map(factureMapper::toResponseDto)
                                .toList()
                )
                .page(facturePage.getNumber())
                .size(facturePage.getSize())
                .totalElements(facturePage.getTotalElements())
                .totalPages(facturePage.getTotalPages())
                .last(facturePage.isLast())
                .build();
    }
}
