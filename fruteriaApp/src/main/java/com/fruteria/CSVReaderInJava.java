package com.fruteria;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CSVReaderInJava {

    public static void main(String... args) {
        List<Product> products = readProductsFile("products.txt");
        List<Purchase> purchases = readPurchaseFile("purchase.txt");

        double subtotal = 0;
        BigDecimal total = BigDecimal.valueOf(0);

        System.out.println("PRODUCT \t QUANTITY \t SUBTOTAL " );

        for (Product p : products) {

            int cantidad = findByProduct(purchases,p.getName()).getQuantity();

            subtotal = p.getPrice() * cantidad;
            System.out.println(p.getName() + "\t \t " + cantidad + "\t\t\t " + subtotal );

            BigDecimal totaldouble = new BigDecimal(subtotal);

            total = total.add(totaldouble);

        }

        double descuentoManzanas = applesOffer(purchases, products);
        double descuentoNaranjas = orangesOffer(purchases, products);
        double descuentoPeras = pearOffer(purchases, products);
        double totaldescuentos = (descuentoManzanas + descuentoNaranjas + descuentoPeras);

        System.out.println("\nApple Offer : " + descuentoManzanas);
        System.out.println("Orange Offer :" + descuentoNaranjas);
        System.out.println("Pear Offer :" + descuentoPeras);

        total = total.setScale(2, RoundingMode.FLOOR);

        System.out.println("\nCalculing Total :  Total " + total  + " - total Offers " + totaldescuentos);

        BigDecimal totaldescuentosBD = new BigDecimal( totaldescuentos);

        totaldescuentosBD = totaldescuentosBD.setScale(2, RoundingMode.CEILING);

        BigDecimal totalDecimal = total.subtract(totaldescuentosBD);

        System.out.println("\nTOTAL : " + totalDecimal.setScale(2, RoundingMode.CEILING) );

    }

    private static List<Product> readProductsFile(String fileName) {
        List<Product> products = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {

            String line = br.readLine();
            line = br.readLine();

            while (line != null) {

                String[] attributes = line.split(",");

                Product product = createProduct(attributes);

                products.add(product);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return products;
    }

    private static List<Purchase> readPurchaseFile(String fileName) {
        List<Purchase> purchases = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {

            String line = br.readLine();
            line = br.readLine();

            while (line != null) {

                String[] attributes = line.split(",");

                Purchase purchase = createPurchase(attributes);

                purchases.add(purchase);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return purchases;
    }

    public static Purchase findByProduct(Collection<Purchase> listCcompra, String producto){
        return listCcompra.stream().filter(purchase -> producto.equals(purchase.getProduct())).findFirst().orElse(null);
    }

    public static Product findByName(Collection<Product> listProduct, String nombreproducto){
        return listProduct.stream().filter(product -> nombreproducto.equals(product.getName())).findFirst().orElse(null);
    }


    public static double applesOffer(List<Purchase> listPurchase, List<Product> listProduct){

        int cantidad = findByProduct(listPurchase,"Apple").getQuantity();

        int oferta = (int) Math.floor(cantidad/3);

        double descuento = oferta * findByName(listProduct,"Apple").getPrice();

        return descuento;
    }

    public static double orangesOffer(List<Purchase> listPurchase, List<Product> listProduct){

        int cantidad = findByProduct(listPurchase,"Pear").getQuantity();

        int oferta = (int) Math.floor(cantidad/2);

        double descuento = oferta * findByName(listProduct,"Orange").getPrice();

        return descuento;
    }

    public static double pearOffer(List<Purchase> listPurchase, List<Product> listProduct){

        int cantidad = findByProduct(listPurchase,"Pear").getQuantity();

        double subtotal = cantidad * findByName(listProduct,"Pear").getPrice();

        int oferta = (int) Math.floor(subtotal/4);

        double descuento = oferta;

        return descuento;
    }

    private static Product createProduct(String[] metadata) {
        String name = metadata[0];
        double price = Double.parseDouble(metadata[1].trim());

        return new Product(name, price);
    }

    private static Purchase createPurchase(String[] metadata) {
        String producto = metadata[0];
        int cantidad = Integer.parseInt(metadata[1].trim());

        return new Purchase(producto, cantidad);
    }

}

