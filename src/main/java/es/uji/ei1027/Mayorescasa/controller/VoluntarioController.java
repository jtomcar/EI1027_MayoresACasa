package es.uji.ei1027.Mayorescasa.controller;

import es.uji.ei1027.Mayorescasa.dao.DisponibilidadDao;
import es.uji.ei1027.Mayorescasa.dao.UsuarioDao;
import es.uji.ei1027.Mayorescasa.model.Disponibilidad;
import es.uji.ei1027.Mayorescasa.model.TempUsuarioComentario;
import es.uji.ei1027.Mayorescasa.model.Usuario;
import es.uji.ei1027.Mayorescasa.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/voluntario")
public class VoluntarioController {
    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    public void setUsuarioDao(UsuarioDao usuarioDao) {

        this.usuarioDao=usuarioDao;
    }
    @Autowired
    DisponibilidadDao disponibilidadDao;

    // Operaciones: Crear, listar, actualizar, borrar

    @RequestMapping("/list")
    public String listvoluntarios(Model model) {

        List<Usuario> listUser =  usuarioDao.getUsuariosVoluntarios();
        Map<Usuario, String> map = new HashMap();
        for (Usuario user:listUser){
            map.put(user, usuarioDao.getVoluntario(user.getDni()).getEstado());
        }
        model.addAttribute("map",map);

        return "voluntario/list";
    }
    //Llamada de la peticion add
    @RequestMapping(value="/add")
    public String addvoluntario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("voluntario", new Voluntario());
        List<String> generoList = new ArrayList<>();
        generoList.add("Femenino");
        generoList.add("Masculino");
        model.addAttribute("generoList", generoList);
        return "voluntario/add";
    }

    @RequestMapping(value = "/rechazar/{dni}")
    public String rechazarVoluntario(@PathVariable String dni){
        Voluntario voluntario = usuarioDao.getVoluntario(dni);
        if(voluntario.getEstado().equals("Pendiente")){
            voluntario.setEstado("Rechazado");
            usuarioDao.updateEstadoVoluntario(voluntario);
            System.out.println("");
            System.out.println("");
            System.out.println("EMAIL ENVIADO");
            System.out.println("*************************************************************************");
            System.out.println("Correo destinatario: " +usuarioDao.getUsuarioDni(dni).getEmail()+ "\n"+
                    "Correo del que envia: mayoresEnCasa@gva.es\n" +
                    "Estimado/a señor/a "+usuarioDao.getUsuarioDni(dni).getNombre()+ " desde mayores en casa le comunicamos que tu solicitud para ser voluntario ha sido "+ voluntario.getEstado()+".\n" +
                    "Gracias por su colaboración.\n");
            System.out.println("*************************************************************************");
        }

        return "redirect:../list";
    }

    @RequestMapping(value = "/aceptar/{dni}")
    public String aceptarVoluntario(@PathVariable String dni){

        Voluntario voluntario = usuarioDao.getVoluntario(dni);

        if(voluntario.getEstado().equals("Pendiente")){
            voluntario.setEstado("Aceptado");
            usuarioDao.updateEstadoVoluntario(voluntario);
            System.out.println("");
            System.out.println("");
            System.out.println("EMAIL ENVIADO");
            System.out.println("*************************************************************************");
            System.out.println("Correo destinatario: " +usuarioDao.getUsuarioDni(dni).getEmail()+ "\n"+
                    "Correo del que envia: mayoresEnCasa@gva.es\n" +
                    "Estimado/a señor/a "+usuarioDao.getUsuarioDni(dni).getNombre()+ " desde mayores en casa le comunicamos que tu solicitud para ser voluntario ha sido "+ voluntario.getEstado()+".\n" +
                    "Gracias por su colaboración.\n");
        }

        return "redirect:../list";
    }

    @RequestMapping(value ="/delete/{dni}")
    public String eliminarVoluntario(@PathVariable String dni){
        usuarioDao.deleteUsuario(dni);
        return "redirect:../list";
    }


    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("voluntario") Voluntario voluntario,
                                   BindingResult bindingResult) {
        VoluntarioValidator nadadorValidator = new VoluntarioValidator();
        nadadorValidator.validate(voluntario, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println("Error al añadir el voluntario");
            return "voluntario/add";
        }
        voluntario.setTipoUsuario("Voluntario");
        usuarioDao.addUsuario(voluntario);


        usuarioDao.addVoluntario(voluntario.getDni(),voluntario.getHobbies(),voluntario.getDias_semana());
        return "redirect:../login";
    }

    @RequestMapping("/index")
    public String index(HttpSession session, Model model) {

        return "voluntario/index";
    }
    @RequestMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario user=(Usuario) session.getAttribute("user");
        Voluntario vol=usuarioDao.getVoluntario(user.getDni());
        String visible=vol.getVisible();
        String estado=vol.getEstado();

        model.addAttribute("visibilidad",visible);
        model.addAttribute("estado",estado);
        return "voluntario/perfil";
    }


    @RequestMapping("/solicitar")
    public String solicitar(Model model,HttpSession session) {
        Usuario user=(Usuario) session.getAttribute("user");
        List<Voluntario> voluntariosDisponibles=usuarioDao.getVoluntariosVisibles();
        List<Disponibilidad> asignados= disponibilidadDao.consultaDisponibilidad(user.getDni());
        List<TempUsuarioComentario> listaTemporal= new ArrayList<>();

        //si ya tiene uno de los voluntarios asignado, y está aceptada, lo elimina de los disponibles.
        if (asignados.size()>0) {
            for(Disponibilidad dis:asignados) {



                    for(Voluntario vol:voluntariosDisponibles) {
                        if ( vol.getDni().equals(dis.getUsuario_vol())) {
                            voluntariosDisponibles.remove(vol);
                            break;//solo hace el break en el for de dentro
                        }
                    }


                //si está aceptada, la añado a la lista de asignados
                if (dis.getEstado().equals("Aceptada")){
                    TempUsuarioComentario temporal = new TempUsuarioComentario();
                    temporal.setUsuario(usuarioDao.getUsuarioDni(dis.getUsuario_vol()));
                    temporal.setComentario(disponibilidadDao.getComentario(dis.getUsuario_vol()));
                    listaTemporal.add(temporal);
                }

            }


        }
        if(listaTemporal.size()>0){
            model.addAttribute("temp",listaTemporal);
        }
        if(voluntariosDisponibles.size()>0){
            model.addAttribute("voluntarios", voluntariosDisponibles);
        }

        return "voluntario/solicitar";
    }


    @RequestMapping(value="/solicitar", method= RequestMethod.POST)
    public String setDisponibildad(@ModelAttribute("dniV") String dniV,@ModelAttribute("comment") String comment, HttpSession session,Model model){

        Usuario user= (Usuario) session.getAttribute("user");
        List<TempUsuarioComentario> listaTemporal= new ArrayList<>();
        List<Voluntario> voluntariosDisponibles=usuarioDao.getVoluntariosVisibles();

        //añado la relacion entre ben y vol
        Disponibilidad disponibilidad=new Disponibilidad();
        disponibilidad.setUsuario_ben(user.getDni());
        disponibilidad.setUsuario_vol(dniV);
        Date fecha = new Date();
        disponibilidad.setFechainicial(fecha);
        disponibilidad.setComentario(comment);
        disponibilidad.setEstado("Pendiente");
        disponibilidadDao.addDisponibilidad(disponibilidad);

        //recojo los voluntarios asignados al beneficiario
        List<Disponibilidad> asignados= disponibilidadDao.consultaDisponibilidad(user.getDni());
        session.setAttribute("volAsignados",asignados);



        //si ya tiene uno de los voluntarios asignado, lo elimina de los disponibles.
        for(Disponibilidad dis:asignados) {


                for(Voluntario vol:voluntariosDisponibles) {
                    if ( vol.getDni().equals(dis.getUsuario_vol())) {
                        voluntariosDisponibles.remove(vol);
                        break;//solo hace el break en el for de dentro
                    }
                }


            //si está aceptada, la añado a la lista de asignados
            if (dis.getEstado().equals("Aceptada")){
                TempUsuarioComentario temporal = new TempUsuarioComentario();
                temporal.setUsuario(usuarioDao.getUsuarioDni(dis.getUsuario_vol()));
                temporal.setComentario(disponibilidadDao.getComentario(dis.getUsuario_vol()));
                listaTemporal.add(temporal);
            }
        }
        if(listaTemporal.size()>0){
            model.addAttribute("temp",listaTemporal);
        }

        if(voluntariosDisponibles.size()>0){
            model.addAttribute("voluntarios", voluntariosDisponibles);
        }
        return "voluntario/solicitar";
    }
    @RequestMapping(value="/rechazarBen/{dni}", method = RequestMethod.GET)
    public String rechazarBeneficiario( @PathVariable("dni") String dniBen, HttpSession session) {

            Usuario user=(Usuario)  session.getAttribute("user");
            Disponibilidad dis=disponibilidadDao.getDisponibilidad(dniBen,user.getDni());

            //si ya estaba aceptada, se finaliza
            if(dis.getEstado().equals("Aceptada")){
                dis.setEstado("Finalizada");
                Date fecha = new Date();
                dis.setFechafinal(fecha);
                disponibilidadDao.finalizarDis(dis);
            }else if(dis.getEstado().equals("Pendiente")){
                dis.setEstado("Rechazada");
                disponibilidadDao.updateEstado(dis);
            }
            System.out.println("");
            System.out.println("");
            System.out.println("EMAIL ENVIADO");
            System.out.println("*************************************************************************");
            System.out.println("Correo destinatario:"+usuarioDao.getUsuarioDni(dniBen).getEmail()+"\n" +
                    "Correo del que envia: mayoresEnCasa@gva.es\n" +
                    "Don/Doña "+usuarioDao.getUsuarioDni(dniBen).getNombre()+", lamentamos comunicarle que el voluntario/a"+user.getNombre()+" por motivos personales no podrá seguir prestano servicio. \n" +
                    "Esperemos que esto no le cause problemas y pueda encontrar otro voluntario rápido con nuestra aplicación");


        return "redirect:../beneficiarios";
    }

    @RequestMapping(value="/aceptarBen/{dni}", method = RequestMethod.GET)
    public String aceptarBeneficiario( @PathVariable("dni") String dniBen,HttpSession session, Model model) {

        Usuario user=(Usuario)  session.getAttribute("user");
        Disponibilidad dis=disponibilidadDao.getDisponibilidad(dniBen,user.getDni());
        //si ya está finaliza o rechaza, no puedes volver a aceptarla
        if(dis.getEstado().equals("Pendiente")){
            dis.setEstado("Aceptada");
            disponibilidadDao.updateEstado(dis);

        }
        System.out.println("");
        System.out.println("");
        System.out.println("EMAIL ENVIADO");
        System.out.println("*************************************************************************");
        System.out.println("Correo destinatario: "+usuarioDao.getUsuarioDni(dniBen).getEmail()+"\n" +
                "Correo del que envia: mayoresEnCasa@gva.es\n" +
                "Don/Doña "+usuarioDao.getUsuarioDni(dniBen).getNombre()+", le confirmamos que el voluntario que solicitaste, "+user.getNombre()+",  ha aceptado su petición.\n" +
                "Esperemos que sea de su agrado y podais construir una buena relación.");

        return "redirect:../beneficiarios";
    }
    @RequestMapping(value="/deleteBen/{dni}", method = RequestMethod.GET)
    public String deleteBeneficiario( @PathVariable("dni") String dniBen,HttpSession session) {

        Usuario user=(Usuario)  session.getAttribute("user");
        Disponibilidad dis=disponibilidadDao.getDisponibilidad(dniBen,user.getDni());

        disponibilidadDao.deleteDisponibilidad(dis);

        return "redirect:../beneficiarios";
    }


    @RequestMapping("/beneficiarios")
    public String beneficiarios(Model model,HttpSession session) {

        Usuario user=(Usuario) session.getAttribute("user");

        //lista de disponilidades de los beneficiarios
        List<Disponibilidad> listaDis= disponibilidadDao.consultaBeneficiarios(user.getDni());

        //mapa de key usuario y valor la disponibilidad
        Map<Usuario,Disponibilidad> map=new HashMap<>();

        for(Disponibilidad dis:listaDis){

            map.put(usuarioDao.getUsuarioDni(dis.getUsuario_ben()),dis);
        }

        model.addAttribute("map",map);
        return "voluntario/beneficiarios";
    }

    @RequestMapping("/quitarVisibilidad")
    public String quitarVisibilidad(Model model,HttpSession session) {

        Usuario user=(Usuario) session.getAttribute("user");

        usuarioDao.setVisibilidad(user.getDni(),"no");

        return "redirect:./perfil";
    }
    @RequestMapping("/ponerVisibilidad")
    public String ponerVisibilidad(Model model,HttpSession session) {

        Usuario user=(Usuario) session.getAttribute("user");
        usuarioDao.setVisibilidad(user.getDni(),"si");

        return "redirect:./perfil";
    }
    @RequestMapping("/baja")
    public String baja(Model model,HttpSession session) {

        Usuario user=(Usuario) session.getAttribute("user");
        Voluntario actual=usuarioDao.getVoluntario(user.getDni());
        actual.setEstado("Baja");
        usuarioDao.updateEstadoVoluntario(actual);

        usuarioDao.setVisibilidad(user.getDni(),"no");

        List<Disponibilidad> listaDisp=disponibilidadDao.consultaBeneficiarios(user.getDni());

        for(Disponibilidad disp:listaDisp){
            Date fecha=new Date();
            disp.setFechafinal(fecha);
            disp.setEstado("Finalizada");
            disponibilidadDao.finalizarDis(disp);
        }

        return "redirect:./perfil";
    }
    @RequestMapping("/alta")
    public String alta(Model model,HttpSession session) {

        Usuario user=(Usuario) session.getAttribute("user");
        Voluntario actual=usuarioDao.getVoluntario(user.getDni());
        actual.setEstado("Aceptado");
        usuarioDao.updateEstadoVoluntario(actual);

        usuarioDao.setVisibilidad(user.getDni(),"si");



        return "redirect:./perfil";
    }

    @RequestMapping( value="/rechazarVol/{dni}", method = RequestMethod.GET)
    public String finalizarConVoluntario( @PathVariable("dni") String dniVol,HttpSession session){
        Usuario user=(Usuario) session.getAttribute("user");
        Disponibilidad dis=disponibilidadDao.getDisponibilidad(user.getDni(),dniVol);
        Date fecha=new Date();
        dis.setFechafinal(fecha);
        dis.setEstado("Finalizada");
        disponibilidadDao.finalizarDis(dis);
        System.out.println("");
        System.out.println("");
        System.out.println("EMAIL ENVIADO");
        System.out.println("*************************************************************************");
        System.out.println("Correo destinatario:"+usuarioDao.getUsuarioDni(dniVol).getEmail()+"\n" +
                "Correo del que envia: mayoresEnCasa@gva.es\n" +
                "Estimado/a "+usuarioDao.getUsuarioDni(dniVol).getNombre()+", lamentamos comunicarle que el beneficiario/a "+user.getNombre()+", por motivos personales ha decido que no quiere que vuelvas.\n" +
                " Puedes consultar tus peticiones y elegir otro beneficiario al que ayudar. Gracias por la colaboración.\n");
        return "redirect:../solicitar";
    }

}
