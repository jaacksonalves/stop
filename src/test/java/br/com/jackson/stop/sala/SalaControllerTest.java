package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.handler.ExceptionsHandlerResponse;
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

import static br.com.jackson.stop.sala.SalaFactory.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class SalaControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;
  @Autowired private SalaRepository salaRepository;

  private String toJson(NovaSalaRequest request) throws JsonProcessingException {
    return mapper.writeValueAsString(request);
  }

  private Object fromJson(String response, Class<?> classe) throws JsonProcessingException {
    return mapper.readValue(response, classe);
  }

  @Nested
  class MetodoCadastrarTest {

    @Test
    @DisplayName("Deve cadastrar uma sala com request válido com senha")
    void teste1() throws Exception {
      var request = criaNovaSalaRequest("senha", 4, List.of("A", "B", "C", "D"));
      mockMvc
          .perform(post("/api/salas").contentType(APPLICATION_JSON).content(toJson(request)))
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"));

      var salas = salaRepository.findAll();
      var sala = salas.iterator().next();

      assertAll(
          () -> assertEquals(1, salas.size()),
          () -> assertEquals(request.rodadas(), sala.getInformacoesSala().getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.categorias().size(), sala.getCategorias().size()),
          () -> assertNotNull(sala.getSenha()),
          () -> assertEquals(request.senha(), sala.getSenha()),
          () -> assertEquals(request.maximoJogadores(), sala.getInformacoesSala().getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getInformacoesSala().getTempoJogo()),
          () -> assertTrue(sala.isPrivada()));
    }

    @Test
    @DisplayName("Deve cadastrar uma sala com request válido sem senha")
    void teste2() throws Exception {
      var request = criaNovaSalaRequest(null, 4, List.of("A", "B", "C", "D"));
      mockMvc
          .perform(post("/api/salas").contentType(APPLICATION_JSON).content(toJson(request)))
          .andExpect(status().isCreated())
          .andExpect(header().exists("location"));

      var salas = salaRepository.findAll();
      var sala = salas.iterator().next();

      assertAll(
          () -> assertEquals(1, salas.size()),
          () -> assertEquals(request.rodadas(), sala.getInformacoesSala().getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.categorias().size(), sala.getCategorias().size()),
          () -> assertNull(sala.getSenha()),
          () -> assertEquals(request.maximoJogadores(), sala.getInformacoesSala().getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getInformacoesSala().getTempoJogo()),
          () -> assertFalse(sala.isPrivada()));
    }

    @Test
    @DisplayName("Não deve cadastrar uma sala com request inválido com menos letras do que rodadas")
    void teste3() throws Exception {
      var request = criaNovaSalaRequest(null, 5, List.of("A", "B", "C", "D"));
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
                  "A quantidade de letras deve ser maior ou igual ao numero de rodadas",
                  resposta.getErros().get("Erro na requisição").iterator().next()),
          () -> assertEquals(1, resposta.getErros().size()),
          () -> assertNotNull(resposta.getOcorridoEm()));
    }

    @Test
    @DisplayName("Não deve cadastrar uma sala com request com menos letras inválidas")
    void teste4() throws Exception {
      var request = criaNovaSalaRequest(null, 4, List.of("A", "0", "~", "!"));
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
                  "Apenas letras maiusculas e unicas de A a Z sao permitidas",
                  resposta.getErros().get("letras").iterator().next()),
          () -> assertEquals(2, resposta.getErros().size()),
          () -> assertNotNull(resposta.getOcorridoEm()));
    }
  }

  @Nested
  class MetodoBuscarDisponiveisTest {
    @Test
    @DisplayName("Deve retornar uma lista de salas disponíveis")
    void teste1() throws Exception {
      var sala1 = criaSalaSemSenha();
      var sala2 = criaSalaComSenha();
      salaRepository.saveAll(List.of(sala1, sala2));

      mockMvc
          .perform(get("/api/salas"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value(sala1.getId().intValue()))
          .andExpect(jsonPath("$[1].id").value(sala2.getId().intValue()))
          .andExpect(jsonPath("$[0].numeroMaximoParticipantes").value(sala1.getInformacoesSala().getMaximoJogadores()))
          .andExpect(jsonPath("$[1].numeroMaximoParticipantes").value(sala2.getInformacoesSala().getMaximoJogadores()))
          .andExpect(jsonPath("$[0].numeroParticipantesAtual").value(sala1.getUsuarios().size()))
          .andExpect(jsonPath("$[1].numeroParticipantesAtual").value(sala2.getUsuarios().size()))
          .andExpect(jsonPath("$[0].quantidadeCategorias").value(sala1.getCategorias().size()))
          .andExpect(jsonPath("$[1].quantidadeCategorias").value(sala2.getCategorias().size()))
          .andExpect(jsonPath("$[0].quantidadeRodadas").value(sala1.getInformacoesSala().getRodadas()))
          .andExpect(jsonPath("$[1].quantidadeRodadas").value(sala2.getInformacoesSala().getRodadas()))
          .andExpect(jsonPath("$[0].privada").value(false))
          .andExpect(jsonPath("$[1].privada").value(true));

      // não fiz assert transformando o response na classe de DTO de resposta, pq precisaria criar
      // um construtor vazio na clase,
      // não sei se isso é muito legal, mudar o código para o teste ficar melhor
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há salas disponíveis")
    void teste2() throws Exception {

      mockMvc
          .perform(get("/api/salas"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isEmpty());
    }
  }

  @Nested
  class MetodoBuscarTest {
    @Test
    @DisplayName("Deve retornar os dados de uma sala sem senha, status 200")
    void teste1() throws Exception {
      var sala = criaSalaSemSenha();
      salaRepository.save(sala);

      mockMvc
          .perform(get("/api/salas/" + sala.getId()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.tempoJogo").value(sala.getInformacoesSala().getTempoJogo().toString()))
          .andExpect(
              jsonPath("$.categorias[0].nomeCategoria")
                  .value(sala.getCategorias().get(0).getNome()))
          .andExpect(jsonPath("$.letras[0]").value(sala.getLetras().get(0)))
          .andExpect(jsonPath("$.letras[1]").value(sala.getLetras().get(1)))
          .andExpect(jsonPath("$.letras[2]").value(sala.getLetras().get(2)))
          .andExpect(jsonPath("$.letras[3]").value(sala.getLetras().get(3)));
    }

    @Test
    @DisplayName("Deve retornar os dados de uma uma sala com senha (privada), status 200")
    void teste2() throws Exception {
      var sala = criaSalaComSenha();
      salaRepository.save(sala);

      mockMvc
          .perform(get("/api/salas/" + sala.getId()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.tempoJogo").value(sala.getInformacoesSala().getTempoJogo().toString()))
          .andExpect(
              jsonPath("$.categorias[0].nomeCategoria")
                  .value(sala.getCategorias().get(0).getNome()))
          .andExpect(jsonPath("$.letras[0]").value(sala.getLetras().get(0)))
          .andExpect(jsonPath("$.letras[1]").value(sala.getLetras().get(1)))
          .andExpect(jsonPath("$.letras[2]").value(sala.getLetras().get(2)))
          .andExpect(jsonPath("$.letras[3]").value(sala.getLetras().get(3)));
    }

    @Test
    @DisplayName("Não deve retornar os dados de uma sala quando a sala não existe, status 404")
    void teste3() throws Exception {
      var response =
          mockMvc
              .perform(get("/api/salas/1"))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.mensagem").value("Sala não encontrada"))
              .andExpect(jsonPath("$.ocorridoEm").exists())
              .andReturn()
              .getResponse()
              .getContentAsString(UTF_8);

      var resposta =
          (ExceptionsHandlerResponse) fromJson(response, ExceptionsHandlerResponse.class);

      assertAll(
          () -> assertEquals("Sala não encontrada", resposta.getMensagem()),
          () -> assertNotNull(resposta.getOcorridoEm()));
    }
  }
}
