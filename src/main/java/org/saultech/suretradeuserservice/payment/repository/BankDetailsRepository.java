package org.saultech.suretradeuserservice.payment.repository;

import org.saultech.suretradeuserservice.payment.entity.BankDetails;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankDetailsRepository extends R2dbcRepository<BankDetails, Long> {
}
