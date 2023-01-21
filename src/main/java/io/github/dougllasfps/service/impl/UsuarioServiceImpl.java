package io.github.dougllasfps.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.dougllasfps.domain.entity.Usuario;
import io.github.dougllasfps.domain.repository.UsuarioRepository;
import io.github.dougllasfps.exception.SenhaInvalidaException;

@Service
public class UsuarioServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UsuarioRepository repository;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }

    public UserDetails autenticar(Usuario usuario) {
        UserDetails userCreated = loadUserByUsername(usuario.getLogin());
        boolean senhasMatch = encoder.matches(usuario.getSenha(), userCreated.getPassword());
        if(senhasMatch) {
            return userCreated;
        }
        throw new SenhaInvalidaException("Senha invalida!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Esse metodo carrega o usuario da base de dados

        Usuario usuario = repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario [ " + username + "]Não encontrado"));

        String[] roles = usuario.isAdmin() ? new String[] { "ADMIN", "USER" } : new String[] { "USER" };

        return User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(roles)
                .build();
    }

}
