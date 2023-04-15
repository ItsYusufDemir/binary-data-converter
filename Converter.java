/* Author: @ItsYusufDemir
 * Date: 11.04.2023 23:05
 *
 * Description:
 */

public class Converter {
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

    public static int[] convertBinaryToDecimal(String binaryString) {//binary değerleri decimal'a çevir ve int array olarak return et
        String[] binaryArray = binaryString.split("\n");//her satırı bir eleman olarak ayır
        int[] decimalArray = new int[binaryArray.length];//decimal değerler için yeterli boyutta array oluştur
        for (int i = 0; i < binaryArray.length; i++) {
            decimalArray[i] = Integer.parseInt(binaryArray[i], 2);//dönüştürdüğün değerleri array elemanlarına ata
            System.out.println(decimalArray[i]);
        }
        return decimalArray;//arrayi geri döndür
    }


    public static void main(String[] args) {
        String binaryValue = hexToBinary("f0 90 01 40 03 00 ff ff 00 00 e0 7f\n" +
                "00 00 e0 ff 00 00 00 00 00 00 00 80\n" +
                "00 00 18 80 00 00 00 00 00 00 00 00");
        System.out.println(binaryValue);
        convertBinaryToDecimal("111100001011111111000011111\n" +
                "0000000000000000111000001000\n" +
                "00000000000000000000000000");

    }


}
