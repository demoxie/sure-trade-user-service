package org.saultech.suretradeuserservice.products.crupto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.products.crupto.vo.CryptoTransactionVO;
import org.saultech.suretradeuserservice.utils.LoggingService;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService{
    private final Web3j web3j;
    private final ModelMapper mapper;

    @Override
    public Mono<CryptoTransactionVO> sendEther(String fromAddress, String toAddress, String password, String amount) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(password, fromAddress);
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3j, credentials, toAddress,
                Convert.toWei(amount, Convert.Unit.ETHER),
                Convert.Unit.WEI).send();
        LoggingService.logResponse(transactionReceipt, "CryptoServiceImpl", "sendEther");
        return Mono.just(mapper.map(transactionReceipt, CryptoTransactionVO.class));
    }
}
