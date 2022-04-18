package br.com.jackson.stop.jogo.resposta;

import javax.validation.constraints.NotNull;
import java.util.Map;

public record EnviarRespostasRequest(@NotNull Long usuarioId, Map<String, String> respostas) {
}
