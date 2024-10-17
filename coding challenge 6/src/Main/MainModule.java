package Main;

import Dao.IOrderManagementRepository;
import Dao.OrderProcessor;
import entity.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainModule {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        IOrderManagementRepository orderRepo = new OrderProcessor();

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Get All Products");
            System.out.println("6. Get Orders by User");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    // Create User
                    System.out.print("Enter User ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter Role (Admin/User): ");
                    String role = scanner.nextLine();
                    User newUser = new User(userId, username, password, role);
                    orderRepo.createUser(newUser);
                    break;

                case 2:
                    // Create Product (Only Admin users)
                    System.out.print("Enter Admin User ID: ");
                    userId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Product Name: ");
                    String productName = scanner.nextLine();
                    System.out.print("Enter Description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter Quantity in Stock: ");
                    int quantityInStock = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Type (Electronics/Clothing): ");
                    String type = scanner.nextLine();

                    Product product;
                    if (type.equalsIgnoreCase("Electronics")) {
                        System.out.print("Enter Brand: ");
                        String brand = scanner.nextLine();
                        System.out.print("Enter Warranty Period (in months): ");
                        int warrantyPeriod = scanner.nextInt();
                        product = new Electronics(0, productName, description, price, quantityInStock, brand, warrantyPeriod);
                    } else {
                        System.out.print("Enter Size: ");
                        String size = scanner.nextLine();
                        System.out.print("Enter Color: ");
                        String color = scanner.nextLine();
                        product = new Clothing(0, productName, description, price, quantityInStock, size, color);
                    }

                    User adminUser = new User(userId, "", "", "Admin");
                    orderRepo.createProduct(adminUser, product);
                    break;

                case 3:
                    // Create Order
                    System.out.print("Enter User ID: ");
                    userId = scanner.nextInt();
                    scanner.nextLine();
                    List<Product> products = new ArrayList<>();
                    System.out.print("Enter number of products to order: ");
                    int numOfProducts = scanner.nextInt();
                    scanner.nextLine();

                    for (int i = 0; i < numOfProducts; i++) {
                        System.out.print("Enter Product ID for product " + (i + 1) + ": ");
                        int productId = scanner.nextInt();
                        products.add(new Product(productId, "", "", 0.0, 0, ""));
                    }

                    User orderUser = new User(userId, "", "", "User");
                    orderRepo.createOrder(orderUser, products);
                    break;

                case 4:
                    // Cancel Order
                    System.out.print("Enter User ID: ");
                    userId = scanner.nextInt();
                    System.out.print("Enter Order ID: ");
                    int orderId = scanner.nextInt();
                    orderRepo.cancelOrder(userId, orderId);
                    break;

                case 5:
                    // Get All Products
                    List<Product> allProducts = orderRepo.getAllProducts();
                    for (Product p : allProducts) {
                        System.out.println(p.getProductId() + ": " + p.getProductName() + " - " + p.getDescription() + " - $" + p.getPrice());
                    }
                    break;

                case 6:
                    // Get Orders by User
                    System.out.print("Enter User ID: ");
                    userId = scanner.nextInt();
                    User user = new User(userId, "", "", "User");
                    List<Product> userOrders = orderRepo.getOrderByUser(user);
                    for (Product p : userOrders) {
                        System.out.println(p.getProductId() + ": " + p.getProductName() + " - " + p.getDescription() + " - $" + p.getPrice());
                    }
                    break;

                case 7:
                    // Exit
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}