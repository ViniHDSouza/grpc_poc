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

/**
 * A classe ProductClient é responsável por interagir com o servidor gRPC.
 * Ela utiliza os stubs gerados pelo gRPC para realizar chamadas de procedimento remoto.
 */
public class ProductClient {

    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    /**
     * Construtor que inicializa o stub de bloqueio com o canal gRPC fornecido.
     */
    public ProductClient(Channel channel) {
        blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Cria um novo produto chamando o RPC CreateProduct.
     */
    public Product createProduct(String name, double price, int stock) {
        CreateProductRequest request = CreateProductRequest.newBuilder()
                .setName(name)
                .setPrice(price)
                .setStock(stock)
                .build();

        return blockingStub.createProduct(request);
    }

    /**
     * Recupera um produto pelo seu ID chamando o RPC GetProduct.
     */
    public Product getProduct(String id) {
        GetProductRequest request = GetProductRequest.newBuilder()
                .setId(id)
                .build();

        return blockingStub.getProduct(request);
    }

    /**
     * Recupera todos os produtos chamando o RPC GetAllProducts.
     * Faz o streaming dos produtos do servidor.
     */
    public List<Product> getAllProducts() {
        Iterator<Product> products = blockingStub.getAllProducts(Empty.getDefaultInstance());
        List<Product> productList = new ArrayList<>();
        products.forEachRemaining(productList::add);
        return productList;
    }

    /**
     * Atualiza um produto existente chamando o RPC UpdateProduct.
     */
    public Product updateProduct(String id, String name, double price, int stock) {
        UpdateProductRequest request = UpdateProductRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setPrice(price)
                .setStock(stock)
                .build();

        return blockingStub.updateProduct(request);
    }

    /**
     * Exclui um produto pelo seu ID chamando o RPC DeleteProduct.
     */
    public boolean deleteProduct(String id) {
        DeleteProductRequest request = DeleteProductRequest.newBuilder()
                .setId(id)
                .build();

        DeleteProductResponse response = blockingStub.deleteProduct(request);
        return response.getSuccess();
    }

    /**
     * Método principal para demonstrar o uso do cliente.
     */
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ProductClient client = new ProductClient(channel);

        // Example usage
        Product product = client.createProduct("Laptop", 1500.00, 5);
        System.out.println("Created Product: " + product);

        Product fetchedProduct = client.getProduct(product.getId());
        System.out.println("Fetched Product: " + fetchedProduct);

        List<Product> allProducts = client.getAllProducts();
        System.out.println("All Products: " + allProducts);

        Product updatedProduct = client.updateProduct(product.getId(), "Gaming Laptop", 2000.00, 3);
        System.out.println("Updated Product: " + updatedProduct);

        boolean isDeleted = client.deleteProduct(product.getId());
        System.out.println("Product Deleted: " + isDeleted);

        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}