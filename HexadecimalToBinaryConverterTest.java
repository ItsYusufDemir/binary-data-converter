public class HexadecimalToBinaryConverterTest {
    public static void main(String[] args) {
        // Example hexadecimal string with 3 lines
        String hexString = "ffff0000";

        // Convert hexadecimal string to binary string
        String binaryString = HexadecimalToBinary.convert(hexString);

        // Print the result
        System.out.println("Hexadecimal string:\n" + hexString);
        System.out.println("Binary string:\n" + binaryString);
    }
}
