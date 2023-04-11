/* Author: @ItsYusufDemir
 * Date: 11.04.2023 23:05
 *
 * Description:
 */

public class Converter {

    //GLOBAL VARIABLES
    static int dataType;  //1: unsigned int, 2: signed int, 3: floating number
    static int sizeOfData; // 1, 2, 3 or 4 bytes.
    static boolean isLittleEndian;
    static String currentLine;


    public static void main(String args[]){

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



}
