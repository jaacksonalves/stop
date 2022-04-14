CREATE INDEX sala_letras_sala_id_idx ON public.sala_letras (sala_id);

CREATE INDEX salas_categorias_sala_id_idx ON public.salas_categorias (sala_id, categorias_id);
