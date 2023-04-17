/* Author: @ItsYusufDemir
 * Date: 11.04.2023 23:05
 *
 * Description:
 */

import java.io.*;
import java.util.Scanner;

public class Converter {

    public Converter() throws FileNotFoundException {
    }

    //ENUMERATORS
    enum DataType { //3 kind of data can be read: unsigned int, signed int, floating point number
        UNSIGNED,
        SIGNED,
        FLOAT
    }

    enum FloatingTYpe { //There are three kinds of floating points: denormalized, normalized, special
        NORMALIZED,
        DENORMALIZED,
        SPECIAL
    }

    //GLOBAL VARIABLES
    static DataType dataType;  //1: unsigned int, 2: signed int, 3: floating number
    static int sizeOfData; // 1, 2, 3 or 4 bytes.
    static boolean isLittleEndian;
    static String currentLine;

    private static FileReader inputFileReader;
    private static FileWriter outputFileWriter;

    private static final String OUTPUT_FILE_PATH = "output.txt";
    private static final String INPUT_FILE_PATH = "input.txt";


    public static void main(String args[]) throws IOException {

        try {
            takeInputs();  //It will ask for the data type, data size and byte ordering from the user and update the global variables
            openFiles();
            readLine(inputFileReader);

            while (currentLine != null && currentLine.length() == 35) {
                readLinesExtractNumbersAndPrint();

                currentLine = hexToBinary(currentLine);
                readLine(inputFileReader);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            inputFileReader.close();
            outputFileWriter.close();
        }
    }

    private static void readLinesExtractNumbersAndPrint() {
        String[] numbers;
        if (sizeOfData == 1) { // We have 12 numbers in each line
            numbers = currentLine.split(" ");
        } else if (sizeOfData == 2) { //We have 6 numbers in each line
            numbers = new String[]{currentLine.substring(0, 5),
                    currentLine.substring(6, 11),
                    currentLine.substring(12, 17),
                    currentLine.substring(18, 23),
                    currentLine.substring(24, 29),
                    currentLine.substring(30, 35)
            };
        } else if (sizeOfData == 3) { //We have 4 numbers in each line
            numbers = new String[]{currentLine.substring(0, 8),
                    currentLine.substring(9, 17),
                    currentLine.substring(18, 26),
                    currentLine.substring(27, 35)
            };
        } else { //We have 3 numbers in each line
            numbers = new String[]{currentLine.substring(0, 11),
                    currentLine.substring(12, 23),
                    currentLine.substring(24, 35)
            };
        }
        for (int i = 0; i < numbers.length; i++) {
            System.out.println(i + ". number: " + numbers[i]);
        }
    }

    private static void openFiles() throws IOException {
        outputFileWriter = new FileWriter(OUTPUT_FILE_PATH);
        inputFileReader = new FileReader(INPUT_FILE_PATH);

    }

    public static void takeInputs() {

        Scanner consoleInput = new Scanner(System.in);

        System.out.println("Enter true if it is little indian or false if not:");
        isLittleEndian = consoleInput.nextBoolean();

        System.out.println("Enter 1 if it is unsigned int, 2 if it is signed int, 3 if it is floating point number:");
        int dataTypesWithNums = consoleInput.nextInt();
        if (dataTypesWithNums == 1) {
            dataType = DataType.UNSIGNED;
        } else if (dataTypesWithNums == 2) {
            dataType = DataType.SIGNED;
        } else {
            dataType = DataType.FLOAT;
        }

        System.out.println("Enter the data type byte size:");
        sizeOfData = consoleInput.nextInt();

    }

    /* DECODING FLOATING POINT NUMBERS
     *
     * Floating point numbers are decoded here, their decimal value is returned.
     */
    public static String decodeFloat(String str) {  //Input will be in binary and byte order is considered before coming here.

        if (str.length() != sizeOfData * 8) {  //If the data size is 3 bytes, then str must be length of 8*3 = 24 bits.
            System.out.println("Binary input size should be same as data size in global!");
            System.exit(0);
        }


        int signBit = str.charAt(0) - '0'; //0 for positive, 1 for negative numbers


        String fraction = ""; //Fraction as string
        String exp = ""; //exp as string

        /* Taking the fraction and exp from the str as substrings
         *
         */
        if (sizeOfData == 1) { //If the size is 1 byte: 1 sign, 4 exp, 3 fraction
            fraction = str.substring(5);
            exp = str.substring(1, 5);
        } else if (sizeOfData == 2) { //If the size is 2 bytes: 1 sign, 6 exp, 9 fraction
            fraction = str.substring(7);
            exp = str.substring(1, 7);
        } else if (sizeOfData == 3) { //If the size is 3 bytes: 1 sign, 8 exp, 15 fraction
            fraction = str.substring(9);
            fraction = roundFraction(fraction); //Since the fraction is grater than 13 digits, round it.
            exp = str.substring(1, 9);

        } else if (sizeOfData == 4) {//If the size is 2 bytes: 1 sign, 10 exp, 21 fraction
            fraction = str.substring(11);
            fraction = roundFraction(fraction); //Since the fraction is grater than 13 digits, round it.
            exp = str.substring(1, 11);
        } else { //Otherwise, give error
            System.out.println("Data size must be 1, 2, 3 or 4 bytes!");
            System.exit(0);
        }


        int fractionInt = unsignedToDecimal(fraction); //Decimal value of fraction
        int expInt = unsignedToDecimal(exp); //Decimal value of exp
        int bias = (int) (Math.pow(2, exp.length() - 1)) - 1; //Bias is 2^(k-1) - 1     k is exp.lenght()

        if (findTypeOfFloat(exp) == FloatingTYpe.SPECIAL) { //Special floating point number, exp = 111....11

            if (fractionInt != 0) {  //If the fraction is not all zero
                return "NaN";
            } else {
                if (signBit == 0) //According to the sign bit, return correct infinity
                    return "∞";
                else
                    return "-∞";
            }

        } else if (findTypeOfFloat(exp) == FloatingTYpe.NORMALIZED) { //Normalized floating point number

            int E = expInt - bias;
            double mantissa = 1 + fractionInt / Math.pow(2, 13); //To find .001011010110, first find its unsgined value,
            // then divide to 2^13. We add 1 to mantissa because in normalized form, mantissa is in the form like 1.101001....

            return (Math.pow(-1, signBit) * mantissa * Math.pow(2, E)) + ""; //Decimal value is: (-1)^s x M x 2^E
        } else { //Denormalized floating point number

            int E = 1 - bias;
            double mantissa = 0 + fractionInt / Math.pow(2, 13); //To find .001011010110, first find its unsgined value,
            //then divide to 2^13. We do not add 1 here because in denormalized for mantissa is like 0.01010....

            return (Math.pow(-1, signBit) * mantissa * Math.pow(2, E)) + ""; //Decimal value is: (-1)^s x M x 2^E
        }


    }

    public static String roundFraction(String str) {

        String one = "1";
        int oneInt = Integer.parseInt(one,2);
        String first13 = str.substring(0,13);
        long first13Long = Long.parseLong(first13,2);
        String remainder = str.substring(13);
        long remainderLong = Long.parseLong(remainder);
        int length = remainder.length();
        long sum = 0;

        if (remainderLong < (10^(length-1))) {

            return first13;
        }
        else if (remainderLong > (10^(length-1))) {

            sum = first13Long + oneInt;
            return Long.toBinaryString(sum);
        }
        else if (remainderLong == (10^(length-1))) {

            if (str.charAt(12) == '1') {

                sum = first13Long + oneInt;
                return Long.toBinaryString(sum);
            }
            else {

                return first13;
            }
        }
        else return first13;
    }




    public static FloatingTYpe findTypeOfFloat(String exp) {


        return FloatingTYpe.NORMALIZED;
    }

    public static String hexToBinary(String hexString) {
        //
        hexString = hexString.replaceAll(" ", "");
        // Her satırı tek tek binary'ye çevir
        String binaryString = "";
        String[] hexArray = hexString.split("\\r?\\n");//her satırı arrayin bir elemanı olarak al
        for (String hex : hexArray) {//for each yapısı ile array elemanlarını tek tek gez hex değerleri byte'a dönüştür
            byte[] bytes = new byte[hex.length() / 2];//byte array oluştur
            for (int i = 0; i < bytes.length; i++) {//bütün elemanları gez
                bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);//her array elemanına byte'a dönüştürülmüş hex değerlerini ekle
            }
            for (byte b : bytes) {//for each yapııs ile array elemanlarını tek tek gez ve byte değerleri bit'e çevir
                binaryString += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }
            binaryString += "\n";
        }
        return binaryString;
    }

    public static int unsignedToDecimal(String binaryString) {//binary değerleri decimal'a çevir ve int array olarak return et
        return Integer.parseInt(binaryString, 2);
    }

    public static int signedToDecimal(String binary) {
        int n = binary.length();
        int signBit = 0;
        if (binary.charAt(0) == '1') {
            // If the leftmost bit is 1, then it's a negative number
            signBit = -1;
            // Flip all the bits in the binary string
            StringBuilder flipped = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (binary.charAt(i) == '0') {
                    flipped.append('1');
                } else {
                    flipped.append('0');
                }
            }
            binary = flipped.toString();
        }
        // Convert the binary string to decimal
        int decimal = 0;
        for (int i = n - 1; i >= 0; i--) {
            if (binary.charAt(i) == '1') {
                decimal += Math.pow(2, n - i - 1);
            }
        }
        // Add the sign bit if necessary
        if (signBit == -1) {
            decimal = -1 * (decimal + 1);
        }
        return decimal;
    }


    public static void readLine(FileReader fileReader) throws IOException {
        BufferedReader bufferReader = new BufferedReader(fileReader);
        currentLine = bufferReader.readLine();
    }

}
