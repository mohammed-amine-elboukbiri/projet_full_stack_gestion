package ma.fst.projet_full_stack_2.metier.impl;



import ma.fst.projet_full_stack_2.dto.profil.ProfilRequestDto;
import ma.fst.projet_full_stack_2.dto.profil.ProfilResponseDto;
import ma.fst.projet_full_stack_2.entities.Profil;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.mapper.ProfilMapper;
import ma.fst.projet_full_stack_2.repository.ProfilRepository;
import ma.fst.projet_full_stack_2.metier.ProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilServiceImpl implements ProfilService {

    private final ProfilRepository profilRepository;
    private final ProfilMapper profilMapper;

    @Override
    public ProfilResponseDto create(ProfilRequestDto dto) {
        profilRepository.findByCode(dto.getCode()).ifPresent(p -> {
            throw new BadRequestException("Un profil avec ce code existe déjà");
        });

        Profil profil = profilMapper.toEntity(dto);
        return profilMapper.toResponseDto(profilRepository.save(profil));
    }

    @Override
    public ProfilResponseDto update(Long id, ProfilRequestDto dto) {
        Profil profil = profilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profil introuvable avec l'id : " + id));

        profilRepository.findByCode(dto.getCode()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Un autre profil avec ce code existe déjà");
            }
        });

        profilMapper.updateEntityFromDto(dto, profil);
        return profilMapper.toResponseDto(profilRepository.save(profil));
    }

    @Override
    public ProfilResponseDto getById(Long id) {
        Profil profil = profilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profil introuvable avec l'id : " + id));

        return profilMapper.toResponseDto(profil);
    }

    @Override
    public List<ProfilResponseDto> getAll() {
        return profilRepository.findAll()
                .stream()
                .map(profilMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Profil profil = profilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profil introuvable avec l'id : " + id));

        profilRepository.delete(profil);
    }
}