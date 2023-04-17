public class ByteOrdering {

    public static void main(String[] args) {
        System.out.println(byteOrdering("f0").equals("f0"));
        System.out.println(byteOrdering("f0 90").equals("90 f0"));
        System.out.println(byteOrdering("f0 90 01").equals("01 90 f0"));
        System.out.println(byteOrdering("f0 90 01 40").equals("40 01 90 f0"));
    }


    public static String byteOrdering(String data) {
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

}
