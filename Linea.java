import java.util.HexFormat;

public class Linea {
    public int numLinea;
    public int direccion = -1;
    public String operando = "";
    public String hexInst = "";
    public String hexOp = "";
    public String etiqueta = "";
    public String lineaOriginal = "";
    public Tarea pendiente = null;
    enum Tarea{
        SUST_ETIQUETA,
        CALC_SALTO
    }

    public String getHexadecimal(){
        return hexInst + hexOp;
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
        for (int i = cadena.length(); i < longitudFinal; i++) {
            if (izquierda)
                cadena = comodin + cadena;
            else
                cadena += comodin;
        }
        cadena = cadena.substring(cadena.length() - longitudFinal, cadena.length());
        return cadena;
    }

    public static String decToHex(int num, int bytes) {
        String hex = Integer.toHexString(num);
        if (bytes == 1){//} && hex.length() < 2){
            hex = CompletarCadena(hex, 2, '0', true);
        }
        else if (bytes == 2){
            hex = CompletarCadena(hex, 4, '0', true);
        }
        return hex.toUpperCase();
    }

    public String toHTML() {
        String cadena = numLinea + ":";
        if (direccion == -1){
            cadena += "Vacío:";
        }
        else {
            cadena += " " + Integer.toHexString(direccion) + " (<span style='color:red'>" + hexInst + "</span>" + "<span style='color:blue'>" + hexOp + "</span>):";
        }
        cadena += lineaOriginal;
        return cadena;
    }
    
    @Override
    public String toString() {
        String cadena = numLinea + ":";
        if (direccion == -1){
            cadena += "\t\t\t\t\t:";
        }
        else {

            String espacio = "\t" + (getHexadecimal().length() > 4 ? "" : "\t");
            cadena += " " + Integer.toHexString(direccion) + " (" + getHexadecimal() + ")" + espacio + ":";
        }
        cadena += lineaOriginal;
        return cadena;
    }
}
