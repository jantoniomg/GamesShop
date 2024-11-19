package modelos;

/**
 *
 * @author Juanan
 */
public class Juego {
    Integer id_juego, stock;
    Double precio;
    String nombre, plataforma,descripcion,imagen;
    
    public Juego(Integer id, String imagen, String nombre, String plataforma, String descripcion, Integer stock, Double precio){
        this.id_juego=id;
        this.imagen=imagen;
        this.nombre=nombre;
        this.plataforma=plataforma;
        this.descripcion=descripcion;
        this.stock=stock;
        this.precio=precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getId_juego() {
        return id_juego;
    }

    public void setId_juego(Integer id_juego) {
        this.id_juego = id_juego;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }
}
