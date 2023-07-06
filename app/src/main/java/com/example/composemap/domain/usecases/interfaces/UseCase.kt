package com.example.composemap.domain.usecases.interfaces


interface UseCase<in P : UseCase.Parameters, out R : Any?> {

    suspend fun execute(parameters: P = EmptyParameters as P) :R

    interface Parameters
    object EmptyParameters : Parameters
}

interface EmptyParamsUseCase<out R : Any?> : UseCase<UseCase.EmptyParameters, R>
