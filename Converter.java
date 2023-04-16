/* Author: @ItsYusufDemir
 * Date: 11.04.2023 23:05
 *
 * Description:
 */
import java.io.*;
import java.util.*;

public class Converter {

    public Converter() throws FileNotFoundException {
    }

    //ENUMERATORS
    enum DataType{ //3 kind of data can be read: unsigned int, signed int, floating point number
        UNSIGNED,
        SIGNED,
        FLOAT
    }

    enum FloatingTYpe{ //There are three kinds of floating points: denormalized, normalized, special
        NORMALIZED,
        DENORMALIZED,
        SPECIAL
    }

    //GLOBAL VARIABLES
    static DataType dataType;  //1: unsigned int, 2: signed int, 3: floating number
    static int sizeOfData; // 1, 2, 3 or 4 bytes.
    static boolean isLittleEndian;
    static String currentLine;

    public static void main(String args[]) {

        takeInputs();  //It will ask for the data type, data size and byte ordering from the user and update the global variables

        while(currentLine != null){

            currentLine = hexToBinary(currentLine);

            if(sizeOfData == 1) { // We have 12 numbers in each line

            }
            else if(sizeOfData == 2){ //We have 6 numbers in each line


            }
            else if(sizeOfData == 3) { //We have 4 numbers in each line

            }
            else{ //We have 3 numbers in each line

            }


        }








    }

    public static void takeInputs() {

        Scanner consoleInput = new Scanner(System.in);

        System.out.println("Enter 1 if it is little indian or 0 if not:");
        isLittleEndian = consoleInput.nextBoolean();

        System.out.println("Enter 1 if it is unsigned int, 2 if it is signed int, 3 if it is floating point number:");
        int dataTypesWithNums = consoleInput.nextInt();
        if (dataTypesWithNums == 1) {
            dataType = DataType.UNSIGNED;
        }
        else if (dataTypesWithNums == 2) {
            dataType = DataType.SIGNED;
        }
        else if (dataTypesWithNums == 3) {
            dataType = DataType.FLOAT;
        }

        System.out.println("Enter the data type byte size:");
        sizeOfData = consoleInput.nextInt();

    }

    /* DECODING FLOATING POINT NUMBERS
     *
     * Floating point numbers are decoded here, their decimal value is returned.
     */
    public static String decodeFloat(String str){  //Input will be in binary and byte order is considered before coming here.

        if(str.length() != sizeOfData * 8){  //If the data size is 3 bytes, then str must be length of 8*3 = 24 bits.
            System.out.println("Binary input size should be same as data size in global!");
            System.exit(0);
        }


        int signBit = str.charAt(0) - '0'; //0 for positive, 1 for negative numbers


        String fraction = ""; //Fraction as string
        String exp = ""; //exp as string

        /* Taking the fraction and exp from the str as substrings
         *
         */
        if(sizeOfData == 1){ //If the size is 1 byte: 1 sign, 4 exp, 3 fraction
            fraction = str.substring(5);
            exp = str.substring(1,5);
        }
        else if(sizeOfData == 2){ //If the size is 2 bytes: 1 sign, 6 exp, 9 fraction
            fraction = str.substring(7);
            exp = str.substring(1,7);
        }
        else if(sizeOfData == 3){ //If the size is 3 bytes: 1 sign, 8 exp, 15 fraction
            fraction = str.substring(9);
            fraction = roundFraction(fraction); //Since the fraction is grater than 13 digits, round it.
            exp = str.substring(1,9);

        }
        else if(sizeOfData == 4) {//If the size is 2 bytes: 1 sign, 10 exp, 21 fraction
            fraction = str.substring(11);
            fraction = roundFraction(fraction); //Since the fraction is grater than 13 digits, round it.
            exp = str.substring(1, 11);
        }
        else{ //Otherwise, give error
            System.out.println("Data size must be 1, 2, 3 or 4 bytes!");
            System.exit(0);
        }



        int fractionInt = unsignedToDecimal(fraction); //Decimal value of fraction
        int expInt = unsignedToDecimal(exp); //Decimal value of exp
        int bias = (int) (Math.pow(2, exp.length() - 1)) - 1; //Bias is 2^(k-1) - 1     k is exp.lenght()

        if(findTypeOfFloat(exp) == FloatingTYpe.SPECIAL) { //Special floating point number, exp = 111....11

            if(fractionInt != 0){  //If the fraction is not all zero
                return "NaN";
            }
            else{
                if(signBit == 0) //According to the sign bit, return correct infinity
                    return "∞";
                else
                    return "-∞";
            }

        }
        else if(findTypeOfFloat(exp) == FloatingTYpe.NORMALIZED){ //Normalized floating point number

            int E = expInt - bias;
            double mantissa = 1 + fractionInt / Math.pow(2, 13); //To find .001011010110, first find its unsgined value,
            // then divide to 2^13. We add 1 to mantissa because in normalized form, mantissa is in the form like 1.101001....

            return (Math.pow(-1, signBit) * mantissa * Math.pow(2, E)) + ""; //Decimal value is: (-1)^s x M x 2^E
        }
        else { //Denormalized floating point number

            int E = 1 - bias;
            double mantissa = 0 + fractionInt / Math.pow(2, 13); //To find .001011010110, first find its unsgined value,
            //then divide to 2^13. We do not add 1 here because in denormalized for mantissa is like 0.01010....

            return (Math.pow(-1, signBit) * mantissa * Math.pow(2, E)) + ""; //Decimal value is: (-1)^s x M x 2^E
        }


    }



    public static FloatingTYpe findTypeOfFloat(String exp){




        return FloatingTYpe.NORMALIZED;
    }






}
