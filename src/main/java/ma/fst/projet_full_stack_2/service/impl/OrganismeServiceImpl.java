package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.dto.common.PageResponseDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeRequestDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeResponseDto;
import ma.fst.projet_full_stack_2.dto.organisme.OrganismeSearchDto;
import ma.fst.projet_full_stack_2.entities.Organisme;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.mapper.OrganismeMapper;
import ma.fst.projet_full_stack_2.repository.OrganismeRepository;
import ma.fst.projet_full_stack_2.service.OrganismeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganismeServiceImpl implements OrganismeService {

    private final OrganismeRepository organismeRepository;
    private final OrganismeMapper organismeMapper;

    @Override
    public OrganismeResponseDto create(OrganismeRequestDto dto) {
        organismeRepository.findByCode(dto.getCode()).ifPresent(o -> {
            throw new BadRequestException("Un organisme avec ce code existe déjà");
        });

        Organisme organisme = organismeMapper.toEntity(dto);
        return organismeMapper.toResponseDto(organismeRepository.save(organisme));
    }

    @Override
    public OrganismeResponseDto update(Long id, OrganismeRequestDto dto) {
        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisme introuvable avec l'id : " + id));

        organismeRepository.findByCode(dto.getCode()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Un autre organisme avec ce code existe déjà");
            }
        });

        organismeMapper.updateEntityFromDto(dto, organisme);
        return organismeMapper.toResponseDto(organismeRepository.save(organisme));
    }

    @Override
    public OrganismeResponseDto getById(Long id) {
        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisme introuvable avec l'id : " + id));

        return organismeMapper.toResponseDto(organisme);
    }

    @Override
    public List<OrganismeResponseDto> getAll() {
        return organismeRepository.findAll()
                .stream()
                .map(organismeMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organisme introuvable avec l'id : " + id));

        organismeRepository.delete(organisme);
    }
    @Override
    public List<OrganismeResponseDto> search(OrganismeSearchDto dto) {
        if (dto.getCode() != null && !dto.getCode().isBlank()) {
            return organismeRepository.findByCodeContainingIgnoreCase(dto.getCode())
                    .stream()
                    .map(organismeMapper::toResponseDto)
                    .toList();
        }

        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            return organismeRepository.findByNomContainingIgnoreCase(dto.getNom())
                    .stream()
                    .map(organismeMapper::toResponseDto)
                    .toList();
        }

        if (dto.getNomContact() != null && !dto.getNomContact().isBlank()) {
            return organismeRepository.findByNomContactContainingIgnoreCase(dto.getNomContact())
                    .stream()
                    .map(organismeMapper::toResponseDto)
                    .toList();
        }

        return organismeRepository.findAll()
                .stream()
                .map(organismeMapper::toResponseDto)
                .toList();
    }
    @Override
    public PageResponseDto<OrganismeResponseDto> getAllPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Organisme> organismePage = organismeRepository.findAll(pageable);

        return PageResponseDto.<OrganismeResponseDto>builder()
                .content(organismePage.getContent().stream().map(organismeMapper::toResponseDto).toList())
                .page(organismePage.getNumber())
                .size(organismePage.getSize())
                .totalElements(organismePage.getTotalElements())
                .totalPages(organismePage.getTotalPages())
                .last(organismePage.isLast())
                .build();
    }
}
