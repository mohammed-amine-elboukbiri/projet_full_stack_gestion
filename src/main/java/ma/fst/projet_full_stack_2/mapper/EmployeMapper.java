package ma.fst.projet_full_stack_2.mapper;

import ma.fst.projet_full_stack_2.dto.employe.EmployeRequestDto;
import ma.fst.projet_full_stack_2.dto.employe.EmployeResponseDto;
import ma.fst.projet_full_stack_2.entities.Employe;
import org.springframework.stereotype.Component;

@Component
public class EmployeMapper {

    public EmployeResponseDto toResponseDto(Employe employe) {
        if (employe == null) return null;

        return EmployeResponseDto.builder()
                .id(employe.getId())
                .matricule(employe.getMatricule())
                .nom(employe.getNom())
                .prenom(employe.getPrenom())
                .telephone(employe.getTelephone())
                .email(employe.getEmail())
                .login(employe.getLogin())
                .profilId(employe.getProfil() != null ? employe.getProfil().getId() : null)
                .profilCode(employe.getProfil() != null ? employe.getProfil().getCode() : null)
                .profilLibelle(employe.getProfil() != null ? employe.getProfil().getLibelle() : null)
                .build();
    }

    public void updateEntityFromDto(EmployeRequestDto dto, Employe employe) {
        employe.setMatricule(dto.getMatricule());
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setTelephone(dto.getTelephone());
        employe.setEmail(dto.getEmail());
        employe.setLogin(dto.getLogin());

    }
}