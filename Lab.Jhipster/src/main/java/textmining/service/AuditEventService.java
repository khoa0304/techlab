package textmining.service;

import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;

public class AuditEventService {

	
	public Mono<? extends Authentication> saveAuthenticationSuccess(String userName) {
		return null;
	}
}
