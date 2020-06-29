package es.uji.ei1027.Mayorescasa.dao;
import es.uji.ei1027.Mayorescasa.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class EmpresaDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate=new JdbcTemplate(dataSource);
    }
    //AÑADIMOS Empresa
    public void addEmpresa(Empresa empresa) {
        jdbcTemplate.update("INSERT INTO empresa VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                empresa.getNombre(),empresa.getCif(),empresa.getContacto(),
                empresa.getUsuario(),empresa.getContraseña(),empresa.getEmail(),empresa.getDireccion(),
                empresa.getTelefono(),empresa.getTiposervicio(),empresa.getCont_nif(),empresa.getCont_tlf(),empresa.getCont_mail());
    }

    //BORRAMOS empresa
    public void deleteEmpresa(String empresa) {
        jdbcTemplate.update("DELETE FROM empresa WHERE usuario=?",empresa);
    }

    //ACTUALIZAMOS empresa (No se actualiza usuario y dni por claves primaria)
    public void updateEmpresa(Empresa empresa){
        jdbcTemplate.update("UPDATE empresa SET nombre=?, cif=?, contacto=?, contraseña=?, " +
                        "email=?, direccion=?, telefono=? ,tiposervicio=?,cont_nif=?,cont_tlf=?,cont_mail=?" +
                        "WHERE usuario=? ",
                empresa.getNombre(),empresa.getCif(), empresa.getContacto(),
                empresa.getContraseña(), empresa.getEmail(),empresa.getDireccion(),
                empresa.getTelefono(),empresa.getTiposervicio(),empresa.getCont_nif(),
                empresa.getCont_tlf(),empresa.getCont_mail(),  empresa.getUsuario());
    }

    public Empresa getEmpresa (String usuario ){
        try{
            return jdbcTemplate.queryForObject("SELECT * FROM empresa WHERE usuario=?", new EmpresaRowMapper(),usuario);
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    //Busca Contrato
    public List<Contrato> buscaContrato(String empresa){
        try {
            return jdbcTemplate.query("SELECT * FROM contrato WHERE empresa=?", new ContratoRowMapper(), empresa);
        } catch (EmptyResultDataAccessException e){
            return  new ArrayList<Contrato>();
        }
    }

    //Busca Contrato
    public List<Contrato> getContratoPasado(String empresa, Date fecha){
        try {
            return jdbcTemplate.query("SELECT * FROM contrato WHERE empresa=? AND fechafinal<?", new ContratoRowMapper(), empresa, fecha);
        } catch (EmptyResultDataAccessException e){
            return  new ArrayList<Contrato>();
        }
    }

    //Busca Contrato
    public List<Contrato> getContratoActivo(String empresa, Date fecha){
        try {
            return jdbcTemplate.query("SELECT * FROM contrato WHERE empresa=? AND fechafinal>=?", new ContratoRowMapper(), empresa, fecha);
        } catch (EmptyResultDataAccessException e){
            return  new ArrayList<Contrato>();
        }
    }

    //AÑADIMOS Usuario
    public void addUsuario(String nombre, String usuario, String contraseña, String email, String direccion,
    String dni, String genero, String nacimiento, int telefono, String tipoUsuario, String tipoDieta) {
        jdbcTemplate.update("INSERT INTO Usuario VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                nombre, usuario, contraseña, email, direccion, dni, genero, nacimiento, telefono, tipoUsuario, tipoDieta );
    }

    //BORRAMOS Usuario
    public void deleteUsuario(String usuario) {
        jdbcTemplate.update("DELETE FROM Usuario WHERE usuario=?", usuario);
    }

    //Existe
    public Empresa existe (String usuario, String nombre, String cif){
        try{
            return  jdbcTemplate.queryForObject("SELECT * FROM empresa WHERE usuario=? OR nombre=? OR cif=?",
                    new EmpresaRowMapper(),usuario, nombre, cif);
        } catch ( EmptyResultDataAccessException e) { return null; }
    }

    //LISTAMOS empresa
    public List<Empresa> getEmpresas() {
        try{
            return jdbcTemplate.query("SELECT * FROM empresa", new
                    EmpresaRowMapper());
        }
        catch (EmptyResultDataAccessException e){
            return  new ArrayList<Empresa>();
        }
    }
    //Busca Servicio

    public List<Servicio> getServiciosActivo(String nombre){
        try {
            return jdbcTemplate.query("SELECT peticion.beneficiario, usuario.dni, peticion.tiposervicio, " +
                    "beneficiario.tipodieta, usuario.direccion, usuario.telefono, peticion.comentarios FROM peticion " +
                    "JOIN beneficiario ON peticion.dni_ben = beneficiario.dni JOIN usuario USING(dni) " +
                    "WHERE peticion.empresa=?;", new ServicioRowMapper(), nombre);
        } catch (EmptyResultDataAccessException e){
            return new ArrayList<Servicio>();
        }
    }


}
