package com.acme.sica.aplicacion.visita;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import java.util.List;
public class ConsultarPersonalPresenteServicio {
    private final VisitaRepositorioPuerto  visitaRepositorio;
    public ConsultarPersonalPresenteServicio(VisitaRepositorioPuerto visitaRepositorio){
        this.visitaRepositorio = visitaRepositorio;

    }
    public List<Visita> ejecutar(String documentoFuncionario){
        if(documentoFuncionario == null || documentoFuncionario.trim().isEmpty()){
            throw new IllegalArgumentException("El documento del funcionario no puede estar vacio");
        }

        return visitaRepositorio.consultarVisitasActivasPorFuncionario(documentoFuncionario);
    }

}
