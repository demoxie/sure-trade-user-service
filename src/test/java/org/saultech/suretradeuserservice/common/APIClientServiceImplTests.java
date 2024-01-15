package org.saultech.suretradeuserservice.common;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.saultech.suretradeuserservice.exception.APIException;
import org.saultech.suretradeuserservice.payment.repository.BankDetailsRepository;
import org.saultech.suretradeuserservice.products.giftcard.vo.BankDetailsVO;
import org.saultech.suretradeuserservice.products.giftcard.vo.GiftCardRateVO;
import org.saultech.suretradeuserservice.products.giftcard.vo.GiftCardTransactionVO;
import org.saultech.suretradeuserservice.products.giftcard.vo.GiftCardVO;
import org.saultech.suretradeuserservice.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {APIClientServiceImpl.class})
@ExtendWith(SpringExtension.class)
class APIClientServiceImplTests {
    @Autowired
    private APIClientServiceImpl aPIClientServiceImpl;

    @MockBean
    private BankDetailsRepository bankDetailsRepository;

    @MockBean
    private ClientSelector clientSelector;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    /**
     * Method under test:
     * {@link APIClientServiceImpl#makePutRequestWithoutQueryParamsWithMonoReturned(String, String, Object, String)}
     */
    @Test
    void testMakePutRequestWithoutQueryParamsWithMonoReturned() {
        // Arrange
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        BankDetailsVO.BankDetailsVOBuilder bankNameResult = BankDetailsVO.builder()
                .accountName("Dr Jane Doe")
                .accountNumber("42")
                .accountType("3")
                .bankCode("Bank Code")
                .bankCountry("GB")
                .bankName("Bank Name");
        BankDetailsVO.BankDetailsVOBuilder idResult = bankNameResult.createdAt(LocalDate.of(1970, 1, 1).atStartOfDay())
                .currency("GBP")
                .id(1L);
        BankDetailsVO buildResult = idResult.updatedAt(LocalDate.of(1970, 1, 1).atStartOfDay()).userId(1L).build();
        Mono<BankDetailsVO> justResult = Mono.just(buildResult);
        when(responseSpec.bodyToMono(Mockito.<Class<BankDetailsVO>>any())).thenReturn(justResult);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);
        when(responseSpec2.onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any())).thenReturn(responseSpec);
        WebClient.RequestHeadersSpec<WebClient.RequestBodySpec> requestHeadersSpec = mock(
                WebClient.RequestHeadersSpec.class);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec2);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        Mockito.<WebClient.RequestHeadersSpec<?>>when(
                requestBodySpec.body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any())).thenReturn(requestHeadersSpec);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(requestBodyUriSpec.uri(Mockito.<String>any(), isA(Object[].class))).thenReturn(requestBodySpec);
        WebClient webClient = mock(WebClient.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(clientSelector.select(Mockito.<String>any())).thenReturn(webClient);

        // Act
        aPIClientServiceImpl.makePutRequestWithoutQueryParamsWithMonoReturned("foo", "Product", "Body", "BankDetailsVO");

        // Assert
        verify(clientSelector).select(Mockito.<String>any());
        verify(webClient).put();
        verify(requestBodySpec).body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Mockito.<Class<BankDetailsVO>>any());
        verify(responseSpec2).onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any());
        verify(requestBodyUriSpec).uri(Mockito.<String>any(), isA(Object[].class));
    }

    /**
     * Method under test:
     * {@link APIClientServiceImpl#makePutRequestWithoutQueryParamsWithMonoReturned(String, String, Object, String)}
     */
    @Test
    void testMakePutRequestWithoutQueryParamsWithMonoReturned2() {
        // Arrange
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        APIException buildResult = APIException.builder().message("An error occurred").statusCode(1).build();
        when(responseSpec.bodyToMono(Mockito.<Class<BankDetailsVO>>any())).thenThrow(buildResult);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);
        when(responseSpec2.onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any())).thenReturn(responseSpec);
        WebClient.RequestHeadersSpec<WebClient.RequestBodySpec> requestHeadersSpec = mock(
                WebClient.RequestHeadersSpec.class);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec2);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        Mockito.<WebClient.RequestHeadersSpec<?>>when(
                requestBodySpec.body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any())).thenReturn(requestHeadersSpec);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(requestBodyUriSpec.uri(Mockito.<String>any(), isA(Object[].class))).thenReturn(requestBodySpec);
        WebClient webClient = mock(WebClient.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(clientSelector.select(Mockito.<String>any())).thenReturn(webClient);

        // Act and Assert
        assertThrows(APIException.class, () -> aPIClientServiceImpl.makePutRequestWithoutQueryParamsWithMonoReturned("foo",
                "Product", "Body", "BankDetailsVO"));
        verify(clientSelector).select(Mockito.<String>any());
        verify(webClient).put();
        verify(requestBodySpec).body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Mockito.<Class<BankDetailsVO>>any());
        verify(responseSpec2).onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any());
        verify(requestBodyUriSpec).uri(Mockito.<String>any(), isA(Object[].class));
    }

    /**
     * Method under test:
     * {@link APIClientServiceImpl#makePutRequestWithoutQueryParamsWithMonoReturned(String, String, Object, String)}
     */
    @Test
    void testMakePutRequestWithoutQueryParamsWithMonoReturned3() {
        // Arrange
        Mono<BankDetailsVO> mono = mock(Mono.class);
        Mono<Object> justResult = Mono.just("Data");
        when(mono.map(Mockito.<Function<BankDetailsVO, Object>>any())).thenReturn(justResult);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Mockito.<Class<BankDetailsVO>>any())).thenReturn(mono);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);
        when(responseSpec2.onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any())).thenReturn(responseSpec);
        WebClient.RequestHeadersSpec<WebClient.RequestBodySpec> requestHeadersSpec = mock(
                WebClient.RequestHeadersSpec.class);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec2);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        Mockito.<WebClient.RequestHeadersSpec<?>>when(
                requestBodySpec.body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any())).thenReturn(requestHeadersSpec);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(requestBodyUriSpec.uri(Mockito.<String>any(), isA(Object[].class))).thenReturn(requestBodySpec);
        WebClient webClient = mock(WebClient.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(clientSelector.select(Mockito.<String>any())).thenReturn(webClient);

        // Act
        Mono<APIResponse> actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult = aPIClientServiceImpl
                .makePutRequestWithoutQueryParamsWithMonoReturned("foo", "Product", "Body", "BankDetailsVO");

        // Assert
        verify(clientSelector).select(Mockito.<String>any());
        verify(webClient).put();
        verify(requestBodySpec).body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Mockito.<Class<BankDetailsVO>>any());
        verify(responseSpec2).onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any());
        verify(requestBodyUriSpec).uri(Mockito.<String>any(), isA(Object[].class));
        verify(mono).map(Mockito.<Function<BankDetailsVO, Object>>any());
        assertSame(justResult, actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult);
    }

    /**
     * Method under test:
     * {@link APIClientServiceImpl#makePutRequestWithoutQueryParamsWithMonoReturned(String, String, Object, String)}
     */
    @Test
    void testMakePutRequestWithoutQueryParamsWithMonoReturned4() {
        // Arrange
        Mono<BankDetailsVO> mono = mock(Mono.class);
        Mono<Object> justResult = Mono.just("Data");
        when(mono.map(Mockito.<Function<BankDetailsVO, Object>>any())).thenReturn(justResult);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Mockito.<Class<BankDetailsVO>>any())).thenReturn(mono);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);
        when(responseSpec2.onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any())).thenReturn(responseSpec);
        WebClient.RequestHeadersSpec<WebClient.RequestBodySpec> requestHeadersSpec = mock(
                WebClient.RequestHeadersSpec.class);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec2);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        Mockito.<WebClient.RequestHeadersSpec<?>>when(
                requestBodySpec.body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any())).thenReturn(requestHeadersSpec);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(requestBodyUriSpec.uri(Mockito.<String>any(), isA(Object[].class))).thenReturn(requestBodySpec);
        WebClient webClient = mock(WebClient.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(clientSelector.select(Mockito.<String>any())).thenReturn(webClient);

        // Act
        Mono<APIResponse> actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult = aPIClientServiceImpl
                .makePutRequestWithoutQueryParamsWithMonoReturned("foo", "Product", "Body", "GiftCardRateVO");

        // Assert
        verify(clientSelector).select(Mockito.<String>any());
        verify(webClient).put();
        verify(requestBodySpec).body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Mockito.<Class<GiftCardRateVO>>any());
        verify(responseSpec2).onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any());
        verify(requestBodyUriSpec).uri(Mockito.<String>any(), isA(Object[].class));
        verify(mono).map(Mockito.<Function<BankDetailsVO, Object>>any());
        assertSame(justResult, actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult);
    }

    /**
     * Method under test:
     * {@link APIClientServiceImpl#makePutRequestWithoutQueryParamsWithMonoReturned(String, String, Object, String)}
     */
    @Test
    void testMakePutRequestWithoutQueryParamsWithMonoReturned5() {
        // Arrange
        Mono<BankDetailsVO> mono = mock(Mono.class);
        Mono<Object> justResult = Mono.just("Data");
        when(mono.map(Mockito.<Function<BankDetailsVO, Object>>any())).thenReturn(justResult);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Mockito.<Class<BankDetailsVO>>any())).thenReturn(mono);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);
        when(responseSpec2.onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any())).thenReturn(responseSpec);
        WebClient.RequestHeadersSpec<WebClient.RequestBodySpec> requestHeadersSpec = mock(
                WebClient.RequestHeadersSpec.class);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec2);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        Mockito.<WebClient.RequestHeadersSpec<?>>when(
                requestBodySpec.body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any())).thenReturn(requestHeadersSpec);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(requestBodyUriSpec.uri(Mockito.<String>any(), isA(Object[].class))).thenReturn(requestBodySpec);
        WebClient webClient = mock(WebClient.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(clientSelector.select(Mockito.<String>any())).thenReturn(webClient);

        // Act
        Mono<APIResponse> actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult = aPIClientServiceImpl
                .makePutRequestWithoutQueryParamsWithMonoReturned("foo", "Product", "Body", "GiftCardTransactionVO");

        // Assert
        verify(clientSelector).select(Mockito.<String>any());
        verify(webClient).put();
        verify(requestBodySpec).body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Mockito.<Class<GiftCardTransactionVO>>any());
        verify(responseSpec2).onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any());
        verify(requestBodyUriSpec).uri(Mockito.<String>any(), isA(Object[].class));
        verify(mono).map(Mockito.<Function<BankDetailsVO, Object>>any());
        assertSame(justResult, actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult);
    }

    /**
     * Method under test:
     * {@link APIClientServiceImpl#makePutRequestWithoutQueryParamsWithMonoReturned(String, String, Object, String)}
     */
    @Test
    void testMakePutRequestWithoutQueryParamsWithMonoReturned6() {
        // Arrange
        Mono<BankDetailsVO> mono = mock(Mono.class);
        Mono<Object> justResult = Mono.just("Data");
        when(mono.map(Mockito.<Function<BankDetailsVO, Object>>any())).thenReturn(justResult);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Mockito.<Class<BankDetailsVO>>any())).thenReturn(mono);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);
        when(responseSpec2.onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any())).thenReturn(responseSpec);
        WebClient.RequestHeadersSpec<WebClient.RequestBodySpec> requestHeadersSpec = mock(
                WebClient.RequestHeadersSpec.class);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec2);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        Mockito.<WebClient.RequestHeadersSpec<?>>when(
                requestBodySpec.body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any())).thenReturn(requestHeadersSpec);
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        when(requestBodyUriSpec.uri(Mockito.<String>any(), isA(Object[].class))).thenReturn(requestBodySpec);
        WebClient webClient = mock(WebClient.class);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(clientSelector.select(Mockito.<String>any())).thenReturn(webClient);

        // Act
        Mono<APIResponse> actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult = aPIClientServiceImpl
                .makePutRequestWithoutQueryParamsWithMonoReturned("foo", "Product", "Body", "GiftCardVO");

        // Assert
        verify(clientSelector).select(Mockito.<String>any());
        verify(webClient).put();
        verify(requestBodySpec).body(Mockito.<BodyInserter<Object, ClientHttpRequest>>any());
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Mockito.<Class<GiftCardVO>>any());
        verify(responseSpec2).onStatus(Mockito.<Predicate<HttpStatusCode>>any(),
                Mockito.<Function<ClientResponse, Mono<Throwable>>>any());
        verify(requestBodyUriSpec).uri(Mockito.<String>any(), isA(Object[].class));
        verify(mono).map(Mockito.<Function<BankDetailsVO, Object>>any());
        assertSame(justResult, actualMakePutRequestWithoutQueryParamsWithMonoReturnedResult);
    }
}
