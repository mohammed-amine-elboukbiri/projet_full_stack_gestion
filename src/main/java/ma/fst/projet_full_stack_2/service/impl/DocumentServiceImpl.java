package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.dto.document.DocumentRequestDto;
import ma.fst.projet_full_stack_2.dto.document.DocumentResponseDto;
import ma.fst.projet_full_stack_2.entities.Document;
import ma.fst.projet_full_stack_2.entities.Projet;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.mapper.DocumentMapper;
import ma.fst.projet_full_stack_2.repository.DocumentRepository;
import ma.fst.projet_full_stack_2.repository.ProjetRepository;
import ma.fst.projet_full_stack_2.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjetRepository projetRepository;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentResponseDto create(DocumentRequestDto dto) {
        checkUniqueCode(dto.getCode(), null);

        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + dto.getProjetId()
                ));

        Document document = Document.builder()
                .code(dto.getCode())
                .libelle(dto.getLibelle())
                .description(dto.getDescription())
                .chemin(dto.getChemin())
                .projet(projet)
                .build();

        return documentMapper.toResponseDto(documentRepository.save(document));
    }

    @Override
    public DocumentResponseDto update(Long id, DocumentRequestDto dto) {
        checkUniqueCode(dto.getCode(), id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document introuvable avec l'id : " + id
                ));

        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'id : " + dto.getProjetId()
                ));

        document.setCode(dto.getCode());
        document.setLibelle(dto.getLibelle());
        document.setDescription(dto.getDescription());
        document.setChemin(dto.getChemin());
        document.setProjet(projet);

        return documentMapper.toResponseDto(documentRepository.save(document));
    }

    @Override
    public DocumentResponseDto getById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document introuvable avec l'id : " + id
                ));

        return documentMapper.toResponseDto(document);
    }

    @Override
    public List<DocumentResponseDto> getAll() {
        return documentRepository.findAll()
                .stream()
                .map(documentMapper::toResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document introuvable avec l'id : " + id
                ));

        documentRepository.delete(document);
    }

    private void checkUniqueCode(String code, Long currentId) {
        documentRepository.findByCode(code).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new BadRequestException("Un document avec ce code existe déjà");
            }
        });
    }
}
