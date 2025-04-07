package graphql.poc.server;

import com.google.protobuf.Empty;
import dev.product.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A classe ProductServer é responsável por iniciar o servidor gRPC e implementar o serviço ProductService.
 * Ela lida com as requisições gRPC recebidas e fornece respostas baseadas na lógica de negócios.
 */
public class ProductServer {

    private Server server;
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    /**
     * Inicia o servidor gRPC na porta 50051 e registra a implementação do ProductService.
     * Adiciona um hook para desligar o servidor de forma graciosa quando a aplicação for encerrada.
     */
    public void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ProductServiceImpl()) // Registers the service implementation
                .build()
                .start();

        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * Para o servidor gRPC de forma graciosa.
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Bloqueia a thread principal até que o servidor seja encerrado.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Método principal para iniciar o servidor.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ProductServer server = new ProductServer();
        server.start();
        server.blockUntilShutdown();
    }

    /**
     * Implementação do ProductService definido no arquivo .proto.
     * Esta classe lida com a lógica de negócios para cada método gRPC.
     */
    private class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

        /**
         * Lida com a chamada RPC CreateProduct.
         * Cria um novo produto e o armazena no mapa em memória.
         */
        @Override
        public void createProduct(CreateProductRequest request, StreamObserver<Product> responseObserver) {
            Product product = Product.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName(request.getName())
                    .setPrice(request.getPrice())
                    .setStock(request.getStock())
                    .build();

            products.put(product.getId(), product);

            responseObserver.onNext(product); // Sends the created product as a response
            responseObserver.onCompleted(); // Completes the RPC call
        }

        /**
         * Lida com a chamada RPC GetProduct.
         * Recupera um produto pelo seu ID.
         */
        @Override
        public void getProduct(GetProductRequest request, StreamObserver<Product> responseObserver) {
            Product product = products.get(request.getId());
            if (product == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Product not found")
                        .asRuntimeException());
            } else {
                responseObserver.onNext(product);
                responseObserver.onCompleted();
            }
        }

        /**
         * Lida com a chamada RPC GetAllProducts.
         * Faz o streaming de todos os produtos para o cliente.
         */
        @Override
        public void getAllProducts(Empty request, StreamObserver<Product> responseObserver) {
            products.values().forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        }

        /**
         * Lida com a chamada RPC UpdateProduct.
         * Atualiza os detalhes de um produto existente.
         */
        @Override
        public void updateProduct(UpdateProductRequest request, StreamObserver<Product> responseObserver) {
            Product existingProduct = products.get(request.getId());
            if (existingProduct == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Product not found")
                        .asRuntimeException());
            } else {
                Product updatedProduct = Product.newBuilder()
                        .setId(existingProduct.getId())
                        .setName(request.getName())
                        .setPrice(request.getPrice())
                        .setStock(request.getStock())
                        .build();

                products.put(updatedProduct.getId(), updatedProduct);

                responseObserver.onNext(updatedProduct);
                responseObserver.onCompleted();
            }
        }

        /**
         * Lida com a chamada RPC DeleteProduct.
         * Exclui um produto pelo seu ID.
         */
        @Override
        public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
            Product removedProduct = products.remove(request.getId());
            if (removedProduct == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Product not found")
                        .asRuntimeException());
            } else {
                DeleteProductResponse response = DeleteProductResponse.newBuilder()
                        .setSuccess(true)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }
    }
}
