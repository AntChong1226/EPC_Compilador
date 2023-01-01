import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static LinkedHashMap<String, Instruccion> setInstrucciones = new LinkedHashMap<>();
    private static List<String> directivas = Arrays.asList("org", "equ", "fcb", "end");
    private static HashMap<String, Integer> etiquetas = new HashMap<>();
    private static HashMap<String, String> variables = new HashMap<>();
    
    private static void cargarSetInstrucciones() {
        try {
            File archivoSetInstrucciones = new File("setInstruccionesConBytes.txt");
            Scanner lector = new Scanner(archivoSetInstrucciones);
            do {
                String linea = lector.nextLine().replaceAll(" |-", "");
                String[] datos = linea.split(",");
                Instruccion instruccion = new Instruccion(datos[0]);
                String codigo, bytes;
                
                if(datos.length >= 2 && !datos[1].isEmpty()) {
                    codigo = datos[1];
                    bytes = datos[2];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INM, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 4 && !datos[3].isEmpty()) {
                    codigo = datos[3];
                    bytes = datos[4];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.DIR, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 6 && !datos[5].isEmpty()) {
                    codigo = datos[5];
                    bytes = datos[6];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INDX, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 8 && !datos[7].isEmpty()) {
                    codigo = datos[7];
                    bytes = datos[8];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INDY, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 10 && !datos[9].isEmpty()) {
                    codigo = datos[9];
                    bytes = datos[10];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.EXT, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 12 && !datos[11].isEmpty()) {
                    codigo = datos[11];
                    bytes = datos[12];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.INH, new TipoInstruccionDetalle(codigo, bytes));
                }
                
                if(datos.length >= 14 && !datos[13].isEmpty()) {
                    codigo = datos[13];
                    bytes = datos[14];
                    instruccion.codigos.put(Instruccion.TipoInstruccion.REL, new TipoInstruccionDetalle(codigo, bytes));
                }
                setInstrucciones.put(datos[0], instruccion);
            } while (lector.hasNextLine());
            lector.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
    }

    private static boolean esDirectiva(String cadena){
        cadena = cadena.toLowerCase();
        for (String directiva : directivas)
            if (cadena.equals(directiva))
                return true;
        return false;
    }

    public static void compilarArchivo(String rutaArchivo){
        etiquetas = new HashMap<>();
        LinkedList<Linea> lineas = new LinkedList<>();
        try {
            File archivoSetInstrucciones = new File(rutaArchivo);
            Scanner lector = new Scanner(archivoSetInstrucciones);
            boolean estaPegadoAlMargen, finDelPrograma = false, hacerSegundaVuelta = false, aumentarDireccion = true;
            String lineaSinComentarios, mnemonico;
            int contador = 0, direccionActualH = 0, auxPosAsterisco;
            List<String> instruccionesEspeciales = Arrays.asList("bclr", "bset", "brclr", "brset");
            Linea lineaDetalle;
            do {
                lineaDetalle = new Linea();
                lineaDetalle.numLinea = ++contador;
                lineaDetalle.lineaOriginal = lector.nextLine();
                auxPosAsterisco = lineaDetalle.lineaOriginal.indexOf("*");
                lineaSinComentarios = lineaDetalle.lineaOriginal.toLowerCase();
                lineaSinComentarios = auxPosAsterisco != -1 ? lineaSinComentarios.substring(0, auxPosAsterisco) : lineaSinComentarios;
                if (lineaSinComentarios.isBlank()) {
                    lineas.add(lineaDetalle);
                    continue;
                }
                estaPegadoAlMargen = !lineaSinComentarios.substring(0, 1).isBlank();
                if (estaPegadoAlMargen){
                    lineaSinComentarios = lineaSinComentarios.trim();
                    if (lineaSinComentarios.toLowerCase().contains(" equ ")) {
                        // Es una variable
                        String nombreVariable = lineaSinComentarios.substring(0, lineaSinComentarios.indexOf(" equ ")).trim().toLowerCase();
                        String valor = lineaSinComentarios.substring( lineaSinComentarios.toLowerCase().indexOf(" equ ") + 5).trim();
                        variables.put(nombreVariable, valor);
                    }
                    else if(!lineaSinComentarios.matches("\s")){
                        // Es una etiqueta
                        etiquetas.put(lineaSinComentarios, direccionActualH);
                        hacerSegundaVuelta = true;
                    }
                    else {
                        lanzarError(contador, lineaDetalle.lineaOriginal, "009 INSTRUCCIÓN CARECE DE ALMENOS UN ESPACIO RELATIVO AL MARGEN");
                    }
                    lineas.add(lineaDetalle);
                    continue;
                }
                else {
                    lineaSinComentarios = lineaSinComentarios.trim();
                    int primerEspacio = lineaSinComentarios.indexOf(" ");
                    if (primerEspacio == -1) {
                        primerEspacio = lineaSinComentarios.length();
                    }
                    mnemonico = lineaSinComentarios.substring(0, primerEspacio).toLowerCase();
                    if(primerEspacio == lineaSinComentarios.length())
                        lineaDetalle.operando = "";
                    else
                        lineaDetalle.operando = lineaSinComentarios.substring(primerEspacio + 1).trim();
                    if (esDirectiva(mnemonico)) {
                        switch (mnemonico) {
                            case "org":
                                direccionActualH = Integer.parseInt(lineaDetalle.operando, 16);
                                aumentarDireccion = false;
                                break;
                    
                            case "fcb":
                                // TODO: PENDIENTE FCB
                            break;
                    
                            case "end":
                                aumentarDireccion = false;
                                finDelPrograma = true;
                            break;
                        }
                    }
                    else if (!setInstrucciones.containsKey(mnemonico)) {
                        lanzarError(contador, lineaDetalle.lineaOriginal, "004 MNEMÓNICO INEXISTENTE");
                    }
                    else {
                        // Es una instrucción
                        Instruccion instruccion = setInstrucciones.get(mnemonico);
                        // Rel
                        if(instruccion.codigos.containsKey(Instruccion.TipoInstruccion.REL)){
                            hacerSegundaVuelta = true;
                            lineaDetalle.pendiente = Linea.Tarea.CALC_SALTO;
                            lineaDetalle.etiqueta = lineaDetalle.operando;
                            lineaDetalle.hexInst = instruccion.codigos.get(Instruccion.TipoInstruccion.REL).codigo;
                        }
                        else {
                            // Inherente
                            if(instruccion.codigos.containsKey(Instruccion.TipoInstruccion.INH)){
                                if(lineaDetalle.operando.isEmpty()){
                                    lineaDetalle.hexInst = instruccion.codigos.get(Instruccion.TipoInstruccion.INH).codigo;
                                }
                                else {
                                    lanzarError(contador, lineaDetalle.lineaOriginal, "006 INSTRUCCIÓN NO LLEVA OPERANDO(S)");
                                }
                            }
                            else{
                                // Inmediato
                                if (lineaDetalle.operando.isEmpty()) {
                                    lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");

                                }
                                if((lineaDetalle.operando.substring(0,1).contains("#"))
                                    && instruccion.codigos.containsKey(Instruccion.TipoInstruccion.INM)){
                                    String op = lineaDetalle.operando.substring(1);
                                    if (variables.containsKey(op)) {
                                        op = variables.get(op);
                                    }
                                    lineaDetalle.hexInst = instruccion.codigos.get(Instruccion.TipoInstruccion.INM).codigo;
                                    if(op.startsWith("$")){
                                        lineaDetalle.hexOp = op.substring(1);   
                                    }
                                    else if(op.startsWith("'")){
                                            lineaDetalle.hexOp = Linea.decToHex((int) op.charAt(1), 1);
                                    }
                                    else if(op.matches("[0-9]+")) {
                                        lineaDetalle.hexOp = Linea.decToHex(Integer.parseInt(op), 1);
                                    }
                                    else{
                                        lanzarError(contador, lineaDetalle.lineaOriginal, "001 CONSTANTE INEXISTENTE");    
                                    }
                                }
                                else{
                                    // Indexado
                                    if (lineaDetalle.operando.isEmpty())
                                        lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                    if((lineaDetalle.operando.toLowerCase().contains(",x") || lineaDetalle.operando.toLowerCase().contains(",y"))
                                    && (instruccion.codigos.containsKey(Instruccion.TipoInstruccion.INDX) || instruccion.codigos.containsKey(Instruccion.TipoInstruccion.INDY))){
                                        String op = lineaDetalle.operando.toLowerCase();
                                        boolean indX = lineaDetalle.operando.contains(",x");
                                        int posicionIndexado = indX ? lineaDetalle.operando.indexOf(",x") : lineaDetalle.operando.indexOf(",y");
                                        op = op.substring(0, posicionIndexado);
                                        if (op.isEmpty())
                                            lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                        if (variables.containsKey(op)) {
                                            op = variables.get(op);
                                        }
                                        lineaDetalle.hexInst = instruccion.codigos.get(indX ? Instruccion.TipoInstruccion.INDX : Instruccion.TipoInstruccion.INDY).codigo;
                                        if(op.startsWith("$")){
                                            lineaDetalle.hexOp = op.substring(1);    
                                        }
                                        else if(op.startsWith("'") ){
                                            lineaDetalle.hexOp = Linea.decToHex((int) op.charAt(1), 1);
                                        }
                                        else if(op.matches("[0-9]+")) {
                                            lineaDetalle.hexOp = Linea.decToHex(Integer.parseInt(op), 1);
                                        }
                                        else {
                                            lanzarError(contador, lineaDetalle.lineaOriginal, "002 VARIABLE INEXISTENTE");
                                        }
                                        /// Excepciones: bset, bclr, brset, brclr
                                        if (instruccionesEspeciales.contains(instruccion.nmemonico)) {
                                            String aux = lineaDetalle.operando.toLowerCase().substring(posicionIndexado + 2).trim();
                                            if (!aux.substring(0,2).contains(",#"))
                                                lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                            int posEspacio = aux.indexOf(" ");
                                            if (posEspacio == -1)
                                                posEspacio = aux.length();
                                            String op2 = aux.substring(2, posEspacio).trim();
                                            String etiqueta = aux.substring(posEspacio).trim();
                                            lineaDetalle.hexInst = instruccion.codigos.get(indX ? Instruccion.TipoInstruccion.INDX : Instruccion.TipoInstruccion.INDY).codigo;
                                            if(op2.startsWith("$")){
                                                lineaDetalle.hexOp += op2.substring(1);    
                                            }
                                            else if(op2.startsWith("'") ){
                                                lineaDetalle.hexOp += Linea.decToHex((int) op2.charAt(1), 1);
                                            }
                                            else if(op2.matches("[0-9]+")) {
                                                lineaDetalle.hexOp += Linea.decToHex(Integer.parseInt(op2), 1);
                                            }
                                            else {
                                                lanzarError(contador, lineaDetalle.lineaOriginal, "001 CONSTANTE INEXISTENTE");
                                            }
                                            if (etiqueta.isEmpty()) {
                                                // Sin r
                                                if (!mnemonico.equals("bset") && !mnemonico.equals("bclr"))
                                                    lanzarError(contador, lineaDetalle.lineaOriginal, "010 MUCHOS OPERANDOS");
                                            }
                                            else {
                                                // Con r
                                                if (mnemonico.equals("bset") || mnemonico.equals("bclr"))
                                                    lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                                if (!etiquetas.containsKey(etiqueta))
                                                    lanzarError(contador, lineaDetalle.lineaOriginal, "003 ETIQUETA INEXISTENTE");
                                                hacerSegundaVuelta = true;
                                                lineaDetalle.pendiente = Linea.Tarea.CALC_SALTO;
                                                lineaDetalle.etiqueta = etiqueta;
                                            }
                                        }
                                    }
                                    else{
                                        // Directo o extendido
                                        String op = lineaDetalle.operando, especial = "";
                                        int posComa = op.indexOf(",#");
                                        boolean esEspecial = posComa != -1;
                                        if (esEspecial) {
                                            especial = op.substring(op.indexOf(",#") + 2).trim();
                                            op = op.substring(0, op.indexOf(",")).trim();
                                        }
                                        if (op.isEmpty())
                                            lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                        if (variables.containsKey(op)) {
                                            op = variables.get(op);
                                        }
                                        int opNum = -1;
                                        if(op.startsWith("$")){
                                            op = op.substring(1);
                                            opNum = Linea.hexToDec(op);
                                        }
                                        else if(op.startsWith("'")){
                                            opNum = (int) op.charAt(1);
                                        }
                                        else if(op.matches("[0-9]+")) {
                                            opNum = Integer.parseInt(op);
                                        }
                                        else {
                                            lanzarError(contador, lineaDetalle.lineaOriginal, "002 VARIABLE INEXISTENTE");
                                        }
                                        if(opNum >= 0 && opNum < 256){
                                            if(instruccion.codigos.containsKey(Instruccion.TipoInstruccion.DIR)){
                                                lineaDetalle.hexInst = instruccion.codigos.get(Instruccion.TipoInstruccion.DIR).codigo;
                                                lineaDetalle.hexOp += Linea.decToHex(opNum, 1);
                                                /// Excepciones: bset, bclr, brset, brclr
                                                if (esEspecial) {
                                                    if (!instruccionesEspeciales.contains(instruccion.nmemonico)) {
                                                        lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                                    }
                                                    String aux = lineaDetalle.operando.toLowerCase().substring(posComa).trim();
                                                    if (!aux.substring(0,2).contains(",#")) {
                                                        lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                                    }
                                                    int posEspacio = aux.indexOf(" ");
                                                    if (posEspacio == -1)
                                                        posEspacio = aux.length();
                                                    String op2 = aux.substring(2, posEspacio).trim();
                                                    String etiqueta = aux.substring(posEspacio).trim();
                                                    lineaDetalle.hexInst = instruccion.codigos.get(Instruccion.TipoInstruccion.DIR).codigo;
                                                    if(op2.startsWith("$")){
                                                        lineaDetalle.hexOp += op2.substring(1);    
                                                    }
                                                    else if(op2.startsWith("'") ){
                                                        lineaDetalle.hexOp += Linea.decToHex((int) op2.charAt(1), 1);
                                                    }
                                                    else if(op2.matches("[0-9]+")) {
                                                        lineaDetalle.hexOp += Linea.decToHex(Integer.parseInt(op2), 1);
                                                    }
                                                    else {
                                                        lanzarError(contador, lineaDetalle.lineaOriginal, "002 VARIABLE INEXISTENTE");
                                                    }
                                                    if (etiqueta.isEmpty()) {
                                                        // Sin r
                                                        if (!mnemonico.equals("bset") && !mnemonico.equals("bclr")) {
                                                            lanzarError(contador, lineaDetalle.lineaOriginal, "010 MUCHOS OPERANDOS");
                                                        }
                                                    }
                                                    else {
                                                        // Con r
                                                        if (mnemonico.equals("bset") || mnemonico.equals("bclr")) {
                                                            lanzarError(contador, lineaDetalle.lineaOriginal, "005 INSTRUCCIÓN CARECE DE OPERANDOS");
                                                        }
                                                        if (!etiquetas.containsKey(etiqueta)) {
                                                            lanzarError(contador, lineaDetalle.lineaOriginal, "003 ETIQUETA INEXISTENTE");
                                                        }
                                                        hacerSegundaVuelta = true;
                                                        lineaDetalle.pendiente = Linea.Tarea.CALC_SALTO;
                                                        lineaDetalle.etiqueta = etiqueta;
                                                    }
                                                }
                                            }
                                            else{
                                                lanzarError(contador, lineaDetalle.lineaOriginal, "007 MAGNITUD DE OPERADO ERRÓNEA");
                                            }
                                        }
                                        else {
                                            if(instruccion.codigos.containsKey(Instruccion.TipoInstruccion.EXT) && opNum >= 256 && opNum < 65536){
                                                lineaDetalle.hexInst = instruccion.codigos.get(Instruccion.TipoInstruccion.EXT).codigo;
                                                lineaDetalle.hexOp += Linea.decToHex(opNum, 2);
                                            }
                                            else{
                                                lanzarError(contador, lineaDetalle.lineaOriginal, "007 MAGNITUD DE OPERADO ERRÓNEA");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(aumentarDireccion){
                        lineaDetalle.direccion = direccionActualH;
                        direccionActualH = direccionActualH + lineaDetalle.getHexadecimal().length()/2;
                    }
                    else{
                        aumentarDireccion = true;
                    }
                    lineas.add(lineaDetalle);
                }
                lineaDetalle.hexInst = lineaDetalle.hexInst.toUpperCase();
                lineaDetalle.hexOp = lineaDetalle.hexOp.toUpperCase();
            } while (lector.hasNextLine() && !finDelPrograma);
            if (hacerSegundaVuelta) {
                for (int i = 0; i < lineas.size(); i++){
                    Linea lineaActual = lineas.get(i);
                    if (lineaActual.pendiente == null)
                        continue;
                    if (lineaActual.pendiente == Linea.Tarea.CALC_SALTO) {
                        if(!etiquetas.containsKey(lineaActual.etiqueta)) {
                            lanzarError(contador, lineaDetalle.lineaOriginal, "003 ETIQUETA INEXISTENTE");
                        }
                        int saltos = etiquetas.get(lineaActual.etiqueta) - lineaActual.direccion - 2;
                        if (saltos < 128 && saltos > -129){
                            lineaActual.hexOp += Linea.decToHex(saltos, 1);
                        }
                        else{
                            lanzarError(contador, lineaDetalle.lineaOriginal, "008 SALTO RELATIVO MUY LEJANO");
                        }
                    }
                }
            }
            if(!finDelPrograma){
                lanzarError(contador, lineaDetalle.lineaOriginal, "010 NO SE ENCUENTRA END");
            }
            lector.close();

            crearArchivoListado(rutaArchivo, lineas);
            crearArchivoCodigoObjeto(rutaArchivo, lineas);
            crearArchivoListadoHTML(rutaArchivo, lineas);
            
            System.out.println("Programa exitosamente compilado.");
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
    }

    public static void lanzarError(int contador, String lineaOriginal, String mensaje) {
        System.out.println("Error en línea " + contador + " (" + mensaje + "): \n" + lineaOriginal);
        System.out.println("Compilación del programa fallida.");
        System.exit(0);
    }

    public static void crearArchivoListado (String rutaArchivo, List<Linea> lineas) {
        try {
            int indexComienzoNombre = rutaArchivo.lastIndexOf("\\");
            if (indexComienzoNombre == -1)
                indexComienzoNombre = 0;
            String carpeta = rutaArchivo.substring(0, indexComienzoNombre + 1);
            String nombreArchivo = rutaArchivo.substring(indexComienzoNombre + 1, rutaArchivo.lastIndexOf("."));
            FileWriter archivoListado = new FileWriter(carpeta + nombreArchivo + ".lst");
            if (lineas.size() == 0) {
                archivoListado.close();
                return;
            }
            for (Linea linea : lineas)
                archivoListado.write(linea.toString() + "\n");
            archivoListado.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void crearArchivoListadoHTML (String rutaArchivo, List<Linea> lineas) {
        try {
            int indexComienzoNombre = rutaArchivo.lastIndexOf("\\");
            if (indexComienzoNombre == -1)
                indexComienzoNombre = 0;
            String carpeta = rutaArchivo.substring(0, indexComienzoNombre + 1);
            String nombreArchivo = rutaArchivo.substring(indexComienzoNombre + 1, rutaArchivo.lastIndexOf("."));
            FileWriter archivoListado = new FileWriter(carpeta + nombreArchivo + ".lst.html");
            if (lineas.size() == 0) {
                archivoListado.close();
                return;
            }
            archivoListado.write("<html\n<head><title>Listado con colores (Punto extra)</title></head>\n");
            archivoListado.write("<body>\n");
            for (Linea linea : lineas)
                archivoListado.write(linea.toHTML() + "<br/>");
            archivoListado.write("</body>\n</html>");
            archivoListado.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void crearArchivoCodigoObjeto (String rutaArchivo, List<Linea> lineas) {
        try {
            int indexComienzoNombre = rutaArchivo.lastIndexOf("\\");
            if (indexComienzoNombre == -1)
                indexComienzoNombre = 0;
            String carpeta = rutaArchivo.substring(0, indexComienzoNombre + 1);
            String nombreArchivo = rutaArchivo.substring(indexComienzoNombre + 1, rutaArchivo.lastIndexOf("."));
            FileWriter archivoListado = new FileWriter(carpeta + nombreArchivo + ".s19");
            String hexadecimalTotal = "", cadena = "";
            if (lineas.size() == 0) {
                archivoListado.close();
                return;
            }
            int direccion = -1;
            for (Linea linea : lineas) {
                if (direccion == -1 && linea.direccion != -1)
                    direccion = linea.direccion;
                hexadecimalTotal += linea.getHexadecimal();
            }
            for (int i = 0; i < hexadecimalTotal.length(); i += 32) {
                cadena = "<" + Linea.decToHex(direccion, 2) + ">";
                for (int j = i; j < i + 32 && j < hexadecimalTotal.length() - 1; j += 2, direccion++)
                    cadena += " " + hexadecimalTotal.charAt(j) + hexadecimalTotal.charAt(j + 1);
                archivoListado.write(cadena + "\n");
                cadena = "";
            }
            archivoListado.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        cargarSetInstrucciones();
        System.out.print("Ruta o nombre del archivo:");
        Scanner sc = new Scanner(System.in);
        String archivoACompilar = sc.nextLine();
        compilarArchivo(archivoACompilar);
    }
}