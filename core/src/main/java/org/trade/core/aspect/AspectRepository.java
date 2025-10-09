package org.trade.core.aspect;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Repository
public interface AspectRepository<T extends Aspect, ID extends Serializable> extends JpaRepository<T, ID> {
}

