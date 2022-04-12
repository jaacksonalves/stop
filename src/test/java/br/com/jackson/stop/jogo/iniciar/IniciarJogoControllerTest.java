package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.jogo.iniciar.IniciarJogoRequest;
import br.com.jackson.stop.sala.SalaFactory;
import br.com.jackson.stop.sala.SalaRepository;
import br.com.jackson.stop.usuario.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static br.com.jackson.stop.sala.SalaFactory.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class IniciarJogoControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SalaRepository salaRepository;
  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private ObjectMapper mapper;

  @Nested
  class MetodoJogoAleatorioTest {

    @Test
    @DisplayName("Deve retornar um jogo aleatório, não privado, com sala disponível, status 200")
    void teste1() throws Exception {
      var sala1 = criaSalaSemSenha();
      var sala2 = criaSalaComSenha();
      salaRepository.saveAll(List.of(sala1, sala2));

      var entrarNoJogoRequest = new IniciarJogoRequest("usuario");
      var request = mapper.writeValueAsString(entrarNoJogoRequest);

      mockMvc
          .perform(post("/api/jogo").contentType(APPLICATION_JSON).content(request))
          .andExpect(status().isOk())
          .andExpect(jsonPath("usuarios[0].nome").value(entrarNoJogoRequest.getNomeUsuario()))
          .andExpect(jsonPath("letras[0]").value(sala1.getLetras().get(0)))
          .andExpect(jsonPath("letras[1]").value(sala1.getLetras().get(1)))
          .andExpect(jsonPath("letras[2]").value(sala1.getLetras().get(2)))
          .andExpect(jsonPath("letras[3]").value(sala1.getLetras().get(3)))
          .andExpect(jsonPath("rodadas").value(sala1.getInformacoesSala().getRodadas()))
          .andExpect(
              jsonPath("categorias[0].nomeCategoria")
                  .value(sala1.getCategorias().get(0).getNome()));

      var usuario = usuarioRepository.findAll().iterator().next();

      assertAll(
          () -> assertEquals(entrarNoJogoRequest.getNomeUsuario(), usuario.getNome()),
          () -> assertEquals(sala1, usuario.getSala()),
          () -> assertEquals(1, sala1.getJogadoresAtuais()));
    }

    @Test
    @DisplayName("Não deve retornar sala aleatória nome não for enviado")
    void teste4() throws Exception {
      var sala1 = criaSalaComSenha();
      var sala2 = criaSalaComSenha2();
      salaRepository.saveAll(List.of(sala1, sala2));

      var entrarNoJogoRequest = new IniciarJogoRequest("");
      var request = mapper.writeValueAsString(entrarNoJogoRequest);

      mockMvc
          .perform(post("/api/jogo").contentType(APPLICATION_JSON).content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.erros.nomeUsuario").value("não deve estar em branco"))
          .andExpect(jsonPath("$.ocorridoEm").exists());

      var usuarios = usuarioRepository.findAll();

      assertAll(() -> assertEquals(0, usuarios.size()));
    }
  }

  @Nested
  class MetodoJogoEspecificoTest {

    @Test
    @DisplayName("Deve retornar um jogo específico, não privado, com sala disponível, status 200")
    void teste1() throws Exception {
      var sala = criaSalaSemSenha();
      salaRepository.save(sala);

      var entrarNoJogoRequest = new IniciarJogoRequest("usuario");
      var request = mapper.writeValueAsString(entrarNoJogoRequest);

      mockMvc
          .perform(post("/api/jogo/" + sala.getId()).contentType(APPLICATION_JSON).content(request))
          .andExpect(status().isOk())
          .andExpect(jsonPath("usuarios[0].nome").value(entrarNoJogoRequest.getNomeUsuario()))
          .andExpect(jsonPath("letras[0]").value(sala.getLetras().get(0)))
          .andExpect(jsonPath("letras[1]").value(sala.getLetras().get(1)))
          .andExpect(jsonPath("letras[2]").value(sala.getLetras().get(2)))
          .andExpect(jsonPath("letras[3]").value(sala.getLetras().get(3)))
          .andExpect(jsonPath("rodadas").value(sala.getInformacoesSala().getRodadas()))
          .andExpect(
              jsonPath("categorias[0].nomeCategoria").value(sala.getCategorias().get(0).getNome()));

      var usuario = usuarioRepository.findAll().iterator().next();

      assertAll(
          () -> assertEquals(entrarNoJogoRequest.getNomeUsuario(), usuario.getNome()),
          () -> assertEquals(sala, usuario.getSala()));
    }

    @Test
    @DisplayName("Deve retornar um jogo específico, privado, com sala disponível, status 200")
    void teste2() throws Exception {
      var sala = SalaFactory.criaSalaComSenha();
      salaRepository.save(sala);

      var entrarNoJogoRequest = new IniciarJogoRequest("usuario");
      entrarNoJogoRequest.setSenha("senha");

      var request = mapper.writeValueAsString(entrarNoJogoRequest);

      mockMvc
          .perform(post("/api/jogo/" + sala.getId()).contentType(APPLICATION_JSON).content(request))
          .andExpect(status().isOk())
          .andExpect(jsonPath("usuarios[0].nome").value(entrarNoJogoRequest.getNomeUsuario()))
          .andExpect(jsonPath("letras[0]").value(sala.getLetras().get(0)))
          .andExpect(jsonPath("letras[1]").value(sala.getLetras().get(1)))
          .andExpect(jsonPath("letras[2]").value(sala.getLetras().get(2)))
          .andExpect(jsonPath("letras[3]").value(sala.getLetras().get(3)))
          .andExpect(jsonPath("rodadas").value(sala.getInformacoesSala().getRodadas()))
          .andExpect(
              jsonPath("categorias[0].nomeCategoria").value(sala.getCategorias().get(0).getNome()));

      var usuario = usuarioRepository.findAll().iterator().next();

      assertAll(
          () -> assertEquals(entrarNoJogoRequest.getNomeUsuario(), usuario.getNome()),
          () -> assertEquals(sala, usuario.getSala()));
    }

    @Test
    @DisplayName("Não deve retornar sala específica se o nome não for enviado")
    void teste5() throws Exception {
      var sala1 = criaSalaSemSenha();
      salaRepository.save(sala1);

      var entrarNoJogoRequest = new IniciarJogoRequest("");
      var request = mapper.writeValueAsString(entrarNoJogoRequest);

      mockMvc
          .perform(post("/api/jogo").contentType(APPLICATION_JSON).content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.erros.nomeUsuario").value("não deve estar em branco"))
          .andExpect(jsonPath("$.ocorridoEm").exists());

      var usuarios = usuarioRepository.findAll();

      assertAll(() -> assertEquals(0, usuarios.size()));
    }
  }
}
