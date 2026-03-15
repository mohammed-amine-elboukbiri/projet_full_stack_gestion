package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.entities.Employe;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeRepository employeRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Employe employe = employeRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + login));

        String role = employe.getProfil() != null ? employe.getProfil().getCode() : "USER";

        return new User(
                employe.getLogin(),
                employe.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}