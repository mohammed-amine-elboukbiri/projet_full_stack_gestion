package ma.fst.projet_full_stack_2.service.impl;

import lombok.RequiredArgsConstructor;
import ma.fst.projet_full_stack_2.config.JwtService;
import ma.fst.projet_full_stack_2.dto.auth.AuthResponseDto;
import ma.fst.projet_full_stack_2.dto.auth.LoginRequestDto;
import ma.fst.projet_full_stack_2.entities.Employe;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final EmployeRepository employeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        Employe employe = employeRepository.findByLogin(dto.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        if (!passwordEncoder.matches(dto.getPassword(), employe.getPassword())) {
            throw new BadCredentialsException("Identifiants invalides");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(employe.getLogin());
        String token = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .type("Bearer")
                .employeId(employe.getId())
                .login(employe.getLogin())
                .profil(employe.getProfil() != null ? employe.getProfil().getCode() : null)
                .build();
    }
}
