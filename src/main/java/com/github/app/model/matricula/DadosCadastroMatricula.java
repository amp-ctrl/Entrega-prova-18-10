package com.github.app.model.matricula;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroMatricula(

    @NotNull
    Integer idAluno,

    @NotNull
    @JsonAlias("professorId")
    Integer idProfessor,

    @NotNull
    Turno turno,

    String semestre,

    String observacao,

    LocalDateTime dataMatricula
) {
}
