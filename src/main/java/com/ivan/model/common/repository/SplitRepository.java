package com.ivan.model.common.repository;

import java.util.Optional;

public interface SplitRepository<M, ID> {
    M save(M model);
    M findById(ID id);
}
