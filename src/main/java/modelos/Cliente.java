package modelos;

/**
 * @author Juan Antonio Martín Gil
 */
public class Cliente {
    String dni,nombre,email,telefono;
    Boolean socio;
    
    public Cliente(String dni, String nombre, String telefono, String email, Boolean socio){
        this.dni=dni;
        this.nombre=nombre;
        this.telefono=telefono;
        this.email=email;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getSocio() {
        return socio;
    }

    public void setSocio(Boolean socio) {
        this.socio = socio;
    }
}
