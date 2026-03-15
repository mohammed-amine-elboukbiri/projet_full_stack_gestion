package ma.fst.projet_full_stack_2.metier;

import ma.fst.projet_full_stack_2.dto.auth.AuthResponseDto;
import ma.fst.projet_full_stack_2.dto.auth.LoginRequestDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto dto);
}
