package org.saultech.suretradeuserservice.business.service;

import org.saultech.suretradeuserservice.business.vo.CryptoTransactionVO;
import reactor.core.publisher.Mono;

public interface CryptoService {

    Mono<CryptoTransactionVO> sendEther(String fromAddress, String toAddress, String password, String amount) throws Exception;
}
