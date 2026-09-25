# 🛒 E-Commerce Distributed Platform (Event-Driven Microservices Architecture)

Uma plataforma de e-commerce completa construída sobre uma **Arquitetura de Microsserviços Orientada a Eventos**. O ecossistema inclui um **API Gateway**, **Service Discovery**, **Saga Coreografada** via mensageria, rastreamento distribuído fim a fim e automação completa de build e testes.

---

## 🛠️ Stack Tecnológica

### **Back-end & Infraestrutura**
* **Linguagem & Framework:** Java 21 | Spring Boot 3.3
* **Edge & Discovery:** Spring Cloud Gateway (Roteamento e CORS) | Spring Cloud Netflix Eureka (Service Registry)
* **Mensageria & Saga:** RabbitMQ (AMQP Broker) com suporte a compensações automáticas
* **Observabilidade & Tracing:** Micrometer Tracing + Brave | OpenZipkin Server
* **Persistência:** H2 Database (Isolado por serviço com inicialização automatizada de estado no Pedido Service) | Spring Data JPA
* **Qualidade & Testes:** JUnit 5 | Mockito | `@WebMvcTest`
* **Contentorização:** Docker | Docker Compose (Multi-stage builds)
* **CI/CD:** GitHub Actions (Automated Build & Test Pipeline)

### **Front-end**
* **Framework:** React.js (Empacotado com Nginx no Docker)
* **Comunicação:** REST API consumida exclusivamente via API Gateway

---

## 🏛️ Arquitetura e Padrões de Design

A aplicação implementa **Domain-Driven Design (DDD)** e desacoplamento total de dados. O tráfego do cliente não acede diretamente aos microsserviços internos, passando obrigatoriamente pela camada de borda (API Gateway).

### **Padrões Chave:**
1. **Choreographed Saga Pattern:** Transações distribuídas (Checkout -> Pagamento -> Baixa de Estoque) são coordenadas de forma assíncrona por eventos no RabbitMQ, sem um orquestrador central. Em caso de falha de pagamento, um evento de compensação cancela o pedido automaticamente.
2. **Database per Service:** Cada domínio (Catálogo, Pedido, Pagamento) possui o seu próprio banco de dados isolado em memória, garantindo autonomia funcional.
3. **Distributed Tracing:** Toda requisição ganha um `TraceId` e `SpanId` propagados entre chamadas HTTP e eventos RabbitMQ, permitindo auditoria visual no Zipkin.

---

## 📊 Diagramas da Arquitetura

### 1. Diagrama de Componentes e Infraestrutura

```mermaid
graph TD
   UI[Front-end React :3000] -->|HTTP / REST| Gateway[API Gateway :8080]

   Gateway -.->|1. Consulta Registro| Eureka[(Eureka Server :8761)]
   Eureka -.->|2. Retorna Instâncias| Gateway

   Gateway ==>|3. Roteamento API| CatMS[Catálogo Service :8082]
   Gateway ==>|3. Roteamento API| PedMS[Pedido Service :8087]
   Gateway ==>|3. Roteamento API| PagMS[Pagamento Service :8081]

   subgraph Ecossistema de Microsserviços e Eventos
      CatMS --> DB1[(H2 - Catálogo)]
      PedMS --> DB2[(H2 - Pedidos)]
      PagMS --> DB3[(H2 - Pagamentos)]

      Broker{{RabbitMQ Broker :5672}}
      Zipkin[Zipkin Server :9411]

      PedMS -.->|Publica / Consome Eventos| Broker
      PagMS -.->|Publica / Consome Eventos| Broker
      CatMS -.->|Publica / Consome Eventos| Broker

      Gateway -.->|Tracing Spans| Zipkin
      PedMS -.->|Tracing Spans| Zipkin
      PagMS -.->|Tracing Spans| Zipkin
      CatMS -.->|Tracing Spans| Zipkin
   end
```


```mermaid
sequenceDiagram
   autonumber
   actor Client as Frontend React
   participant GW as API Gateway
   participant Pedido as Pedido Service
   participant MQ as RabbitMQ (Broker)
   participant Pagamento as Pagamento Service
   participant Catalogo as Catálogo Service

   Client->>GW: POST /api/pedidos/checkout
   GW->>Pedido: Roteia Requisição
   Pedido->>Pedido: Salva Pedido (Status: PENDENTE)
   Pedido-->>GW: 202 Accepted (Retorna Pedido)
   GW-->>Client: Confirmação de Processamento

   Note over Pedido,MQ: Início da Saga Coreografada (Assíncrono)

   Pedido->>MQ: Publica Evento [pedido.criado]
   MQ->>Pagamento: Consome [pedido.criado]
   Pagamento->>Pagamento: Processa Transação Financeira

   alt Pagamento Aprovado
      Pagamento->>MQ: Publica Evento [pagamento.sucesso]
      MQ->>Pedido: Consome [pagamento.sucesso] -> Atualiza Status (PAGO)
      MQ->>Catalogo: Consome [pagamento.sucesso] -> Baixa no Estoque
   else Pagamento Recusado
      Pagamento->>MQ: Publica Evento [pagamento.falha]
      MQ->>Pedido: Consome [pagamento.falha] -> Atualiza Status (CANCELADO)
   end
```

```mermaid
erDiagram
   PRODUTO {
      Long id PK
      String nome
      String descricao
      BigDecimal preco
      Integer quantidadeEstoque
   }
   CARRINHO {
      Long id PK
   }
   ITEM_CARRINHO {
      Long id PK
      Long carrinho_id FK
      Long produtoId
      Integer quantidade
      BigDecimal precoUnitario
   }
   PEDIDO {
      Long id PK
      String status
      BigDecimal total
      LocalDateTime dataCriacao
   }
   ITEM_PEDIDO {
      Long id PK
      Long pedido_id FK
      Long produtoId
      Integer quantidade
      BigDecimal precoUnitario
   }
   PAGAMENTO {
      Long id PK
      Long pedidoId
      String status
      BigDecimal valor
      LocalDateTime dataProcessamento
   }

   CARRINHO ||--o{ ITEM_CARRINHO : "contém"
   PEDIDO ||--o{ ITEM_PEDIDO : "contém"
```