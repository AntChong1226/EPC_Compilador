public class Linea {
    public int numLinea;
    public int direccion = -1;
    public String operando = "";
    public String hexadecimal = "";
    public String lineaOriginal = "";
    public Tarea pendiente = null;
    enum Tarea{
        SUST_ETIQUETA,
        CALC_SALTO
    }

    public static int hexToDec(String num) {
        return Integer.parseInt(num, 16);
    }

    public static String calcularSalto(int inicio, int fin) {
        int salto = fin - inicio;
        String a = Integer.toHexString(salto);
        return (salto < 0 ? a.substring(a.length()-2) : CompletarCadena(a, 2, '0', true)).toUpperCase();
    }

    public static String CompletarCadena(String cadena, int longitudFinal, char comodin, boolean izquierda){
        for (int i = cadena.length(); i <= longitudFinal; i++) {
            if (izquierda)
                cadena = comodin + cadena;
            else
                cadena += comodin;
        }
        return cadena;
    }

    public static String decToHex(int num, int bytes) {
        String hex = Integer.toHexString(num);
        if (bytes == 1 && hex.length() < 2){
            hex = CompletarCadena(hex, 2, '0', true);
        }
        else if (bytes == 2){
            hex = CompletarCadena(hex, 4, '0', true);
        }
        return hex;
    }

    @Override
    public String toString() {
        String cadena = numLinea + ":";
        if (direccion == -1){
            cadena += "\t\t\t:";
        }
        else {
            cadena += " " + Integer.toHexString(direccion) + " (" + hexadecimal + ")\t\t:";
        }
        cadena += lineaOriginal;
        return cadena;
    }
}
