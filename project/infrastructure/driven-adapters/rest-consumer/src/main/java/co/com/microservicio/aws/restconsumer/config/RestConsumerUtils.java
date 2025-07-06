package co.com.microservicio.aws.restconsumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.experimental.UtilityClass;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@UtilityClass
public class RestConsumerUtils {
    public static ClientHttpConnector getClientHttpConnector(Long timeout) {
        return new ReactorClientHttpConnector(HttpClient.create()
            .compress(true)
            .keepAlive(true)
            .option(CONNECT_TIMEOUT_MILLIS, timeout.intValue())
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
            }));
    }
}
