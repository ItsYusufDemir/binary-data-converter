public class HexadecimalToBinary {
    private static String convertLine(String hexLine) {
        StringBuilder binaryStringBuilder = new StringBuilder();
        // Iterate over each character of the hexadecimal line and convert it to binary
        for (int i = 0; i < hexLine.length(); i++) {
            char hexChar = hexLine.charAt(i);
            int hexValue = Character.digit(hexChar, 16);
            String binaryString = String.format("%4s", Integer.toBinaryString(hexValue)).replace(' ', '0');
            binaryStringBuilder.append(binaryString);
        }
        return binaryStringBuilder.toString();
    }

    // Converts a multiline hexadecimal string to binary string
    public static String convert(String hexString) {
        StringBuilder binaryStringBuilder = new StringBuilder();
        // Split the hexadecimal string into lines
        String[] hexLines = hexString.split("\n");
        // Iterate over each line of the hexadecimal string and convert it to binary
        for (String hexLine : hexLines) {
            String binaryLine = convertLine(hexLine);
            binaryStringBuilder.append(binaryLine);
            binaryStringBuilder.append(System.lineSeparator());
        }
        return binaryStringBuilder.toString();
    }
}