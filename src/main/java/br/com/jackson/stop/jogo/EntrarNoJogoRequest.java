package br.com.jackson.stop.jogo;

import br.com.jackson.stop.usuario.Usuario;

import javax.validation.constraints.NotBlank;

public record EntrarNoJogoRequest(@NotBlank String nomeJogador, String senha) {

    public Usuario toUsuario() {
        return new Usuario(nomeJogador);
    }
}
