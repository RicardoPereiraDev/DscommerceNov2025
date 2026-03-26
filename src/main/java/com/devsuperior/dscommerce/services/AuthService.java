package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    public void validateSelfOrAdmin(long userId) {
        User me = userService.authenticated();
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
    }

}
/*🔹 🔹 Exemplo prático completo

Rui (id = 5, não admin) tenta aceder userId = 5:

!me.hasRole("ROLE_ADMIN") → true

!me.getId().equals(userId) → false

Resultado: true && false = false → não entra no if ✅ permitido

Rui (id = 5, não admin) tenta aceder userId = 10:

!me.hasRole("ROLE_ADMIN") → true

!me.getId().equals(userId) → true

Resultado: true && true = true → entra no if ❌ acesso negado

Admin (id = qualquer, role = admin) tenta aceder userId = 10:

!me.hasRole("ROLE_ADMIN") → false

!me.getId().equals(userId) → true

Resultado: false && true = false → não entra no if ✅ permitido

🔹 🔹 Em linguagem simples

“Se não és admin e não és o dono da conta, então bloqueia o acesso.” */