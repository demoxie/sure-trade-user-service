package org.saultech.suretradeuserservice.products.crupto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CryptoTransactionVO {
    private String transactionHash;
    private Integer blockNumber;
    private BigDecimal gasUsed;
    private String status;
    private String message;
    private String additionalInfo;
    private String transactionIndex;
    private String blockHash;
    private String cumulativeGasUsed;
    private String contractAddress;
    private String root;
    private String from;
    private String to;
    private List<Log> logs;
    private String logsBloom;
    private String revertReason;
    private String type;
    private String effectiveGasPrice;
}
