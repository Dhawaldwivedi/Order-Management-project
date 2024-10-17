package Dao;

import entity.*;
import Exception.*;
import util.DBConnUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor implements IOrderManagementRepository {

    @Override
    public void createOrder(User user, List<Product> products) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDBConn("db.properties");
            conn.setAutoCommit(false);  // Start transaction

            // Check if the user exists
            String checkUserQuery = "SELECT * FROM users WHERE userId = ?";
            ps = conn.prepareStatement(checkUserQuery);
            ps.setInt(1, user.getUserId());
            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new UserNotFoundException("User not found in the database.");
            }

            // Create an order
            String insertOrderQuery = "INSERT INTO orders (userId) VALUES (?)";
            ps = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, user.getUserId());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // Insert products into order_items table
            String insertOrderItemsQuery = "INSERT INTO order_items (orderId, productId, quantity) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(insertOrderItemsQuery);

            for (Product product : products) {
                ps.setInt(1, orderId);
                ps.setInt(2, product.getProductId());
                ps.setInt(3, 1); // Default quantity to 1 for simplicity
                ps.addBatch();
            }
            ps.executeBatch();

            conn.commit();  // Commit transaction

            System.out.println("Order created successfully with Order ID: " + orderId);

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DBConnUtil.close(rs, ps, conn);
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnUtil.getDBConn("db.properties");

            // Check if the user exists
            String checkUserQuery = "SELECT * FROM users WHERE userId = ?";
            ps = conn.prepareStatement(checkUserQuery);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new UserNotFoundException("User not found in the database.");
            }

            // Check if the order exists
            String checkOrderQuery = "SELECT * FROM orders WHERE orderId = ? AND userId = ?";
            ps = conn.prepareStatement(checkOrderQuery);
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new OrderNotFoundException("Order not found for the specified user.");
            }

            // Cancel the order
            String deleteOrderQuery = "DELETE FROM orders WHERE orderId = ?";
            ps = conn.prepareStatement(deleteOrderQuery);
            ps.setInt(1, orderId);
            ps.executeUpdate();

            System.out.println("Order with ID " + orderId + " has been canceled.");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnUtil.close(ps, conn);
        }
    }

    @Override
    public void createProduct(User user, Product product) {
        if (!"Admin".equals(user.getRole())) {
            throw new UnauthorizedException("Only admin users can create products.");
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnUtil.getDBConn("db.properties");
            String insertProductQuery = "INSERT INTO products (productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertProductQuery);
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getQuantityInStock());
            ps.setString(5, product.getType());
            ps.executeUpdate();

            System.out.println("Product created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnUtil.close(ps, conn);
        }
    }

    @Override
    public void createUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnUtil.getDBConn("db.properties");
            String insertUserQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(insertUserQuery);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.executeUpdate();

            System.out.println("User created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnUtil.close(ps, conn);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDBConn("db.properties");
            String getAllProductsQuery = "SELECT * FROM products";
            ps = conn.prepareStatement(getAllProductsQuery);
            rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("productId"),
                        rs.getString("productName"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantityInStock"),
                        rs.getString("type")
                );
                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnUtil.close(rs, ps, conn);
        }

        return productList;
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> orderedProducts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnUtil.getDBConn("db.properties");
            String getOrderByUserQuery = "SELECT p.* FROM products p JOIN order_items oi ON p.productId = oi.productId " +
                    "JOIN orders o ON oi.orderId = o.orderId WHERE o.userId = ?";
            ps = conn.prepareStatement(getOrderByUserQuery);
            ps.setInt(1, user.getUserId());
            rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("productId"),
                        rs.getString("productName"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantityInStock"),
                        rs.getString("type")
                );
                orderedProducts.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnUtil.close(rs, ps, conn);
        }

        return orderedProducts;
    }
}