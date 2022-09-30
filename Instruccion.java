import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Instruccion {
    enum TipoInstruccion {
        INM,
        DIR,
        INDX,
        INDY,
        EXT,
        INH,
        REL
    }

    public static LinkedHashMap<TipoInstruccion, Tipo> tipos;
    static {
        tipos.put(TipoInstruccion.INM, new Tipo(TipoInstruccion.INM, false, true));
        tipos.put(TipoInstruccion.DIR, new Tipo(TipoInstruccion.DIR, false, false));
        tipos.put(TipoInstruccion.INDX, new Tipo(TipoInstruccion.INDX, false, false));
        tipos.put(TipoInstruccion.INDY, new Tipo(TipoInstruccion.INDY, false, false));
        tipos.put(TipoInstruccion.EXT, new Tipo(TipoInstruccion.EXT, false, true));
        tipos.put(TipoInstruccion.INH, new Tipo(TipoInstruccion.INH, true, false));
        tipos.put(TipoInstruccion.REL, new Tipo(TipoInstruccion.REL, false, false));
    }

    public String nmemonico;

    public LinkedHashMap<TipoInstruccion, String> codigos;
    
    public Instruccion(String nmemonico) {
        this.nmemonico = nmemonico;
        this.codigos = new LinkedHashMap<>();
    }

    @Override
    public String toString() {
        String codigos = "";
        for (TipoInstruccion tipo : this.codigos.keySet()) {
            codigos += tipo + "-" + this.codigos.get(tipo) + ",";
        }
        return "Instroducción: " + nmemonico + " " + codigos;
    }
}
