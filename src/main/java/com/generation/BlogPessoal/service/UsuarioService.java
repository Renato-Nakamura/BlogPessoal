package com.generation.BlogPessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.generation.BlogPessoal.model.Usuario;
import com.generation.BlogPessoal.model.UsuarioLogin;
import com.generation.BlogPessoal.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public Optional<Usuario> CadastrarUsuario(Usuario usuario) {
		if(repository.findByUsuario(usuario.getUsuario()).isPresent()) {
			return null;
		}
			
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String senhaEncoder = encoder.encode(usuario.getSenha());
		usuario.setSenha(senhaEncoder);
		return Optional.of(repository.save(usuario));
	}
	
	public Optional<UsuarioLogin> login(Optional<UsuarioLogin> usuarioLogin){
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuarioDB = repository.findByUsuario(usuarioLogin.get().getUsuario());
		
		if(usuarioDB.isPresent()) {
			if(encoder.matches(usuarioLogin.get().getSenha(), usuarioDB.get().getSenha())) {
				String auth = usuarioLogin.get().getUsuario() + ":" + usuarioLogin.get().getSenha();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic "+ new String(encodedAuth);
				
				usuarioLogin.get().setToken(authHeader);
				usuarioLogin.get().setNome(usuarioDB.get().getNome());
				//usuarioLogin.get().setSenha(usuarioDB.get().getSenha());
				usuarioLogin.get().setFoto(usuarioDB.get().getFoto());
				usuarioLogin.get().setTipo(usuarioDB.get().getTipo());
				usuarioLogin.get().setId(usuarioDB.get().getId());

				return usuarioLogin;
			}
		}
		return Optional.empty();
	}
}
