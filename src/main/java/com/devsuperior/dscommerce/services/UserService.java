package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /* result é uma lista (List) de objetos do tipo UserDetailsProjection.

          Essa lista vem da base de dados.

          Cada elemento da lista representa um “pedaço” do utilizador + roles (normalmente uma linha do resultado da query). */
        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
        if (result.size() == 0) {
            throw new UsernameNotFoundException("Email not found");
        }

        User user = new User();
        user.setEmail(result.get(0).getUsername());
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }

    /*

    Resumo geral (bem simples)

Este método authenticated() faz:

Pega o utilizador autenticado do Spring Security

Extrai o JWT

Lê o username do token

Vai à base de dados buscar o utilizador

Se falhar → lança erro

🔹 🧠 Em linguagem humana

👉 “Vai buscar o utilizador que fez login usando o token JWT e devolve os dados dele da base de dados.”
     */
    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return repository.findByEmail(username).get();
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {
        User entity = authenticated();
        return new UserDTO(entity);
    }
}

/* Sobre tudo o que está dentro do metodo loadUserByUsername:

1. “Percorrer todas as linhas” são estas linhas?
user.setEmail(result.get(0).getUsername());
user.setPassword(result.get(0).getPassword());

👉 Resposta curta: NÃO.

✅ O que essas linhas fazem?

Elas fazem apenas isto:

Pegam um único elemento da lista → result.get(0)

Usam esse elemento para preencher:

email

password

👉 Ou seja:

NÃO percorrem a lista
Só usam o primeiro elemento

🔁 Onde acontece “percorrer todas as linhas”?

👉 Aqui:

for (UserDetailsProjection projection : result)

Isso sim significa:

“Vai passar por TODOS os elementos da lista”

💡 Comparação clara
Código	O que faz
result.get(0)	pega só o primeiro
for (... : result)	percorre todos
❓ 2. Por que não aparece a password nas iterações?

Excelente observação — aqui está o ponto chave 👇

📦 Cada linha TEM password?

Sim, tem. Exemplo real:

{email: "joao@email.com", password: "123456", roleId: 2, authority: "ROLE_ADMIN"}

👉 Então por que não usamos?

🎯 Porque o código NÃO precisa da password no loop

Dentro do for:

user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));

👉 Ele só está interessado em:

roleId

authority

🧠 Lógica importante

Email e password → são dados do utilizador → só precisamos uma vez

Roles → podem ser várias → precisamos percorrer todas

🔥 Forma mais clara possível
Antes do for:
user.setEmail(...)
user.setPassword(...)

👉 Define o utilizador (uma vez)

Dentro do for:
user.addRole(...)

👉 Adiciona roles (várias vezes)

📊 Analogia simples

Imagina isto:

Tens um cartão de cidadão (user)
E vários acessos (roles)

O nome no cartão → só escreves uma vez

Os acessos → vais adicionando vários

⚠️ Ponto MUITO importante

Mesmo que a password esteja em todas as linhas:

👉 É sempre igual
👉 Então não faz sentido repetir

🧠 Resumo final (cristalino)

result.get(0) → pega UM registo → define user

for (...) → percorre TODOS → adiciona roles

password aparece nas linhas? → sim

usamos no loop? → não, porque não é necessário


* */

