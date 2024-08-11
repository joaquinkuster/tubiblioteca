package com.tubiblioteca.controller.ABMMiembro;

import org.slf4j.LoggerFactory;

import com.tubiblioteca.App;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.VistasFXML;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.ResourceBundle;

import org.slf4j.Logger;

public class FormularioMiembroControlador implements Initializable {

    // Campos del formulario
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private ComboBox<TipoMiembro> cmbTipo;

    @FXML
    private Button btnNuevo;

    // Listas utilizadas
    private ObservableList<TipoMiembro> tipos = FXCollections.observableArrayList();

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(FormularioMiembroControlador.class);

    private Miembro miembro;
    private MiembroServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos los combos del formulario
        inicializarCombosFormulario();

        // Estblecemos en nulo al miembro
        miembro = null;
    }

    public void inicializarCombosFormulario() {
        // Cargamos lista y combo de estados
        tipos.clear();
        tipos.addAll(TipoMiembro.values());
        cmbTipo.setItems(tipos);
    }

    @FXML
    private void nuevo() {
        // Limpiamos el formulario
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        cmbTipo.setValue(null);
        txtContrasena.clear();
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo miembro auxiliar
            // Obtenemos la información proporcionada
            String dni = txtDni.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            TipoMiembro tipo = cmbTipo.getValue();
            String contrasena = txtContrasena.getText().trim();
            Miembro aux = new Miembro(dni, nombre, apellido, tipo, contrasena);

            if (miembro == null) { 
                if (servicio.buscarPorId(dni) == null) {
                    
                    miembro = aux;
                    
                    servicio.insertar(miembro);

                    Alerta.mostrarMensaje(false, "Info", "Se ha agregado el miembro correctamente!");
                } else {
                    throw new IllegalArgumentException("El DNI ingresado ya está en uso. Por favor, ingrese otro DNI.");
                }
            } else {

                miembro.setNombre(nombre);
                miembro.setApellido(apellido);
                miembro.setTipo(tipo);
                miembro.setClave(contrasena);

                servicio.modificar(miembro);

                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el miembro correctamente!");

                //lista.setMiembro(miembro);


            }

            // Salimos del modal
            App.cerrarmodal(VistasFXML.FormularioMiembro);
        } catch (Exception e) {
            Alerta.mostrarMensaje(true, "Error", e.getMessage());
        }
    }

    private void autocompletar(){
        txtDni.setText(miembro.getDni());
        txtNombre.setText(miembro.getNombre());
        txtApellido.setText(miembro.getApellido());
        cmbTipo.setValue(miembro.getTipo());
        txtContrasena.setText(miembro.getClave());
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;

        if (miembro != null) {
            autocompletar();
            txtDni.setDisable(true);
            btnNuevo.setDisable(true);

        }
    }

    public Miembro getMiembro() {
        return miembro;
    }

    public void setServicio(MiembroServicio servicio) {
        this.servicio = servicio;
    }

}
