package modelos;

/**
 * @author Juan Antonio Martín Gil
 */
public class Cliente {
    String dni,nombre,email;
    Integer telefono;
    Boolean socio;
    
    public Cliente(String dni, String nombre, String email, Integer telefono, Boolean socio){
        this.dni=dni;
        this.nombre=nombre;
        this.email=email;
        this.telefono=telefono;
        this.socio=socio;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public Boolean getSocio() {
        return socio;
    }

    public void setSocio(Boolean socio) {
        this.socio = socio;
    }
}
