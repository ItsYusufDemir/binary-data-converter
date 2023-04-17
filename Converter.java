/* Author: @ItsYusufDemir
 * Date: 11.04.2023 23:05
 *
 * Description:
 */

import java.io.*;
import java.util.ArrayList;
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

    enum FloatingType { //There are three kinds of floating points: denormalized, normalized, special
        NORMALIZED,
        DENORMALIZED,
        SPECIAL
    }

    //GLOBAL VARIABLES
    static DataType dataType;  //1: unsigned int, 2: signed int, 3: floating number
    static int sizeOfData; // 1, 2, 3 or 4 bytes.
    static boolean isLittleEndian;
    static String currentLine;
    static boolean carryOut;

    private static FileReader inputFileReader;
    private static FileWriter outputFileWriter;
    private static BufferedReader bufferedReader;

    private static final String OUTPUT_FILE_PATH = "output.txt";
    private static String INPUT_FILE_PATH = "input.txt";


    public static void main(String args[]) throws IOException {


        try {
            takeInputs();  //It will ask for the data type, data size and byte ordering from the user and update the global variables
            openFiles();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }

        bufferedReader = new BufferedReader(inputFileReader);

        readLine();

        while (currentLine != null && currentLine.length() == 35) {
            extractNumbersAndPrint();

            //currentLine = hexToBinary(currentLine);
            readLine();
            System.out.println();
            outputFileWriter.write('\n');
        }

        inputFileReader.close();
        outputFileWriter.close();

    }

    private static void extractNumbersAndPrint() throws IOException {
        ArrayList<String> numbers = new ArrayList<>();

        int startingIndex = 0;
        int endingIndex = 0;

        if (sizeOfData == 1)
            endingIndex = 1;
        else if (sizeOfData == 2)
            endingIndex = 4;
        else if (sizeOfData == 3)
            endingIndex = 7;
        else
            endingIndex = 10;


        while (endingIndex <= 34) {

            String currentNumber = currentLine.substring(startingIndex, endingIndex + 1);
            currentNumber = byteOrdering(currentNumber); //If is little endian, do some process.
            currentNumber = deleteSpaces(currentNumber);
            currentNumber = hexToBinary(currentNumber);

            if (dataType == DataType.FLOAT) {
                numbers.add(decodeFloat(currentNumber));
            } else if (dataType == DataType.SIGNED) {
                numbers.add(signedToDecimal(currentNumber) + "");
            } else {
                numbers.add(unsignedToDecimal(currentNumber) + "");
            }


            if (sizeOfData == 1) {
                startingIndex += 3;
                endingIndex += 3;
            } else if (sizeOfData == 2) {
                startingIndex += 6;
                endingIndex += 6;
            } else if (sizeOfData == 3) {
                startingIndex += 9;
                endingIndex += 9;
            } else {
                startingIndex += 12;
                endingIndex += 12;
            }

        }

        for (int i = 0; i < numbers.size(); i++) {
            printOutput(numbers.get(i), outputFileWriter);
        }
    }


    private static void openFiles() throws IOException {
        outputFileWriter = new FileWriter(OUTPUT_FILE_PATH);
        inputFileReader = new FileReader(INPUT_FILE_PATH);

    }

    public static void takeInputs() {

        Scanner consoleInput = new Scanner(System.in);

        System.out.println("Enter the input file name: ");
        INPUT_FILE_PATH = consoleInput.next();



        System.out.println("Byte ordering:"); //l for little endian, b for big endian.
        char byteOrdering = consoleInput.next().charAt(0);
        if(byteOrdering == 'l')
            isLittleEndian = true;
        else if (byteOrdering == 'b')
            isLittleEndian = false;
        else{
            System.out.println("Invalid byte ordering! Enter l for Little Endian b for Big Endian.");
            System.exit(0);
        }

        System.out.println("Data type: ");
        String dataTypeString = consoleInput.next();
        if (dataTypeString.equals("unsigned")) {
            dataType = DataType.UNSIGNED;
        } else if (dataTypeString.equals("int")) {
            dataType = DataType.SIGNED;
        } else if (dataTypeString.equals("float")) {
            dataType = DataType.FLOAT;
        }
        else {
            System.out.println("Invalid data type! Enter float, int or unsigned.");
            System.exit(0);
        }

        System.out.println("Data type size: ");
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


        int fractionInt = (int) (unsignedToDecimal(fraction)); //Decimal value of fraction
        int expInt = (int) unsignedToDecimal(exp); //Decimal value of exp
        int bias = (int) (Math.pow(2, exp.length() - 1)) - 1; //Bias is 2^(k-1) - 1     k is exp.lenght()

        if (findTypeOfFloat(exp) == FloatingType.SPECIAL) { //Special floating point number, exp = 111....11

            if (fractionInt != 0) {  //If the fraction is not all zero
                return "NaN";
            } else {
                if (signBit == 0) //According to the sign bit, return correct infinity
                    return "∞";
                else
                    return "-∞";
            }

        } else if (findTypeOfFloat(exp) == FloatingType.NORMALIZED) { //Normalized floating point number

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
        int oneInt = Integer.parseInt(one,2); //creating a binary 1
        String first13 = str.substring(0,13); //getting initial 13 character fraction
        long first13Long = Long.parseLong(first13,2); //creating an initial binary first13
        String remainder = str.substring(13); //remaining part that exceeds 13 length
        long remainderLong = Long.parseLong(remainder); //creating the remaining part as decimal
        int length = remainder.length(); //remaining part length
        long sum = 0; //will be updated

        if (remainderLong < (10^(length-1))) { //it rounds to low

            return first13;
        }
        else if (remainderLong > (10^(length-1))) { //it rounds to up

            if (first13.equals("1111111111111")) { //this is an exception that should be handled first

                carryOut = true;
                return "0000000000000";
            }

            else { //binary addition to round up
                sum = first13Long + oneInt;
                return Long.toBinaryString(sum);
            }
        }
        else if (remainderLong == (10^(length-1))) { // if remaining part is halfway, fraction is rounded to even

            if (first13.equals("1111111111111")) { //this is an exception that should be handled first

                carryOut = true;
                return "0000000000000";
            }

            else if (str.charAt(12) == '1') { //this rounds up the fraction because the last digit can't be odd

                sum = first13Long + oneInt;
                return Long.toBinaryString(sum);
            } else {

                return first13;
            }
        } else return first13;
    }


    public static FloatingType findTypeOfFloat(String exp) {

        boolean isThereZero = false;
        boolean isThereOne = false;

        for (int i = 0; i < exp.length(); i++) {

            if (exp.charAt(i) == '0')
                isThereZero = true;
            else if (exp.charAt(i) == '1')
                isThereOne = true;
        }

        if (isThereOne == true && isThereZero == false)
            return FloatingType.SPECIAL;
        else if (isThereOne == false && isThereZero == true)
            return FloatingType.DENORMALIZED;
        else
            return FloatingType.NORMALIZED;
    }

    // This method takes a hexadecimal string as input and converts it to its equivalent binary value.
    // The method returns the binary value as a string.
    public static String hexToBinary(String hexString) {
        hexString = hexString.replaceAll(" ", ""); // remove any spaces from the input string
        String binaryString = ""; // initialize the output binary string
        String[] hexArray = hexString.split("\\r?\\n"); // split the input string into an array of strings, each containing a hexadecimal value
        for (String hex : hexArray) {
            byte[] bytes = new byte[hex.length() / 2]; // create a byte array to hold the hexadecimal value
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16); // convert each pair of hexadecimal digits to a byte value
            }
            for (byte b : bytes) {
                binaryString += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'); // convert each byte value to an 8-bit binary string and append it to the output binary string
            }

        }
        return binaryString; // return the final output binary string
    }

    // This method takes a binary string as input and converts it to its equivalent decimal value.
    // The method returns the decimal value as an integer.
    public static long unsignedToDecimal(String binaryString) {
        long decimal = 0; // initialize decimal value to 0
        int powerIndex = 0; // initialize power index to 0

        // loop through the binary string from right to left
        for (int i = binaryString.length() - 1; i >= 0; i--) {
            decimal += (binaryString.charAt(i) - '0') * Math.pow(2, powerIndex); // add the decimal value of each binary digit to the running total
            powerIndex++; // increment the power index
        }
        return decimal; // return the final decimal value
    }

    // This method takes a binary string as input and converts it to its equivalent signed decimal value.
    // The method returns the decimal value as an integer.
    public static int signedToDecimal(String binary) {
        int n = binary.length(); // get the length of the binary string
        int signBit = 0; // initialize the sign bit to 0
        if (binary.charAt(0) == '1') {// If the leftmost bit is 1, then it's a negative number
            signBit = -1; // set the sign bit to -1
            // Flip all the bits in the binary string
            StringBuilder flipped = new StringBuilder(); // create a StringBuilder object to hold the flipped binary string
            for (int i = 0; i < n; i++) {
                if (binary.charAt(i) == '0') {
                    flipped.append('1'); // flip each 0 to 1
                } else {
                    flipped.append('0'); // flip each 1 to 0
                }
            }
            binary = flipped.toString(); // set the binary string to the flipped value
        }
        // Convert the binary string to decimal
        int decimal = 0; // initialize the decimal value to 0
        for (int i = n - 1; i >= 0; i--) {
            if (binary.charAt(i) == '1') {
                decimal += Math.pow(2, n - i - 1); // add the decimal value of each binary digit to the running total
            }
        }
        // Add the sign bit if necessary
        if (signBit == -1) {
            decimal = -1 * (decimal + 1); // if the sign bit is negative, convert the decimal value to its two's complement form
        }
        return decimal; // return the final decimal value
    }


    public static void readLine() throws IOException {
        //BufferedReader bufferReader = new BufferedReader(fileReader);
        currentLine = bufferedReader.readLine();
    }

    public static String byteOrdering(String data) {

        if (!isLittleEndian)
            return data;

        if (data.length() == 2) {
            return data;
        } else if (data.length() == 5) {
            return data.substring(3, 5) + " " + data.substring(0, 2);
        } else if (data.length() == 8) {
            return data.substring(6, 8) + " " + data.substring(3, 5) + " " + data.substring(0, 2);
        } else if (data.length() == 11) {
            return data.substring(9, 11) + " " + data.substring(6, 8) + " " + data.substring(3, 5) + " " + data.substring(0, 2);
        }
        return null;
    }


    public static String deleteSpaces(String str) {  //This function deletes the whitespaces in a string.
        String[] bytes = str.split("\\s+");

        String newStr = "";

        for (int i = 0; i < bytes.length; i++) {
            newStr += bytes[i];
        }

        return newStr;
    }

    public static void printOutput(String number, FileWriter file) throws IOException {

        String output = "";

        if(number.equals("0.0") || number.equals("-0.0"))
            output = "0";
        else if(doesContainDot(number)){ //If the number is a floating point number

            int dotIndex = 0;
            int eIndex = -1;
            for(int i = 0; i < number.length(); i++) {  //Find the index of dot character
                if (number.charAt(i) == '.') {
                    dotIndex = i;
                }


                if(number.charAt(i) == 'e' || number.charAt(i) == 'E')
                    eIndex = i;
            }

            for(int i = 0; i <= dotIndex; i++){ //Take the digits until dot (including dot) ex: 2345.
                output += number.charAt(i);
            }

            int counter = 0;
            for(int i = dotIndex + 1; i < number.length() && (counter < 5);i++){ //Take the digits after the dot until 5 digits of precision
                output += number.charAt(i);
                counter++;
            }


            for(int i = eIndex; i < number.length() && eIndex != -1; i++){ //Take the remaining ex: e-20
                if(i == eIndex)
                    output += 'e';
                else
                    output += number.charAt(i);
            }


        }
        else
            output = number;



        System.out.print(output + " ");
        file.write(output + " ");
    }


    public static boolean doesContainDot(String number){
        for(int i = 0; i < number.length(); i++){  //If the number contains a dot, return true
            if(number.charAt(i) == '.')
                return true;
        }

        return false; //Else, return false
    }

}