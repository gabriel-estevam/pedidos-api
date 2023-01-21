package io.github.dougllasfps.security.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
//import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

//import io.github.dougllasfps.VendasApplication;
import io.github.dougllasfps.domain.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    @Value("${security.jwt.expiracao}")
    private String expiracao;

    @Value("${security.jwt.chave-assinatura}")
    private String chaveAssinatura;

    public String gerarToken(Usuario usuario) {
    //metodo par gerar um token JWT

        /*HashMap<String, Object> claims = new HashMap<>();
        claims.put("email usuario:","gabriel@email.com");
        claims.put("roles", "admin");
        */
        long expString = Long.valueOf(expiracao); 
        // expira em 30 minutos
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(expString); // pega data hora atual e soma mais 30 minutos
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);
        return Jwts.builder()
                   .setSubject(usuario.getLogin()) // identifica o usuario no payload
                   .setExpiration(data)
                  // .setClaims(claims) customiza a informação do payload
                   .signWith(SignatureAlgorithm.HS512, chaveAssinatura) // aqui ele faz a chave de assinatura do token;
                   .compact();

    }

    private Claims obterClaims(String token) throws ExpiredJwtException {
    //metodo para retornar as informações do token
    //lança exceção quando token esta expirado (invalido)

        return Jwts.parser()
                   .setSigningKey(chaveAssinatura)
                   .parseClaimsJws(token)
                   .getBody();
    }

    public boolean tokenValido(String token) {
        try {
            Claims clamis = obterClaims(token);
            Date dataExpiracao = clamis.getExpiration();
            LocalDateTime data =  dataExpiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(data); //token valido
        } catch (Exception e) {
            return false;
        }
    }

    public String obterLoginUsuario(String token) throws ExpiredJwtException {
        return (String) obterClaims(token).getSubject();
    }

    
    //uma maneira de rodar um pedaço da aplicação sem iniciar o spring inteiro
   /*  public static void main(String[] args) {
        ConfigurableApplicationContext contexto = SpringApplication.run(VendasApplication.class);
        JwtService service = contexto.getBean(JwtService.class);
        Usuario usuario = Usuario.builder().login("gabriel").build();
        String token = service.gerarToken(usuario);
        System.out.println(token);

        boolean isTokenValido = service.tokenValido(token);
        System.out.println("O token esta valido?" + isTokenValido);
        
        System.out.println(service.obterLoginUsuario(token));
    }*/
}
