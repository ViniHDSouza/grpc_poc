package graphql.poc.client;

import com.google.protobuf.Empty;
import dev.product.*;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
public class ProductClient {

    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    public ProductClient(Channel channel) {
        blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }

    // Create
    public Product createProduct(String name, double price, int stock) {
        return blockingStub.createProduct(
                CreateProductRequest.newBuilder()
                        .setName(name)
                        .setPrice(price)
                        .setStock(stock)
                        .build()
        );
    }

    // Read (Single)
    public Product getProduct(String id) {
        return blockingStub.getProduct(
                GetProductRequest.newBuilder()
                        .setId(id)
                        .build()
        );
    }

    // Read (All)
    public List<Product> getAllProducts() {
        Iterator<Product> products = blockingStub.getAllProducts(Empty.getDefaultInstance());
        List<Product> productList = new ArrayList<>();
        products.forEachRemaining(productList::add);
        return productList;
    }

    // Update
    public Product updateProduct(String id, String name, double price, int stock) {
        return blockingStub.updateProduct(
                UpdateProductRequest.newBuilder()
                        .setId(id)
                        .setName(name)
                        .setPrice(price)
                        .setStock(stock)
                        .build()
        );
    }

    // Delete
    public boolean deleteProduct(String id) {
        return blockingStub.deleteProduct(
                DeleteProductRequest.newBuilder()
                        .setId(id)
                        .build()
        ).getSuccess();
    }

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50051")
                .usePlaintext()
                .build();

        ProductClient client = new ProductClient(channel);

        // Teste completo do CRUD
        try {
            // Create
            Product newProduct = client.createProduct("Smartphone", 2500.00, 15);
            System.out.println("Criado: " + newProduct);

            // Read
            Product retrieved = client.getProduct(newProduct.getId());
            System.out.println("Consultado: " + retrieved);

            // Update
            Product updated = client.updateProduct(
                    newProduct.getId(),
                    "Smartphone Premium",
                    2999.99,
                    10
            );
            System.out.println("Atualizado: " + updated);

            // List All
            System.out.println("Todos produtos:");
            client.getAllProducts().forEach(System.out::println);

            // Delete
            boolean deleted = client.deleteProduct(newProduct.getId());
            System.out.println("Deletado? " + deleted);

        } finally {
            channel.shutdown();
        }
    }


}