package modelos;

import java.util.Date;

/**
 * @author Juanan
 */
public class Compras{
  String dni;
  Integer idjuego;
  Date fecha;
  
  public Compras(Date fecha,String dni , Integer idjuego){
    this.fecha=fecha;
    this.dni=dni;
    this.idjuego=idjuego;
  }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Integer getIdjuego() {
        return idjuego;
    }

    public void setIdjuego(Integer idjuego) {
        this.idjuego = idjuego;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
