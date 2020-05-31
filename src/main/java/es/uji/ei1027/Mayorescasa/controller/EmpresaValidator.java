package es.uji.ei1027.Mayorescasa.controller;

import es.uji.ei1027.Mayorescasa.model.Empresa;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EmpresaValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Empresa.class.equals(cls);
        // o, si volguérem tractar també les subclasses:
        // return Empresa.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Empresa empresa = (Empresa)obj;
        if (empresa.getNombre().trim().equals(""))
            errors.rejectValue("nombre", "obligatorio",
                    " Hay que introducir un valor");
        // Afegeix ací la validació per a Edat > 18 anys
        if (empresa.getUsuario().trim().equals(""))
            errors.rejectValue("usuario", "obligatorio",
                    " Hay que introducir un valor");
        if (empresa.getPersona_contacto().trim().equals(""))
            errors.rejectValue("persona_contacto", "obligatorio",
                    " Hay que introducir un valor");
        if (empresa.getTelefono()<600000000)
            errors.rejectValue("telefono", "obligatorio",
                    " Hay que introducir un valor");
        if (empresa.getCif().trim().equals(""))
            errors.rejectValue("cif", "obligatorio",
                    " Hay que introducir un valor");
        if (empresa.getContraseña().trim().equals(""))
            errors.rejectValue("contraseña", "obligatorio",
                    " Hay que introducir un valor");
        if (empresa.getEmail().trim().equals(""))
            errors.rejectValue("email", "obligatorio",
                    " Hay que introducir un valor");
        if (empresa.getTiposervicio().trim().equals(""))
            errors.rejectValue("tiposervicio", "obligatorio",
                    " Hay que introducir un valor");


    }
}