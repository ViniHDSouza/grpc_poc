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
import java.util.concurrent.ConcurrentMap;

public class ProductServer {

    private Server server;
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    // Inicia o servidor
    public void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ProductServiceImpl())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // Implementação do serviço
    private class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

        // Create
        @Override
        public void createProduct(CreateProductRequest request,
                                  StreamObserver<Product> responseObserver) {
            Product product = Product.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setName(request.getName())
                    .setPrice(request.getPrice())
                    .setStock(request.getStock())
                    .build();

            products.put(product.getId(), product);
            responseObserver.onNext(product);
            responseObserver.onCompleted();
        }

        // Read (Single)
        @Override
        public void getProduct(GetProductRequest request,
                               StreamObserver<Product> responseObserver) {
            Product product = products.get(request.getId());
            if (product == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Produto não encontrado")
                        .asRuntimeException());
            } else {
                responseObserver.onNext(product);
                responseObserver.onCompleted();
            }
        }

        // Read (All)
        @Override
        public void getAllProducts(Empty request,
                                   StreamObserver<Product> responseObserver) {
            products.values().forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        }

        // Update
        @Override
        public void updateProduct(UpdateProductRequest request,
                                  StreamObserver<Product> responseObserver) {
            Product existing = products.get(request.getId());
            if (existing == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Produto não encontrado")
                        .asRuntimeException());
                return;
            }

            Product updated = existing.toBuilder()
                    .setName(request.getName())
                    .setPrice(request.getPrice())
                    .setStock(request.getStock())
                    .build();

            products.put(request.getId(), updated);
            responseObserver.onNext(updated);
            responseObserver.onCompleted();
        }

        // Delete
        @Override
        public void deleteProduct(DeleteProductRequest request,
                                  StreamObserver<DeleteProductResponse> responseObserver) {
            boolean exists = products.containsKey(request.getId());
            products.remove(request.getId());

            responseObserver.onNext(DeleteProductResponse.newBuilder()
                    .setSuccess(exists)
                    .build());
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ProductServer server = new ProductServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
