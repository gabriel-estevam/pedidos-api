package io.github.dougllasfps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.dougllasfps.service.impl.UsuarioServiceImpl;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // esse metodo faz a autenticação dos usuários, de onde vamos buscar os usuarios
        auth.userDetailsService(usuarioService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Esse metodo para a autorização, isto é, ele pega o usuario autentica e
        // verifica qual a role daquele usuario
        http.csrf().disable() // desativamos essa configuração no ambiente de dev
                .authorizeRequests()
                // .antMatchers("/api/clientes/**").hasRole("USER")//aqui nesse metodo definimos
                // qual url vai poder acesssar e quais a roles do usuario
                // .permitAll() //esse metodo permite acesso a url mesmo não estando autenticado
                // .antMatchers("/api/clientes/**").authenticated() // segunda forma de liberar
                // acesso é verificar se o
                // usuario esta autenticado, isto é, se ele passou pelo
                // metodo acima
                .antMatchers("/api/clientes/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/produtos/**").hasRole("ADMIN")
                .antMatchers("/api/pedidos/**").hasAnyRole("USER", "ADMIN")
                .and()
                .httpBasic(); // autenticação via header
        // .formLogin(); // cria o formulario de login do spring security ou é possivel
        // passar um caminho
        // de um formulario de login customizado
    }

}
