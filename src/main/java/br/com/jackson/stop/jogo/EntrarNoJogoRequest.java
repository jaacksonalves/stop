package br.com.jackson.stop.jogo;

import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;

public record EntrarNoJogoRequest(@NotBlank String nomeJogador, String senha, Long idUsuario) {

    //adicionei o Id do usuário pra simular um usuário já cadastrado anteriormente e enviando o seu token
    public Usuario toUsuario(UsuarioRepository usuarioRepository) {
        if (idUsuario == null){
            return new Usuario(nomeJogador);
        }
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}
