/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import sample.repository.UserRepository;

@Component
public class SpringDataJpaUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public SpringDataJpaUserDetailsService(UserRepository repository) {
		this.userRepository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		sample.model.User user = this.userRepository.findByEmail(email);
		if (user != null) {
			//String[] roles = (String[]) user.getRoles().stream().map(r -> r.name()).collect(Collectors.toList()).toArray();
			return new User(String.valueOf(user.getId()), user.getPassword(), user.isActive(), true, true, true,
					AuthorityUtils.createAuthorityList(user.getRole().name()));

		} else {
			throw new UsernameNotFoundException("email:" + email);
		}
	}
}
