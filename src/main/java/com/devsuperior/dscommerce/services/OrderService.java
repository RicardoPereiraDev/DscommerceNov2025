package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.dto.OrderItemDTO;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    //findById(id) → procura Order pelo ID
    //
    //order.getClient().getId() → pega no ID do cliente dessa Order
    public OrderDTO findById(Long id) {
        Order order = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado"));
        authService.validateSelfOrAdmin(order.getClient().getId());// esta linha de codigo diz o seguinte: "Só permite continuar se o utilizador autenticado for o dono da encomenda OU um admin."
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {

        Order order = new Order();

        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        User user = userService.authenticated();//Isto vai buscar o utilizador que está atualmente autenticado no sistema.
        order.setClient(user);//Esta encomenda pertence a este utilizador

        for (OrderItemDTO itemDto : dto.getItems()) {

            /*itemDto.getProductId()

Isso significa:

"Vai ao itemDTO buscar o ID do produto"

Porque cada item do pedido tem algo assim:

ItemDTO:
   productId = 5
   quantity = 2

    "Vai ao banco buscar o produto cujo ID veio dentro do itemDTO"

    Exemplo concreto

Se o itemDto for:

productId = 5
quantity = 2

Então:

itemDto.getProductId() → 5

E a linha vira:

productRepository.getReferenceById(5);

👉 Ou seja:

Vai buscar o produto com ID = 5
    */

            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item); //Adiciona esse item à encomenda
        }

        repository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);

         /* Resumo final
User user = userService.authenticated();

👉 vai buscar o utilizador logado

order.setClient(user);

👉 associa a encomenda a esse utilizador

💡 Frase para fixar
"Quem está logado é quem faz a encomenda" */



        /*
        A linha
for (OrderItemDTO itemDto : dto.getItems())
1️⃣ O que é dto.getItems()

Lembra da tua classe OrderDTO:

private List<OrderItemDTO> items = new ArrayList<>();

Então:

dto.getItems()

👉 devolve:

Uma lista de itens da encomenda (OrderItemDTO)
2️⃣ O que é OrderItemDTO itemDto

Aqui estás a dizer:

"Para cada item dessa lista..."

OrderItemDTO → tipo de cada elemento da lista

itemDto → variável que representa um item de cada vez

3️⃣ Tradução para linguagem normal
for (OrderItemDTO itemDto : dto.getItems())

significa:

Para cada item (OrderItemDTO) que está dentro da lista de items do OrderDTO
4️⃣ Exemplo simples

Imagina que o OrderDTO tem:

items = [
   ItemDTO(produto=1, qtd=2),
   ItemDTO(produto=5, qtd=1)
]

O loop vai fazer:

1ª volta → itemDto = ItemDTO(produto=1, qtd=2)
2ª volta → itemDto = ItemDTO(produto=5, qtd=1)
5️⃣ Forma equivalente (mais explícita)

Isto:

for (OrderItemDTO itemDto : dto.getItems())

é igual a:

List<OrderItemDTO> lista = dto.getItems();

for (int i = 0; i < lista.size(); i++) {
    OrderItemDTO itemDto = lista.get(i);
}
🎯 Resumo simples

dto.getItems() → lista de itens do pedido

OrderItemDTO → tipo de cada item

itemDto → um item de cada vez no loop

💡 Frase para fixar
"Estou a percorrer cada item da lista de itens do pedido"

          */
    }
}
