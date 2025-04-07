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

# gRPC CRUD Example

Este projeto demonstra a implementação de um servidor gRPC que realiza operações CRUD (Create, Read, Update, Delete) para gerenciar produtos. Abaixo está uma explicação do fluxo lógico do gRPC no contexto deste CRUD.

## Fluxo Lógico do gRPC no CRUD

1. **Definição do Serviço no Proto File**:
   - O serviço `ProductService` é definido em um arquivo `.proto`, especificando os métodos gRPC disponíveis, como `CreateProduct`, `GetProduct`, `GetAllProducts`, `UpdateProduct` e `DeleteProduct`.
   - Cada método tem suas mensagens de requisição e resposta definidas no mesmo arquivo.

2. **Geração do Código Stub**:
   - O arquivo `.proto` é compilado usando o `protoc` para gerar as classes Java necessárias, incluindo as mensagens e a interface do serviço.

3. **Implementação do Serviço**:
   - A classe `ProductServer` implementa o serviço `ProductService` gerado pelo `protoc`.
   - A lógica de negócios para cada método gRPC é implementada na classe interna `ProductServiceImpl`.

4. **Fluxo de Cada Operação CRUD**:
   - **CreateProduct**:
     - Recebe uma requisição contendo os detalhes do produto.
     - Gera um ID único para o produto e o armazena em um mapa em memória.
     - Retorna o produto criado como resposta.
   - **GetProduct**:
     - Recebe um ID de produto como entrada.
     - Busca o produto no mapa em memória.
     - Retorna o produto encontrado ou um erro `NOT_FOUND` se o produto não existir.
   - **GetAllProducts**:
     - Não recebe parâmetros.
     - Faz o streaming de todos os produtos armazenados no mapa para o cliente.
   - **UpdateProduct**:
     - Recebe um ID de produto e os novos detalhes do produto.
     - Atualiza os detalhes do produto existente no mapa.
     - Retorna o produto atualizado ou um erro `NOT_FOUND` se o produto não existir.
   - **DeleteProduct**:
     - Recebe um ID de produto como entrada.
     - Remove o produto correspondente do mapa.
     - Retorna uma resposta indicando sucesso ou um erro `NOT_FOUND` se o produto não existir.

5. **Execução do Servidor**:
   - O servidor gRPC é iniciado na porta `50051` usando a classe `ProductServer`.
   - A implementação do serviço `ProductServiceImpl` é registrada no servidor.
   - O servidor permanece ativo até ser encerrado manualmente ou por um hook de desligamento.

6. **Comunicação Cliente-Servidor**:
   - O cliente gRPC se comunica com o servidor enviando requisições para os métodos definidos no serviço.
   - O servidor processa as requisições, executa a lógica de negócios e retorna as respostas apropriadas.

## Estrutura do Projeto

- **`ProductServer.java`**: Contém a implementação do servidor gRPC e a lógica do CRUD.
- **`proto`**: Diretório contendo o arquivo `.proto` que define o serviço e as mensagens.

## Como Executar

1. Compile o arquivo `.proto` para gerar as classes Java.
2. Compile o projeto Java.
3. Inicie o servidor executando a classe `ProductServer`.
4. Use um cliente gRPC para testar as operações CRUD.

## Exemplo de Uso

- Criar um produto:
  ```
  CreateProductRequest {
    name: "Produto A",
    price: 100.0,
    stock: 50
  }
  ```
- Obter um produto:
  ```
  GetProductRequest {
    id: "123e4567-e89b-12d3-a456-426614174000"
  }
  ```
- Atualizar um produto:
  ```
  UpdateProductRequest {
    id: "123e4567-e89b-12d3-a456-426614174000",
    name: "Produto A Atualizado",
    price: 120.0,
    stock: 40
  }
  ```
- Excluir um produto:
  ```
  DeleteProductRequest {
    id: "123e4567-e89b-12d3-a456-426614174000"
  }
  ```

Este fluxo lógico garante que o servidor gRPC seja eficiente e fácil de entender, permitindo a manipulação de produtos de forma simples e escalável.

