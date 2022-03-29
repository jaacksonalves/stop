package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.validadores.ValidaQuantidadeLetrasCompativeisComRodadas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/salas")
@ICP(5)
public class SalaController {

  // 2
  private final SalaRepository salaRepository;
  private final CategoriaRepository categoriaRepository;

  @Autowired
  public SalaController(SalaRepository salaRepository, CategoriaRepository categoriaRepository) {
    this.salaRepository = salaRepository;
    this.categoriaRepository = categoriaRepository;
  }

  @InitBinder
  public void init(WebDataBinder binder) {
    binder.addValidators(
        // 1
        new ValidaQuantidadeLetrasCompativeisComRodadas());
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
