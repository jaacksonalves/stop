package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.handler.ValidationExceptionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static br.com.jackson.stop.sala.SalaFactory.criaNovaSalaRequest;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class SalaControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;
  @Autowired private SalaRepository salaRepository;

  @Nested
  class MetodoCadastrarTest {

    @Test
    @DisplayName("Deve cadastrar uma sala com request válido com senha")
    void teste1() throws Exception {
      var request = criaNovaSalaRequest("senha", 3, List.of("A", "B", "C"));
      mockMvc
          .perform(post("/api/salas").contentType(APPLICATION_JSON).content(toJson(request)))
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"));

      var salas = salaRepository.findAll();
      var sala = salas.iterator().next();

      assertAll(
          () -> assertEquals(1, salas.size()),
          () -> assertEquals(request.rodadas(), sala.getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.categorias().size(), sala.getCategorias().size()),
          () -> assertNotNull(sala.getSenha()),
          () -> assertEquals(request.senha(), sala.getSenha()),
          () -> assertEquals(request.maximoJogadores(), sala.getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getTempoJogo()),
          () -> assertTrue(sala.isPrivada()));
    }

    @Test
    @DisplayName("Deve cadastrar uma sala com request válido sem senha")
    void teste2() throws Exception {
      var request = criaNovaSalaRequest(null, 3, List.of("A", "B", "C"));
      mockMvc
          .perform(post("/api/salas").contentType(APPLICATION_JSON).content(toJson(request)))
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"));

      var salas = salaRepository.findAll();
      var sala = salas.iterator().next();

      assertAll(
          () -> assertEquals(1, salas.size()),
          () -> assertEquals(request.rodadas(), sala.getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.categorias().size(), sala.getCategorias().size()),
          () -> assertNull(sala.getSenha()),
          () -> assertEquals(request.maximoJogadores(), sala.getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getTempoJogo()),
          () -> assertFalse(sala.isPrivada()));
    }

    @Test
    @DisplayName("Não deve cadastrar uma sala com request inválido com menos letras do que rodadas")
    void teste3() throws Exception {
      var request = criaNovaSalaRequest(null, 4, List.of("A", "B", "C"));
      var response =
          mockMvc
              .perform(post("/api/salas").contentType(APPLICATION_JSON).content(toJson(request)))
              .andExpect(status().isBadRequest())
              .andReturn()
              .getResponse()
              .getContentAsString(UTF_8);

      var resposta =
          (ValidationExceptionResponse) fromJson(response, ValidationExceptionResponse.class);

      assertAll(
          () -> assertTrue(salaRepository.findAll().isEmpty()),
          () ->
              assertEquals(
                  "A quantidade de letras deve ser maior ou igual ao número de rodadas",
                  resposta.getErros().get("Erro na requisição").iterator().next()),
          () -> assertEquals(1, resposta.getErros().size()),
          () -> assertNotNull(resposta.getOcorridoEm()));
    }

    @Test
    @DisplayName("Não deve cadastrar uma sala com request com menos letras inválidas")
    void teste4() throws Exception {
      var request = criaNovaSalaRequest(null, 3, List.of("A", "0", "~"));
      var response =
          mockMvc
              .perform(post("/api/salas").contentType(APPLICATION_JSON).content(toJson(request)))
              .andExpect(status().isBadRequest())
              .andReturn()
              .getResponse()
              .getContentAsString(UTF_8);

      var resposta =
          (ValidationExceptionResponse) fromJson(response, ValidationExceptionResponse.class);

      assertAll(
          () -> assertTrue(salaRepository.findAll().isEmpty()),
          () ->
              assertEquals(
                  "Apenas letras maiúsculas únicas de A a Z são permitidas",
                  resposta.getErros().get("letras").iterator().next()),
          () -> assertEquals(1, resposta.getErros().size()),
          () -> assertNotNull(resposta.getOcorridoEm()));
    }
  }

  private String toJson(NovaSalaRequest request) throws JsonProcessingException {
    return mapper.writeValueAsString(request);
  }

  private Object fromJson(String response, Class<?> classe) throws JsonProcessingException {
    return mapper.readValue(response, classe);
  }
}
