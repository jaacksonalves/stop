package br.com.jackson.stop.sala;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/salas")
// ICP(4.0)
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

    var uri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(novaSala.getId())
            .toUri();

    return ResponseEntity.created(uri).build();
  }
}
