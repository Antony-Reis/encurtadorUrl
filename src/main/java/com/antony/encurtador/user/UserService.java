package com.antony.encurtador.user;

import com.antony.encurtador.exceptions.AuthErrorException;
import com.antony.encurtador.exceptions.ConflictErrorException;
import com.antony.encurtador.exceptions.NotFoundErrorException;
import com.antony.encurtador.user.utils.RUserDto;
import com.antony.encurtador.user.utils.RUserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final IUserRepository iUserRepository;

    public UserService(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    public RUserResponseDto registerUser(RUserDto body) throws ConflictErrorException {
        if (body.email() == null && body.password() == null){
            throw new RuntimeException("Email ou senha não podem ser null");
        }

        if (iUserRepository.existsByEmail(body.email())){
            throw new ConflictErrorException("Email");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(body.password());

        iUserRepository.save(new UserEntity(body.email(), encryptedPassword));

        return new RUserResponseDto(HttpStatus.CREATED, "User registado");
    }

    private UserEntity getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();}

    public RUserResponseDto patchPasswordUser(RUserDto body) throws AuthErrorException, NotFoundErrorException {
        if (body.email() == null && body.password() == null){
            throw new RuntimeException("Email ou senha não podem ser null");
        }

        if (!iUserRepository.existsByEmail(body.email())){
            throw new NotFoundErrorException("Email");
        }

        UserEntity user = getAuthenticatedUser();

        if (!user.getEmail().equals(body.email())){
            throw new AuthErrorException();
        }

        if (body.password() != null){
            String encryptedPassword = new BCryptPasswordEncoder().encode(body.password());
            user.setSenha(encryptedPassword);
        }

        iUserRepository.save(user);

        return new RUserResponseDto(HttpStatus.OK, "User atualizado com sucesso");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return iUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));
    }
}
