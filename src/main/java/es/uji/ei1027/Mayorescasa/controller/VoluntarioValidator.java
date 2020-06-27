package es.uji.ei1027.Mayorescasa.controller;


import es.uji.ei1027.Mayorescasa.model.Usuario;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class VoluntarioValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Usuario.class.equals(cls);
        // o, si volguérem tractar també les subclasses:
        // return Usuario.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Usuario usuario = (Usuario)obj;
        if (usuario.getNombre().length()<12)
            errors.rejectValue("nombre", "obligatorio",
                    " Introduce un valor valido");
        if (usuario.getNacimiento()>2002)
            errors.rejectValue("nacimiento", "obligatorio",
                    " Mayor de 18 años (o cumplirlos este año)");
        if (usuario.getDni().length()!=9)
            errors.rejectValue("dni", "obligatorio",
                    " Introduce un valor valido");
        if (usuario.getTelefono()<600000000 || usuario.getTelefono()>750000000)
            errors.rejectValue("telefono", "obligatorio",
                    " Introduce un valor valido");
        if (usuario.getUsuario().trim().equals(""))
            errors.rejectValue("usuario", "obligatorio",
                    " Hay que introducir un valor");
        if (usuario.getContraseña().trim().equals(""))
            errors.rejectValue("contraseña", "obligatorio",
                    " Hay que introducir un valor");
        if (usuario.getEmail().trim().equals(""))
            errors.rejectValue("email", "obligatorio",
                    " Hay que introducir un valor");
        if (usuario.getGenero().length()>9)
            errors.rejectValue("genero", "obligatorio",
                    " Selecciona un genero correcto");
    }
}
