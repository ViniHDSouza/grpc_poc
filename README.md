# POC gRPC (Prova de Conceito)

## O que é gRPC?

gRPC (gRPC Remote Procedure Call) é um framework de alto desempenho e código aberto desenvolvido pelo Google para construir sistemas distribuídos. Ele permite a comunicação entre aplicações cliente e servidor usando um protocolo baseado em HTTP/2. O gRPC utiliza Protocol Buffers (protobuf) como sua linguagem de definição de interface (IDL) para definir contratos de serviço e estruturas de dados.

### Principais Características do gRPC:
- **Multiplataforma**: Suporta múltiplas linguagens de programação.
- **Eficiente**: Usa HTTP/2 para multiplexação e serialização binária para transmissão compacta de dados.
- **Streaming**: Suporta RPCs unários, streaming do servidor, streaming do cliente e streaming bidirecional.
- **Geração de Código**: Gera automaticamente o código do cliente e do servidor a partir de arquivos `.proto`.

## Visão Geral do Projeto

Este projeto demonstra uma aplicação baseada em gRPC para gerenciamento de produtos. Ele inclui um servidor e um cliente, ambos implementados em Java usando Spring Boot e bibliotecas gRPC.

### Estrutura do Projeto

- **proto**: Contém os arquivos `.proto` e a configuração para gerar o código gRPC.
- **grpc-server**: Implementa o servidor gRPC que fornece serviços de gerenciamento de produtos.
- **grpc-client**: Implementa o cliente gRPC que interage com o servidor.

### Funcionalidades

1. **Criar Produto**: Adiciona um novo produto ao sistema.
2. **Obter Produto**: Recupera os detalhes de um produto específico pelo ID.
3. **Obter Todos os Produtos**: Faz o streaming de todos os produtos do servidor para o cliente.
4. **Atualizar Produto**: Atualiza os detalhes de um produto existente.
5. **Excluir Produto**: Exclui um produto pelo ID.

### Como Funciona

1. **Protocol Buffers**:
   - O arquivo `product.proto` define o serviço (`ProductService`) e as estruturas de dados (`Product`, `CreateProductRequest`, etc.).
   - O `protobuf-maven-plugin` gera classes Java para o serviço e mensagens.

2. **Servidor**:
   - A classe `ProductServer` implementa a interface `ProductServiceGrpc.ProductServiceImplBase`.
   - Ela fornece métodos para lidar com requisições gRPC (ex.: `createProduct`, `getProduct`).

3. **Cliente**:
   - A classe `ProductClient` usa o `ProductServiceGrpc.ProductServiceBlockingStub` para fazer chamadas gRPC síncronas ao servidor.

### Pré-requisitos

- Java 21
- Maven
- Compilador Protocol Buffers (`protoc`)

### Como Executar

1. **Gerar Código gRPC**:
   ```bash
   mvn clean compile
   ```

2. **Iniciar o Servidor**:
   ```bash
   cd grpc-server
   mvn spring-boot:run
   ```

3. **Executar o Cliente**:
   ```bash
   cd grpc-client
   mvn exec:java -Dexec.mainClass="graphql.poc.client.ProductClient"
   ```

### Exemplo de Uso

- **Criar um Produto**:
  ```java
  Product product = client.createProduct("Notebook", 1200.00, 10);
  System.out.println("Produto Criado: " + product);
  ```

- **Obter um Produto**:
  ```java
  Product product = client.getProduct("id-do-produto");
  System.out.println("Detalhes do Produto: " + product);
  ```

- **Obter Todos os Produtos**:
  ```java
  List<Product> products = client.getAllProducts();
  products.forEach(System.out::println);
  ```

### Dependências

- [gRPC Java](https://grpc.io/docs/languages/java/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Protocol Buffers](https://developers.google.com/protocol-buffers)

