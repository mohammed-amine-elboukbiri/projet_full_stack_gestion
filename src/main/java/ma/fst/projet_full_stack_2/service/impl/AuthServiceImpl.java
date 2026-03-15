package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.config.JwtService;
import ma.fst.projet_full_stack_2.dto.auth.AuthResponseDto;
import ma.fst.projet_full_stack_2.dto.auth.LoginRequestDto;
import ma.fst.projet_full_stack_2.entities.Employe;
import ma.fst.projet_full_stack_2.exception.ResourceNotFoundException;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final EmployeRepository employeRepository;

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getLogin());
        String token = jwtService.generateToken(userDetails);

        Employe employe = employeRepository.findByLogin(dto.getLogin())
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        return AuthResponseDto.builder()
                .token(token)
                .type("Bearer")
                .employeId(employe.getId())
                .login(employe.getLogin())
                .profil(employe.getProfil() != null ? employe.getProfil().getCode() : null)
                .build();
    }
}
