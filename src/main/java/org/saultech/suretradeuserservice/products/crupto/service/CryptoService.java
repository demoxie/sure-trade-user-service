package org.saultech.suretradeuserservice.products.crupto.service;

import org.saultech.suretradeuserservice.products.crupto.vo.CryptoTransactionVO;
import reactor.core.publisher.Mono;

public interface CryptoService {

    Mono<CryptoTransactionVO> sendEther(String fromAddress, String toAddress, String password, String amount) throws Exception;
}
