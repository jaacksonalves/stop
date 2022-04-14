package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/salas")
@ICP(6)
public class SalaController {

  // 2
  private final SalaRepository salaRepository;
  private final CategoriaRepository categoriaRepository;

  @Autowired
  public SalaController(SalaRepository salaRepository, CategoriaRepository categoriaRepository) {
    this.salaRepository = salaRepository;
    this.categoriaRepository = categoriaRepository;
  }

  @PostMapping
  @Transactional
  // 1
  public ResponseEntity<?> cadastrar(@RequestBody @Valid NovaSalaRequest request) {
    // 1
    var novaSala = request.toSala(categoriaRepository);
    salaRepository.save(novaSala);

    var locationURI =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(novaSala.getId())
            .toUri();

    return ResponseEntity.created(locationURI).build();
  }

  @GetMapping
  public ResponseEntity<List<SalasResponse>> buscarDisponiveis() {
    var salasDisponiveis =
        salaRepository.findAll().stream().filter(Sala::temVaga).toList();
    // 1
    var salasResponses = salasDisponiveis.stream().map(SalasResponse::new).toList();
    return ResponseEntity.ok(salasResponses);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<DetalheDaSalaResponse> buscar(@PathVariable Long id) {
    var sala =
        salaRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sala não encontrada"));
    // 1
    return ResponseEntity.ok(new DetalheDaSalaResponse(sala));
  }
}
