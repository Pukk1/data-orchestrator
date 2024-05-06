package com.ivan.model.orchestrator.repository;

public interface SplitRepository<M, ID> {
    M save(M model);
    M findById(ID id);
}
