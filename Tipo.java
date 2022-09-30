public class Tipo {
    public Instruccion.TipoInstruccion tipo;

    public boolean careceOperandos;

    public boolean acepta16Bits;

    public Tipo(Instruccion.TipoInstruccion tipo, boolean careceOperandos, boolean acepta16Bits) {
        this.tipo = tipo;
        this.careceOperandos = careceOperandos;
        this.acepta16Bits = acepta16Bits;
    }
}
