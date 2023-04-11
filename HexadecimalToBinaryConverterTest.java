public class HexadecimalToBinaryConverterTest {
    public static void main(String[] args) {
        // Example hexadecimal string with 3 lines
        String hexString = "3c4d7e4d4e4d7e4d4e4d7e4d4e4d7e4d\n" +
                "7e4d4e4d7e4d4e4d7e4d4e4d7e4d4e4d\n" +
                "2d2c2f2c2f2c2f2c2f2c2f2c2f2c2f2c\n" +
                "2f2c2f2c2f2c2f2c2f2c2f2c2f2c2f2c";

        // Convert hexadecimal string to binary string
        String binaryString = HexadecimalToBinary.convert(hexString);

        // Print the result
        System.out.println("Hexadecimal string:\n" + hexString);
        System.out.println("Binary string:\n" + binaryString);
    }
}
