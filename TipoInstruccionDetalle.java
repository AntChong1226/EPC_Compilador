public class TipoInstruccionDetalle {
    public String codigo;
    public int bytesAOcupar;

    public TipoInstruccionDetalle(String codigo, String bytesAOcupar) {
        this.codigo = codigo;
        try {
            this.bytesAOcupar = Integer.parseInt(bytesAOcupar);
        } catch (Exception e) {
            this.bytesAOcupar = 0;
        }
    }
}
