package co.com.microservicio.aws.redis.config.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisSecret {
    private String username;
    private String password;
    private String host;
    private String port;
    private String hostReplicas;
}
